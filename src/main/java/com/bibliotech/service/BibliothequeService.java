package com.bibliotech.service;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.ConnectionFactory;
import com.bibliotech.dao.DatabaseConnection;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.exception.LivreDejaExistantException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Service métier de la bibliothèque.
 *
 * <p>Il regroupe :
 * <ul>
 *   <li>la persistance (validation métier puis appel des DAO, erreurs SQL traduites en
 *       {@link BibliothequeException}) ;</li>
 *   <li>des traitements en mémoire écrits deux fois, avec les Collections « classiques » puis
 *       avec l'API Stream, pour comparer les deux styles ;</li>
 *   <li>des traitements concurrents : parallélisme simple, accès limité par un {@link Semaphore},
 *       insertion concurrente protégée par {@code synchronized} et par la contrainte UNIQUE.</li>
 * </ul>
 */
public class BibliothequeService implements ActionBibliotheque {

    /** Nombre de consultations simultanées autorisées par défaut (permis du sémaphore). */
    public static final int CONSULTATIONS_SIMULTANEES_PAR_DEFAUT = 2;

    /**
     * Durée simulée d'un traitement « lourd ». MySQL répond en quelques millisecondes : sans cette
     * pause, l'effet du parallélisme et du sémaphore ne serait pas visible dans les démonstrations.
     */
    public static final Duration DUREE_TRAITEMENT_PAR_DEFAUT = Duration.ofSeconds(2);

    /** Code d'erreur MySQL : entrée en double sur une clé unique. */
    private static final int MYSQL_ENTREE_EN_DOUBLE = 1062;
    /** Code d'erreur MySQL : clé étrangère sans ligne parente (auteur inexistant). */
    private static final int MYSQL_CLE_ETRANGERE_INVALIDE = 1452;

    private final LivreDAO livreDAO;
    private final AuteurDAO auteurDAO;
    private final int consultationsSimultanees;
    private final Semaphore semaphore;
    private final Duration dureeTraitement;
    private final Consumer<String> journal;

    private final AtomicInteger consultationsEnCours = new AtomicInteger();
    private final AtomicInteger maxConsultationsObservees = new AtomicInteger();

    /** Service connecté à la base décrite par les variables d'environnement BIBLIOTECH_DB_*. */
    public BibliothequeService() {
        this(DatabaseConnection.depuisEnvironnement());
    }

    public BibliothequeService(ConnectionFactory connexions) {
        this(new LivreDAO(connexions), new AuteurDAO(connexions),
                CONSULTATIONS_SIMULTANEES_PAR_DEFAUT, DUREE_TRAITEMENT_PAR_DEFAUT, System.out::println);
    }

    /**
     * Constructeur complet (utilisé par les tests et les démonstrations).
     *
     * @param consultationsSimultanees nombre de permis du sémaphore (au moins 1)
     * @param dureeTraitement          pause simulant un traitement long ({@link Duration#ZERO} pour aucune)
     * @param journal                  destination des messages de suivi (par exemple {@code System.out::println})
     */
    public BibliothequeService(LivreDAO livreDAO, AuteurDAO auteurDAO, int consultationsSimultanees,
                               Duration dureeTraitement, Consumer<String> journal) {
        if (consultationsSimultanees < 1) {
            throw new IllegalArgumentException("Il faut au moins une consultation simultanée autorisée.");
        }
        if (dureeTraitement.isNegative()) {
            throw new IllegalArgumentException("La durée de traitement ne peut pas être négative.");
        }
        this.livreDAO = Objects.requireNonNull(livreDAO, "livreDAO");
        this.auteurDAO = Objects.requireNonNull(auteurDAO, "auteurDAO");
        this.consultationsSimultanees = consultationsSimultanees;
        // Sémaphore équitable : les threads en attente obtiennent un permis dans leur ordre d'arrivée.
        this.semaphore = new Semaphore(consultationsSimultanees, true);
        this.dureeTraitement = dureeTraitement;
        this.journal = Objects.requireNonNull(journal, "journal");
    }

    // =========================================================================
    // Persistance : auteurs
    // =========================================================================

    /** Enregistre un auteur. Règle métier : le nom est obligatoire. */
    @Override
    public void sauvegarderAuteur(Auteur auteur) throws BibliothequeException {
        if (auteur == null || auteur.getNom() == null || auteur.getNom().isBlank()) {
            throw new BibliothequeException("Erreur métier : le nom de l'auteur est obligatoire.");
        }
        try {
            auteurDAO.ajouterAuteur(auteur);
        } catch (SQLException e) {
            throw new BibliothequeException("Erreur technique lors de l'ajout de l'auteur.", e);
        }
    }

    public List<Auteur> chargerTousLesAuteurs() throws BibliothequeException {
        try {
            return auteurDAO.listerTousLesAuteurs();
        } catch (SQLException e) {
            throw new BibliothequeException("Impossible de charger la liste des auteurs.", e);
        }
    }

    /**
     * Recherche un auteur. Un identifiant inconnu est une erreur métier : le service lève une
     * exception explicite au lieu de renvoyer {@code null}.
     */
    public Auteur chercherAuteurParId(int id) throws BibliothequeException {
        try {
            return auteurDAO.trouverAuteurParId(id)
                    .orElseThrow(() -> new BibliothequeException("Aucun auteur trouvé avec l'ID : " + id));
        } catch (SQLException e) {
            throw new BibliothequeException("Erreur lors de la recherche de l'auteur ID : " + id, e);
        }
    }

    // =========================================================================
    // Persistance : livres
    // =========================================================================

    /**
     * Enregistre un livre. Sans verrou Java : si plusieurs threads insèrent le même titre en même
     * temps, c'est la contrainte {@code UNIQUE} de la base qui garantit qu'une seule insertion réussit ;
     * les autres reçoivent une {@link LivreDejaExistantException}.
     */
    @Override
    public void sauvegarderLivre(Livre livre) throws BibliothequeException {
        validerLivre(livre);
        try {
            livreDAO.ajouterLivre(livre);
        } catch (SQLException e) {
            throw traduireErreurInsertion(livre, e);
        }
    }

    /**
     * Variante « vérifier puis insérer » protégée par {@code synchronized}.
     *
     * <p>Sans verrou, deux threads pourraient constater en même temps que le titre est absent puis
     * l'insérer chacun (condition de course « check-then-act »). {@code synchronized} rend la
     * séquence vérification + insertion atomique pour les threads qui partagent cette instance.
     * Ce verrou ne protège pas contre un autre programme ou une autre instance : la contrainte
     * UNIQUE reste la garantie finale (elle est aussi traduite ici).
     */
    public synchronized void sauvegarderLivreSiAbsent(Livre livre) throws BibliothequeException {
        validerLivre(livre);
        try {
            if (livreDAO.existeTitre(livre.getTitre())) {
                throw new LivreDejaExistantException(
                        "Le livre '" + livre.getTitre() + "' existe déjà (détecté par la vérification Java).");
            }
            livreDAO.ajouterLivre(livre);
        } catch (SQLException e) {
            throw traduireErreurInsertion(livre, e);
        }
    }

    @Override
    public List<Livre> chargerTousLesLivres() throws BibliothequeException {
        try {
            return livreDAO.listerTousLesLivres();
        } catch (SQLException e) {
            throw new BibliothequeException("Impossible de charger la bibliothèque.", e);
        }
    }

    private static void validerLivre(Livre livre) throws BibliothequeException {
        if (livre == null) {
            throw new BibliothequeException("Erreur métier : aucun livre fourni.");
        }
        if (livre.getTitre() == null || livre.getTitre().isBlank()) {
            throw new BibliothequeException("Erreur métier : le titre ne peut pas être vide.");
        }
        if (livre.getAuteur() == null || livre.getAuteur().getId() <= 0) {
            throw new BibliothequeException("Erreur métier : le livre doit être rattaché à un auteur enregistré.");
        }
        if (livre.getCategorie() == null) {
            throw new BibliothequeException("Erreur métier : la catégorie est obligatoire.");
        }
    }

    private static BibliothequeException traduireErreurInsertion(Livre livre, SQLException e) {
        return switch (e.getErrorCode()) {
            case MYSQL_ENTREE_EN_DOUBLE -> new LivreDejaExistantException(
                    "Le livre '" + livre.getTitre() + "' existe déjà (refusé par la contrainte UNIQUE de la base).", e);
            case MYSQL_CLE_ETRANGERE_INVALIDE -> new BibliothequeException(
                    "L'auteur ID " + livre.getAuteur().getId() + " n'existe pas en base.", e);
            default -> new BibliothequeException("Erreur lors de la sauvegarde en base.", e);
        };
    }

    // =========================================================================
    // Collections « classiques »
    // =========================================================================

    /** List (ArrayList) : filtrage manuel avec une boucle. */
    public List<Livre> filtrerParCategorie(List<Livre> tousLesLivres, Categorie cible) {
        List<Livre> resultat = new ArrayList<>();
        for (Livre l : tousLesLivres) {
            if (l.getCategorie() == cible) {
                resultat.add(l);
            }
        }
        return resultat;
    }

    /** Set (HashSet) : un ensemble refuse les doublons, on obtient donc les catégories distinctes. */
    public Set<Categorie> obtenirCategoriesUniques(List<Livre> tousLesLivres) {
        Set<Categorie> categories = new HashSet<>();
        for (Livre l : tousLesLivres) {
            categories.add(l.getCategorie());
        }
        return categories;
    }

    /**
     * Map : index « titre → livre » pour une recherche directe sans parcourir la liste.
     * Si deux livres portent le même titre, le premier rencontré est conservé.
     */
    public Map<String, Livre> indexerLivresParTitre(List<Livre> tousLesLivres) {
        Map<String, Livre> index = new LinkedHashMap<>();
        for (Livre l : tousLesLivres) {
            index.putIfAbsent(l.getTitre(), l);
        }
        return index;
    }

    /** Tri en place par titre (A → Z, insensible à la casse) avec un Comparator anonyme (style pré-Java 8). */
    public void trierLivresParTitre(List<Livre> tousLesLivres) {
        Collections.sort(tousLesLivres, new Comparator<Livre>() {
            @Override
            public int compare(Livre l1, Livre l2) {
                // Négatif si l1 < l2, zéro si égaux, positif si l1 > l2.
                return l1.getTitre().compareToIgnoreCase(l2.getTitre());
            }
        });
    }

    /** Tri en place par identifiant décroissant (du plus récent au plus ancien). */
    public void trierLivresParId(List<Livre> tousLesLivres) {
        Collections.sort(tousLesLivres, new Comparator<Livre>() {
            @Override
            public int compare(Livre l1, Livre l2) {
                return Integer.compare(l2.getId(), l1.getId());
            }
        });
    }

    // =========================================================================
    // API Stream (mêmes traitements, style déclaratif ; la liste source n'est jamais modifiée)
    // =========================================================================

    /** stream → filter → toList. */
    public List<Livre> filtrerParCategorieStream(List<Livre> tousLesLivres, Categorie cible) {
        return tousLesLivres.stream()
                .filter(l -> l.getCategorie() == cible)
                .toList();
    }

    /** map extrait les catégories (avec doublons), le collecteur toSet les élimine. */
    public Set<Categorie> obtenirCategoriesUniquesStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .map(Livre::getCategorie)
                .collect(Collectors.toSet());
    }

    /**
     * Index « titre → livre ». La fonction de fusion {@code (premier, doublon) -> premier} est
     * indispensable : sans elle, {@link Collectors#toMap} lève une {@link IllegalStateException}
     * dès que deux livres portent le même titre.
     */
    public Map<String, Livre> indexerLivresParTitreStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .collect(Collectors.toMap(
                        Livre::getTitre,
                        l -> l,
                        (premier, doublon) -> premier,
                        LinkedHashMap::new));
    }

    /** Transformation List&lt;Livre&gt; → List&lt;String&gt;. */
    public List<String> obtenirSeulementLesTitres(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .map(Livre::getTitre)
                .toList();
    }

    /** Tri par titre (A → Z, insensible à la casse) dans une nouvelle liste. */
    public List<Livre> trierLivresParTitreStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .sorted(Comparator.comparing(Livre::getTitre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /** Tri par identifiant décroissant dans une nouvelle liste. */
    public List<Livre> trierLivresParNumeroIDStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .sorted(Comparator.comparingInt(Livre::getId).reversed())
                .toList();
    }

    /** Chaînage complet : filtre, tri puis transformation en titres. */
    public List<String> titresTriesDeCategorie(List<Livre> tousLesLivres, Categorie cible) {
        return tousLesLivres.stream()
                .filter(l -> l.getCategorie() == cible)
                .map(Livre::getTitre)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    // =========================================================================
    // Lambda seule (sans Stream)
    // =========================================================================

    /**
     * Retire de la liste les livres dont le titre contient le mot-clé (insensible à la casse),
     * grâce à {@link List#removeIf} qui reçoit une lambda (Predicate).
     *
     * @return {@code true} si au moins un livre a été retiré
     */
    public boolean supprimerLivresParMotCle(List<Livre> livres, String motCle) {
        Objects.requireNonNull(motCle, "motCle");
        String cible = motCle.toLowerCase(Locale.ROOT);
        return livres.removeIf(l -> l.getTitre().toLowerCase(Locale.ROOT).contains(cible));
    }

    // =========================================================================
    // Multithreading : accès limité par un sémaphore
    // =========================================================================

    /**
     * Consultation « lourde » du catalogue : au plus {@code consultationsSimultanees} threads
     * l'exécutent en même temps, les autres attendent un permis.
     *
     * <p>{@code acquire()} est appelé <strong>avant</strong> le bloc {@code try} : si l'attente est
     * interrompue, aucun permis n'a été obtenu et le {@code finally} (qui rend le permis) n'est pas
     * exécuté. Placer {@code acquire()} dans le {@code try} rendrait un permis jamais obtenu et
     * augmenterait la capacité du sémaphore. L'interruption n'est pas avalée : elle est propagée
     * à l'appelant.
     *
     * @return le nombre de livres lus
     */
    public int consulterLivresSimultane(String nomUtilisateur) throws BibliothequeException, InterruptedException {
        journal.accept("[" + nomUtilisateur + "] attend un accès à la base...");
        semaphore.acquire();
        int enCours = consultationsEnCours.incrementAndGet();
        maxConsultationsObservees.accumulateAndGet(enCours, Math::max);
        try {
            journal.accept("[" + nomUtilisateur + "] accès accordé (" + enCours + "/" + consultationsSimultanees
                    + " consultations en cours)");
            int nbLivres = livreDAO.listerTousLesLivres().size();
            journal.accept("[" + nomUtilisateur + "] lecture en cours : " + nbLivres + " livres trouvés");
            Thread.sleep(dureeTraitement);
            return nbLivres;
        } catch (SQLException e) {
            throw new BibliothequeException("Erreur de lecture pour " + nomUtilisateur + ".", e);
        } finally {
            consultationsEnCours.decrementAndGet();
            semaphore.release();
            journal.accept("[" + nomUtilisateur + "] libère son accès.");
        }
    }

    // =========================================================================
    // Multithreading : parallélisme simple
    // =========================================================================

    /**
     * Simule un traitement lent (pause de {@code dureeTraitement}) suivi d'une vraie requête SQL.
     * Appelée deux fois de suite, elle dure deux fois plus longtemps qu'appelée dans deux threads.
     */
    public int recupererTotalLivresLentement(String nomTache) throws BibliothequeException, InterruptedException {
        journal.accept("[" + nomTache + "] traitement en cours...");
        Thread.sleep(dureeTraitement);
        try {
            int total = livreDAO.compterLivres();
            journal.accept("[" + nomTache + "] terminé : " + total + " livres.");
            return total;
        } catch (SQLException e) {
            throw new BibliothequeException("Erreur de lecture pour " + nomTache + ".", e);
        }
    }

    // =========================================================================
    // Observation (tests et démonstrations)
    // =========================================================================

    public int getConsultationsSimultanees() {
        return consultationsSimultanees;
    }

    /** Permis actuellement libres ; égal à {@link #getConsultationsSimultanees()} au repos. */
    public int permisDisponibles() {
        return semaphore.availablePermits();
    }

    /** Nombre (estimé) de threads bloqués en attente d'un permis. */
    public int threadsEnAttente() {
        return semaphore.getQueueLength();
    }

    public int getConsultationsEnCours() {
        return consultationsEnCours.get();
    }

    /** Plus grand nombre de consultations simultanées observé depuis la création du service. */
    public int getMaxConsultationsObservees() {
        return maxConsultationsObservees.get();
    }
}

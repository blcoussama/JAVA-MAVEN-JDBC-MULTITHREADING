package com.bibliotech.services;

import com.bibliotech.dao.LivreDAO;
import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.MembreDAO;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Auteur;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Categorie;
import com.bibliotech.exceptions.LivreNonTrouveException;
import com.bibliotech.exceptions.MembreNonTrouveException;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Service de gestion de la bibliothèque
 *
 * Couche métier qui orchestre les opérations entre les DAO
 * et applique les règles de validation métier.
 *
 * Fonctionnalités principales :
 * - Gestion des livres (ajout, recherche, disponibilité)
 * - Gestion des membres (inscription, recherche)
 * - Validation des données métier
 * - Conversion Optional → Exception pour API cohérente
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class BibliothequeService {

    // ==================== ATTRIBUTS - DAO ====================

    private final LivreDAO livreDAO;
    private final AuteurDAO auteurDAO;
    private final MembreDAO membreDAO;

    // ==================== CONSTRUCTEUR ====================

    /**
     * Constructeur avec injection des DAO
     */
    public BibliothequeService() {
        this.livreDAO = new LivreDAO();
        this.auteurDAO = new AuteurDAO();
        this.membreDAO = new MembreDAO();
    }

    /**
     * Constructeur avec DAO personnalisés (pour tests)
     *
     * @param livreDAO DAO des livres
     * @param auteurDAO DAO des auteurs
     * @param membreDAO DAO des membres
     */
    public BibliothequeService(LivreDAO livreDAO, AuteurDAO auteurDAO, MembreDAO membreDAO) {
        this.livreDAO = livreDAO;
        this.auteurDAO = auteurDAO;
        this.membreDAO = membreDAO;
    }

    // ==================== GESTION DES LIVRES ====================

    /**
     * Ajoute un nouveau livre dans le catalogue
     *
     * VALIDATION MÉTIER :
     * 1. Vérifie que l'ISBN n'existe pas déjà
     * 2. Vérifie que l'auteur existe dans la BD
     * 3. Valide les attributs du livre (titre, catégorie, exemplaires)
     *
     * @param livre Livre à ajouter (doit avoir un auteur avec nom/prénom)
     * @return ID du livre créé
     * @throws DatabaseException si erreur BD ou validation échouée
     */
    public int ajouterLivre(Livre livre) throws DatabaseException {
        // VALIDATION 1 : Livre ne doit pas être null
        if (livre == null) {
            throw new DatabaseException("Le livre ne peut pas être null", null);
        }

        // VALIDATION 2 : ISBN obligatoire et non-existant
        if (livre.getIsbn() == null || livre.getIsbn().trim().isEmpty()) {
            throw new DatabaseException("L'ISBN est obligatoire", null);
        }

        if (livreDAO.isbnExists(livre.getIsbn())) {
            throw new DatabaseException(
                "Un livre avec l'ISBN " + livre.getIsbn() + " existe déjà", null);
        }

        // VALIDATION 3 : Titre obligatoire
        if (livre.getTitre() == null || livre.getTitre().trim().isEmpty()) {
            throw new DatabaseException("Le titre est obligatoire", null);
        }

        // VALIDATION 4 : Catégorie obligatoire
        if (livre.getCategorie() == null) {
            throw new DatabaseException("La catégorie est obligatoire", null);
        }

        // VALIDATION 5 : Auteur obligatoire
        if (livre.getAuteur() == null) {
            throw new DatabaseException("L'auteur est obligatoire", null);
        }

        // VALIDATION 6 : Vérifier/créer l'auteur dans la BD
        Auteur auteur = livre.getAuteur();

        // Si l'auteur n'a pas d'ID, on cherche s'il existe déjà
        if (auteur.getIdAuteur() == 0) {
            // Rechercher par nom/prénom
            Optional<Auteur> auteurExistant = auteurDAO.findByNom(
                auteur.getNom(), auteur.getPrenom());

            if (auteurExistant.isPresent()) {
                // Auteur existe déjà, utiliser son ID
                livre.setAuteur(auteurExistant.get());
            } else {
                // Auteur n'existe pas, le créer
                if (auteur.getNom() == null || auteur.getNom().trim().isEmpty()) {
                    throw new DatabaseException(
                        "Le nom de l'auteur est obligatoire", null);
                }
                if (auteur.getPrenom() == null || auteur.getPrenom().trim().isEmpty()) {
                    throw new DatabaseException(
                        "Le prénom de l'auteur est obligatoire", null);
                }

                int idAuteur = auteurDAO.insert(auteur);
                auteur.setIdAuteur(idAuteur);
            }
        } else {
            // L'auteur a déjà un ID, vérifier qu'il existe
            if (!auteurDAO.exists(auteur.getIdAuteur())) {
                throw new DatabaseException(
                    "L'auteur avec l'ID " + auteur.getIdAuteur() + " n'existe pas", null);
            }
        }

        // VALIDATION 7 : Nombre d'exemplaires positif
        if (livre.getNombreExemplaires() <= 0) {
            throw new DatabaseException(
                "Le nombre d'exemplaires doit être supérieur à 0", null);
        }

        // VALIDATION 8 : Disponibles = nombre_exemplaires au début
        if (livre.getDisponibles() != livre.getNombreExemplaires()) {
            livre.setDisponibles(livre.getNombreExemplaires());
        }

        // Insertion du livre
        return livreDAO.insert(livre);
    }

    /**
     * Recherche des livres selon un critère
     *
     * RECHERCHE MULTI-CRITÈRES :
     * - Si critère ressemble à un ISBN (commence par 978) → recherche par ISBN
     * - Si critère contient des espaces → recherche par auteur
     * - Sinon → recherche par titre
     *
     * @param critere Critère de recherche (titre, auteur, ou ISBN)
     * @return Liste des livres correspondants (peut être vide)
     * @throws DatabaseException si erreur BD
     */
    public List<Livre> rechercherLivres(String critere) throws DatabaseException {
        if (critere == null || critere.trim().isEmpty()) {
            // Aucun critère : retourner tous les livres
            return livreDAO.findAll();
        }

        String critereNettoye = critere.trim();

        // Stratégie 1 : Recherche par ISBN (si commence par 978)
        if (critereNettoye.startsWith("978")) {
            Optional<Livre> livre = livreDAO.findByIsbn(critereNettoye);
            if (livre.isPresent()) {
                List<Livre> resultat = new ArrayList<>();
                resultat.add(livre.get());
                return resultat;
            }
            // ISBN pas trouvé, continuer avec les autres stratégies
        }

        // Stratégie 2 : Recherche par auteur (si contient espace = nom + prénom)
        if (critereNettoye.contains(" ")) {
            List<Livre> parAuteur = livreDAO.rechercherParAuteur(critereNettoye);
            if (!parAuteur.isEmpty()) {
                return parAuteur;
            }
        }

        // Stratégie 3 : Recherche par titre (par défaut)
        return livreDAO.rechercherParTitre(critereNettoye);
    }

    /**
     * Affiche la disponibilité de tous les livres
     *
     * Retourne TOUS les livres avec leur statut de disponibilité.
     * Utile pour avoir une vue d'ensemble du catalogue.
     *
     * @return Liste de tous les livres avec leur disponibilité
     * @throws DatabaseException si erreur BD
     */
    public List<Livre> afficherDisponibilite() throws DatabaseException {
        return livreDAO.findAll();
    }

    /**
     * Affiche uniquement les livres disponibles (disponibles > 0)
     *
     * Utilise la méthode métier estDisponible() de Livre
     *
     * @return Liste des livres disponibles
     * @throws DatabaseException si erreur BD
     */
    public List<Livre> getLivresDisponibles() throws DatabaseException {
        List<Livre> tousLivres = livreDAO.findAll();
        List<Livre> disponibles = new ArrayList<>();

        for (Livre livre : tousLivres) {
            if (livre.estDisponible()) {  // Utilise méthode métier
                disponibles.add(livre);
            }
        }

        return disponibles;
    }

    /**
     * Recherche un livre par ISBN et lance exception si introuvable
     *
     * CONVERSION Optional → Exception pour API cohérente
     *
     * @param isbn ISBN du livre recherché
     * @return Livre trouvé (jamais null)
     * @throws LivreNonTrouveException si livre introuvable
     * @throws DatabaseException si erreur BD
     */
    public Livre getLivreParIsbn(String isbn)
            throws LivreNonTrouveException, DatabaseException {
        Optional<Livre> livre = livreDAO.findByIsbn(isbn);

        if (livre.isEmpty()) {
            throw new LivreNonTrouveException(isbn);
        }

        return livre.get();
    }

    /**
     * Recherche un livre par ID et lance exception si introuvable
     *
     * @param id ID du livre recherché
     * @return Livre trouvé (jamais null)
     * @throws LivreNonTrouveException si livre introuvable
     * @throws DatabaseException si erreur BD
     */
    public Livre getLivreParId(int id)
            throws LivreNonTrouveException, DatabaseException {
        Optional<Livre> livre = livreDAO.findById(id);

        if (livre.isEmpty()) {
            throw new LivreNonTrouveException(
                String.valueOf(id), "Livre introuvable avec l'ID");
        }

        return livre.get();
    }

    /**
     * Récupère les livres par catégorie
     *
     * @param categorie Catégorie recherchée
     * @return Liste des livres de cette catégorie
     * @throws DatabaseException si erreur BD
     */
    public List<Livre> getLivresParCategorie(Categorie categorie)
            throws DatabaseException {
        if (categorie == null) {
            throw new DatabaseException("La catégorie ne peut pas être null", null);
        }

        return livreDAO.findByCategorie(categorie);
    }

    // ==================== GESTION DES MEMBRES ====================

    /**
     * Inscrit un nouveau membre dans le système
     *
     * VALIDATION MÉTIER :
     * 1. Vérifie que le CIN n'existe pas déjà
     * 2. Vérifie que l'email n'existe pas déjà
     * 3. Valide les champs obligatoires
     * 4. Initialise date d'inscription et nombre d'emprunts à 0
     *
     * @param membre Membre à inscrire
     * @return ID du membre créé
     * @throws DatabaseException si erreur BD ou validation échouée
     */
    public int inscrireMembre(Membre membre) throws DatabaseException {
        // VALIDATION 1 : Membre ne doit pas être null
        if (membre == null) {
            throw new DatabaseException("Le membre ne peut pas être null", null);
        }

        // VALIDATION 2 : CIN obligatoire et non-existant
        if (membre.getCin() == null || membre.getCin().trim().isEmpty()) {
            throw new DatabaseException("Le CIN est obligatoire", null);
        }

        if (membreDAO.cinExists(membre.getCin())) {
            throw new DatabaseException(
                "Un membre avec le CIN " + membre.getCin() + " existe déjà", null);
        }

        // VALIDATION 3 : Nom obligatoire
        if (membre.getNom() == null || membre.getNom().trim().isEmpty()) {
            throw new DatabaseException("Le nom est obligatoire", null);
        }

        // VALIDATION 4 : Prénom obligatoire
        if (membre.getPrenom() == null || membre.getPrenom().trim().isEmpty()) {
            throw new DatabaseException("Le prénom est obligatoire", null);
        }

        // VALIDATION 5 : Email obligatoire et non-existant
        if (membre.getEmail() == null || membre.getEmail().trim().isEmpty()) {
            throw new DatabaseException("L'email est obligatoire", null);
        }

        // Validation format email (simple)
        if (!membre.getEmail().contains("@")) {
            throw new DatabaseException("Format d'email invalide", null);
        }

        if (membreDAO.emailExists(membre.getEmail())) {
            throw new DatabaseException(
                "Un membre avec l'email " + membre.getEmail() + " existe déjà", null);
        }

        // VALIDATION 6 : Date d'inscription (initialiser si null)
        if (membre.getDateInscription() == null) {
            membre.setDateInscription(java.time.LocalDate.now());
        }

        // VALIDATION 7 : Nombre d'emprunts actifs = 0 au début
        if (membre.getNombreEmpruntsActifs() != 0) {
            membre.setNombreEmpruntsActifs(0);
        }

        // Insertion du membre
        return membreDAO.insert(membre);
    }

    /**
     * Recherche un membre par CIN et lance exception si introuvable
     *
     * CONVERSION Optional → Exception pour API cohérente
     *
     * @param cin CIN du membre recherché
     * @return Membre trouvé (jamais null)
     * @throws MembreNonTrouveException si membre introuvable
     * @throws DatabaseException si erreur BD
     */
    public Membre rechercherMembre(String cin)
            throws MembreNonTrouveException, DatabaseException {
        if (cin == null || cin.trim().isEmpty()) {
            throw new DatabaseException("Le CIN ne peut pas être vide", null);
        }

        Optional<Membre> membre = membreDAO.findByCin(cin);

        if (membre.isEmpty()) {
            throw new MembreNonTrouveException(cin);
        }

        return membre.get();
    }

    /**
     * Recherche un membre par ID et lance exception si introuvable
     *
     * @param id ID du membre recherché
     * @return Membre trouvé (jamais null)
     * @throws MembreNonTrouveException si membre introuvable
     * @throws DatabaseException si erreur BD
     */
    public Membre getMembreParId(int id)
            throws MembreNonTrouveException, DatabaseException {
        Optional<Membre> membre = membreDAO.findById(id);

        if (membre.isEmpty()) {
            throw new MembreNonTrouveException(
                String.valueOf(id), "Membre introuvable avec l'ID");
        }

        return membre.get();
    }

    /**
     * Recherche des membres par nom (partiel)
     *
     * @param nom Nom à rechercher
     * @return Liste des membres correspondants
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> rechercherMembresParNom(String nom)
            throws DatabaseException {
        if (nom == null || nom.trim().isEmpty()) {
            // Aucun critère : retourner tous les membres
            return membreDAO.findAll();
        }

        return membreDAO.rechercherParNom(nom);
    }

    /**
     * Récupère tous les membres inscrits
     *
     * @return Liste de tous les membres
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> getAllMembres() throws DatabaseException {
        return membreDAO.findAll();
    }

    /**
     * Récupère les membres ayant des emprunts actifs
     *
     * @return Liste des membres actifs
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> getMembresActifs() throws DatabaseException {
        return membreDAO.getMembresActifs();
    }

    /**
     * Vérifie si un membre peut emprunter (< 5 emprunts)
     *
     * Utilise la méthode métier peutEmprunter() de Membre
     *
     * @param cin CIN du membre
     * @return true si le membre peut emprunter
     * @throws MembreNonTrouveException si membre introuvable
     * @throws DatabaseException si erreur BD
     */
    public boolean membrePeutEmprunter(String cin)
            throws MembreNonTrouveException, DatabaseException {
        Membre membre = rechercherMembre(cin);
        return membre.peutEmprunter();  // Utilise méthode métier
    }

    // ==================== STATISTIQUES SIMPLES ====================

    /**
     * Compte le nombre total de livres dans le catalogue
     *
     * @return Nombre de livres
     * @throws DatabaseException si erreur BD
     */
    public long getNombreTotalLivres() throws DatabaseException {
        return livreDAO.count();
    }

    /**
     * Compte le nombre total de membres inscrits
     *
     * @return Nombre de membres
     * @throws DatabaseException si erreur BD
     */
    public long getNombreTotalMembres() throws DatabaseException {
        return membreDAO.count();
    }

    /**
     * Compte le nombre de livres par catégorie
     *
     * @param categorie Catégorie à compter
     * @return Nombre de livres dans cette catégorie
     * @throws DatabaseException si erreur BD
     */
    public long getNombreLivresParCategorie(Categorie categorie)
            throws DatabaseException {
        if (categorie == null) {
            throw new DatabaseException("La catégorie ne peut pas être null", null);
        }

        return livreDAO.countByCategorie(categorie);
    }

    // ==================== GETTERS (pour tests) ====================

    public LivreDAO getLivreDAO() {
        return livreDAO;
    }

    public AuteurDAO getAuteurDAO() {
        return auteurDAO;
    }

    public MembreDAO getMembreDAO() {
        return membreDAO;
    }
}

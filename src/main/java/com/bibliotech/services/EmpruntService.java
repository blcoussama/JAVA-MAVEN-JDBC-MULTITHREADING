package com.bibliotech.services;

import com.bibliotech.dao.EmpruntDAO;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.dao.MembreDAO;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Membre;
import com.bibliotech.models.StatutEmprunt;
import com.bibliotech.exceptions.DatabaseException;
import com.bibliotech.exceptions.LivreIndisponibleException;
import com.bibliotech.exceptions.LivreNonTrouveException;
import com.bibliotech.exceptions.MembreNonTrouveException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service de gestion des emprunts avec support multithreading
 *
 * MULTITHREADING (TD6-10) :
 * - ReentrantLock pour synchronisation des accès concurrents
 * - Condition pour attente et notification (await/signal/signalAll)
 * - Protection des sections critiques (emprunt/retour)
 * - Gestion de file d'attente si livre indisponible
 *
 * FONCTIONNALITÉS :
 * - Emprunt de livre avec validation et lock
 * - Retour de livre avec notification des threads en attente
 * - Consultation thread-safe de la disponibilité
 * - Historique et statistiques
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class EmpruntService {

    // ==================== ATTRIBUTS ====================

    // DAOs pour accès base de données
    private final EmpruntDAO empruntDAO;
    private final LivreDAO livreDAO;
    private final MembreDAO membreDAO;

    // MULTITHREADING (TD8) - ReentrantLock + Condition
    private final ReentrantLock lock;
    private final Condition livreDisponible;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur par défaut avec initialisation des DAOs
     */
    public EmpruntService() {
        this.empruntDAO = new EmpruntDAO();
        this.livreDAO = new LivreDAO();
        this.membreDAO = new MembreDAO();

        // Initialisation multithreading
        this.lock = new ReentrantLock(true);  // Fair lock (FIFO)
        this.livreDisponible = lock.newCondition();
    }

    /**
     * Constructeur pour injection de dépendances (tests)
     * @param empruntDAO DAO des emprunts
     * @param livreDAO DAO des livres
     * @param membreDAO DAO des membres
     */
    public EmpruntService(EmpruntDAO empruntDAO, LivreDAO livreDAO, MembreDAO membreDAO) {
        this.empruntDAO = empruntDAO;
        this.livreDAO = livreDAO;
        this.membreDAO = membreDAO;

        // Initialisation multithreading
        this.lock = new ReentrantLock(true);  // Fair lock (FIFO)
        this.livreDisponible = lock.newCondition();
    }

    // ==================== OPÉRATIONS D'EMPRUNT (AVEC MULTITHREADING) ====================

    /**
     * Effectue un emprunt avec protection multithreading
     *
     * SYNCHRONISATION (TD8) :
     * - Acquisition du ReentrantLock avant opération
     * - Validation de disponibilité dans section critique
     * - Libération du lock dans finally (TOUJOURS)
     *
     * VALIDATION MÉTIER :
     * 1. Membre existe et peut emprunter (< 5 emprunts actifs)
     * 2. Livre existe et est disponible (disponibles > 0)
     * 3. Transaction BD atomique via EmpruntDAO
     *
     * @param idMembre ID du membre emprunteur
     * @param idLivre ID du livre à emprunter
     * @return Emprunt créé avec toutes les informations
     * @throws MembreNonTrouveException si membre inexistant
     * @throws LivreNonTrouveException si livre inexistant
     * @throws LivreIndisponibleException si livre non disponible
     * @throws DatabaseException si erreur BD ou validation échouée
     */
    public Emprunt effectuerEmprunt(int idMembre, int idLivre)
            throws MembreNonTrouveException, LivreNonTrouveException,
                   LivreIndisponibleException, DatabaseException {

        // ACQUISITION DU LOCK (TD8)
        lock.lock();

        try {
            // ===== VALIDATION 1 : Membre existe =====
            Optional<Membre> membreOpt = membreDAO.findById(idMembre);
            if (membreOpt.isEmpty()) {
                throw new MembreNonTrouveException("ID: " + idMembre);
            }

            Membre membre = membreOpt.get();

            // ===== VALIDATION 2 : Membre peut emprunter =====
            if (!membre.peutEmprunter()) {
                throw new DatabaseException(
                    "Le membre " + membre.getNom() + " " + membre.getPrenom() +
                    " a atteint le maximum d'emprunts (5)", null);
            }

            // ===== VALIDATION 3 : Livre existe =====
            Optional<Livre> livreOpt = livreDAO.findById(idLivre);
            if (livreOpt.isEmpty()) {
                throw new LivreNonTrouveException("ID: " + idLivre);
            }

            Livre livre = livreOpt.get();

            // ===== VALIDATION 4 : Livre est disponible =====
            if (!livre.estDisponible()) {
                throw new LivreIndisponibleException(livre.getIsbn());
            }

            // ===== TRANSACTION BD (EmpruntDAO est déjà synchronized) =====
            // Le DAO gère la transaction atomique en 4 étapes :
            // 1. Vérifier disponibilité avec SELECT FOR UPDATE
            // 2. Décrémenter disponibilité livre
            // 3. Créer emprunt
            // 4. Incrémenter compteur membre
            Emprunt emprunt = empruntDAO.effectuerEmpruntTransaction(idMembre, idLivre);

            return emprunt;

        } finally {
            // LIBÉRATION DU LOCK (TOUJOURS dans finally)
            lock.unlock();
        }
    }

    /**
     * Effectue un emprunt avec attente si livre indisponible
     *
     * SYNCHRONISATION AVANCÉE (TD8) :
     * - Utilise Condition.await() pour attendre notification
     * - Attend qu'un autre thread fasse un retour et signale
     * - Timeout pour éviter attente infinie
     *
     * SCÉNARIO D'UTILISATION :
     * Thread 1 : Emprunte le dernier exemplaire
     * Thread 2 : Appelle effectuerEmpruntAvecAttente() → ATTEND
     * Thread 1 : Retourne le livre → signalAll()
     * Thread 2 : Se réveille et effectue l'emprunt
     *
     * @param idMembre ID du membre emprunteur
     * @param idLivre ID du livre à emprunter
     * @param timeoutSeconds Temps d'attente maximum en secondes
     * @return Emprunt créé
     * @throws MembreNonTrouveException si membre inexistant
     * @throws LivreNonTrouveException si livre inexistant
     * @throws LivreIndisponibleException si timeout atteint
     * @throws DatabaseException si erreur BD
     * @throws InterruptedException si thread interrompu pendant attente
     */
    public Emprunt effectuerEmpruntAvecAttente(int idMembre, int idLivre, long timeoutSeconds)
            throws MembreNonTrouveException, LivreNonTrouveException,
                   LivreIndisponibleException, DatabaseException, InterruptedException {

        // ACQUISITION DU LOCK
        lock.lock();

        try {
            // ===== VALIDATION : Membre et Livre existent =====
            Optional<Membre> membreOpt = membreDAO.findById(idMembre);
            if (membreOpt.isEmpty()) {
                throw new MembreNonTrouveException("ID: " + idMembre);
            }

            Membre membre = membreOpt.get();

            if (!membre.peutEmprunter()) {
                throw new DatabaseException(
                    "Le membre a atteint le maximum d'emprunts (5)", null);
            }

            Optional<Livre> livreOpt = livreDAO.findById(idLivre);
            if (livreOpt.isEmpty()) {
                throw new LivreNonTrouveException("ID: " + idLivre);
            }

            Livre livre = livreOpt.get();

            // ===== ATTENTE SI LIVRE INDISPONIBLE (TD8 - Condition) =====
            long nanosTimeout = TimeUnit.SECONDS.toNanos(timeoutSeconds);

            while (!livre.estDisponible()) {
                System.out.println("[" + Thread.currentThread().getName() + "] " +
                    "Livre '" + livre.getTitre() + "' indisponible. Attente...");

                // ATTENTE AVEC TIMEOUT
                // await() libère temporairement le lock et attend signal
                nanosTimeout = livreDisponible.awaitNanos(nanosTimeout);

                if (nanosTimeout <= 0) {
                    // TIMEOUT ATTEINT
                    throw new LivreIndisponibleException(
                        livre.getIsbn() + " - Timeout d'attente atteint (" +
                        timeoutSeconds + "s)");
                }

                // Recharger le livre pour vérifier disponibilité mise à jour
                livreOpt = livreDAO.findById(idLivre);
                if (livreOpt.isEmpty()) {
                    throw new LivreNonTrouveException("ID: " + idLivre);
                }
                livre = livreOpt.get();
            }

            // Livre maintenant disponible, effectuer emprunt
            System.out.println("[" + Thread.currentThread().getName() + "] " +
                "Livre '" + livre.getTitre() + "' maintenant disponible !");

            Emprunt emprunt = empruntDAO.effectuerEmpruntTransaction(idMembre, idLivre);

            return emprunt;

        } finally {
            lock.unlock();
        }
    }

    /**
     * Retourne un livre avec notification des threads en attente
     *
     * SYNCHRONISATION (TD8) :
     * - Acquisition du lock avant retour
     * - signalAll() après retour pour réveiller TOUS les threads en attente
     * - Threads en attente dans effectuerEmpruntAvecAttente() se réveillent
     *
     * NOTIFICATION (TD7-8) :
     * - signalAll() au lieu de signal() pour réveiller tous les threads
     * - Chaque thread vérifie s'il peut emprunter (FIFO grâce à fair lock)
     *
     * @param idEmprunt ID de l'emprunt à retourner
     * @return Emprunt mis à jour avec date de retour
     * @throws DatabaseException si erreur BD ou emprunt inexistant
     */
    public Emprunt retournerLivre(int idEmprunt) throws DatabaseException {

        // ACQUISITION DU LOCK
        lock.lock();

        try {
            // ===== VALIDATION : Emprunt existe =====
            Optional<Emprunt> empruntOpt = empruntDAO.findById(idEmprunt);
            if (empruntOpt.isEmpty()) {
                throw new DatabaseException(
                    "Emprunt #" + idEmprunt + " introuvable", null);
            }

            Emprunt emprunt = empruntOpt.get();

            // ===== VALIDATION : Emprunt est EN_COURS =====
            if (emprunt.getStatut() != StatutEmprunt.EN_COURS) {
                throw new DatabaseException(
                    "L'emprunt #" + idEmprunt + " n'est pas en cours (statut: " +
                    emprunt.getStatut() + ")", null);
            }

            // ===== TRANSACTION BD (retour atomique) =====
            // Le DAO gère la transaction en 4 étapes :
            // 1. Récupérer infos emprunt
            // 2. Mettre à jour emprunt (date retour + statut)
            // 3. Incrémenter disponibilité livre
            // 4. Décrémenter compteur membre
            Emprunt empruntRetourne = empruntDAO.effectuerRetourTransaction(idEmprunt);

            // ===== NOTIFICATION DES THREADS EN ATTENTE (TD8) =====
            // signalAll() réveille TOUS les threads qui attendent sur cette Condition
            // Threads en attente dans effectuerEmpruntAvecAttente() vont se réveiller
            livreDisponible.signalAll();

            System.out.println("[" + Thread.currentThread().getName() + "] " +
                "Livre '" + emprunt.getLivre().getTitre() + "' retourné. " +
                "Notification envoyée aux threads en attente.");

            return empruntRetourne;

        } finally {
            lock.unlock();
        }
    }

    // ==================== CONSULTATIONS THREAD-SAFE ====================

    /**
     * Vérifie la disponibilité d'un livre de manière thread-safe
     *
     * SYNCHRONISATION (TD8) :
     * - Lecture protégée par lock pour cohérence
     * - Aucune modification donc pas besoin de Condition
     *
     * @param idLivre ID du livre à vérifier
     * @return true si au moins un exemplaire disponible
     * @throws LivreNonTrouveException si livre inexistant
     * @throws DatabaseException si erreur BD
     */
    public boolean verifierDisponibilite(int idLivre)
            throws LivreNonTrouveException, DatabaseException {

        lock.lock();

        try {
            Optional<Livre> livreOpt = livreDAO.findById(idLivre);
            if (livreOpt.isEmpty()) {
                throw new LivreNonTrouveException("ID: " + idLivre);
            }

            return livreOpt.get().estDisponible();

        } finally {
            lock.unlock();
        }
    }

    /**
     * Récupère un emprunt par son ID
     * Conversion Optional → Exception
     *
     * @param idEmprunt ID de l'emprunt
     * @return Emprunt trouvé
     * @throws DatabaseException si emprunt inexistant ou erreur BD
     */
    public Emprunt getEmprunt(int idEmprunt) throws DatabaseException {
        Optional<Emprunt> empruntOpt = empruntDAO.findById(idEmprunt);

        if (empruntOpt.isEmpty()) {
            throw new DatabaseException(
                "Emprunt #" + idEmprunt + " introuvable", null);
        }

        return empruntOpt.get();
    }

    /**
     * Récupère l'historique complet des emprunts d'un membre
     *
     * @param cin CIN du membre
     * @return Liste des emprunts (tous statuts)
     * @throws MembreNonTrouveException si membre inexistant
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> getHistoriqueEmpruntsMembre(String cin)
            throws MembreNonTrouveException, DatabaseException {

        // Validation : CIN non vide
        if (cin == null || cin.trim().isEmpty()) {
            throw new DatabaseException("Le CIN est obligatoire", null);
        }

        // Rechercher membre
        Optional<Membre> membreOpt = membreDAO.findByCin(cin);
        if (membreOpt.isEmpty()) {
            throw new MembreNonTrouveException(cin);
        }

        Membre membre = membreOpt.get();

        // Récupérer emprunts
        return empruntDAO.findByMembre(membre.getIdMembre());
    }

    /**
     * Récupère tous les emprunts en cours
     *
     * @return Liste des emprunts avec statut EN_COURS
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> getEmpruntsEnCours() throws DatabaseException {
        return empruntDAO.findByStatut(StatutEmprunt.EN_COURS);
    }

    /**
     * Récupère tous les emprunts en retard
     * Calcul automatique : EN_COURS + date_retour_prevue < aujourd'hui
     *
     * @return Liste des emprunts en retard
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> getEmpruntsEnRetard() throws DatabaseException {
        return empruntDAO.findEmpruntsEnRetard();
    }

    /**
     * Récupère les emprunts en cours d'un membre spécifique
     *
     * @param idMembre ID du membre
     * @return Liste des emprunts EN_COURS du membre
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> getEmpruntsEnCoursMembre(int idMembre) throws DatabaseException {
        return empruntDAO.findEmpruntsEnCoursByMembre(idMembre);
    }

    /**
     * Récupère tous les emprunts (tous statuts)
     *
     * @return Liste complète des emprunts
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> getAllEmprunts() throws DatabaseException {
        return empruntDAO.findAll();
    }

    // ==================== STATISTIQUES ====================

    /**
     * Compte le nombre d'emprunts en cours
     *
     * @return Nombre d'emprunts avec statut EN_COURS
     * @throws DatabaseException si erreur BD
     */
    public long getNombreEmpruntsEnCours() throws DatabaseException {
        return empruntDAO.countByStatut(StatutEmprunt.EN_COURS);
    }

    /**
     * Compte le nombre total d'emprunts
     *
     * @return Nombre total d'emprunts dans la base
     * @throws DatabaseException si erreur BD
     */
    public long getNombreEmpruntsTotal() throws DatabaseException {
        return empruntDAO.count();
    }

    /**
     * Compte le nombre d'emprunts retournés
     *
     * @return Nombre d'emprunts avec statut RETOURNE
     * @throws DatabaseException si erreur BD
     */
    public long getNombreEmpruntsRetournes() throws DatabaseException {
        return empruntDAO.countByStatut(StatutEmprunt.RETOURNE);
    }

    /**
     * Compte le nombre d'emprunts en retard
     *
     * @return Nombre d'emprunts avec statut EN_RETARD
     * @throws DatabaseException si erreur BD
     */
    public long getNombreEmpruntsEnRetard() throws DatabaseException {
        return empruntDAO.countByStatut(StatutEmprunt.EN_RETARD);
    }
}

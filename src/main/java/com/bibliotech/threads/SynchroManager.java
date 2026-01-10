package com.bibliotech.threads;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gestionnaire centralisé de synchronisation pour le multithreading
 *
 * CONCEPTS TD6-10 DÉMONTRÉS :
 * - ReentrantLock pour coordination globale (TD8)
 * - Semaphore pour limitation d'accès concurrent (TD10)
 * - AtomicInteger pour compteurs thread-safe (TD10)
 *
 * FONCTIONNALITÉS :
 * - Limitation à 5 opérations simultanées maximum
 * - Compteurs thread-safe pour statistiques
 * - Lock global pour coordination
 * - Affichage des statistiques en temps réel
 *
 * UTILISATION :
 * - BibliothecaireThread utilise ce manager pour toutes ses opérations
 * - Garantit la cohérence dans un environnement multi-threads
 * - Évite les race conditions sur les compteurs
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class SynchroManager {

    // ==================== ATTRIBUTS DE SYNCHRONISATION ====================

    /**
     * REENTRANT LOCK (TD8) - Fair lock pour coordination globale
     * Garantit l'ordre FIFO (First In First Out) des threads
     */
    private final ReentrantLock lock;

    /**
     * SEMAPHORE (TD10) - Limite à 5 opérations simultanées maximum
     * Simule une limitation de ressources (5 guichets disponibles)
     */
    private final Semaphore semaphore;

    /**
     * ATOMIC COUNTERS (TD10) - Compteurs thread-safe sans synchronisation
     * Plus performant que synchronized pour de simples incréments
     */
    private final AtomicInteger compteurEmprunts;
    private final AtomicInteger compteurRetours;
    private final AtomicInteger compteurRecherches;

    // Compteurs supplémentaires pour statistiques détaillées
    private final AtomicInteger compteurEmpruntsReussis;
    private final AtomicInteger compteurEmpruntsEchoues;
    private final AtomicInteger compteurRetoursReussis;
    private final AtomicInteger compteurRetoursEchoues;

    // Timestamp de démarrage pour calcul de durée
    private final long timestampDemarrage;

    // ==================== CONSTRUCTEUR ====================

    /**
     * Constructeur avec initialisation de tous les mécanismes de synchronisation
     *
     * PARAMÈTRES PAR DÉFAUT :
     * - Fair lock (FIFO) activé
     * - 5 permits pour le semaphore
     * - Tous les compteurs initialisés à 0
     */
    public SynchroManager() {
        // ReentrantLock avec fairness = true (FIFO)
        this.lock = new ReentrantLock(true);

        // Semaphore avec 5 permits (5 opérations simultanées max)
        this.semaphore = new Semaphore(5, true);  // fair = true aussi

        // Initialisation des compteurs atomiques
        this.compteurEmprunts = new AtomicInteger(0);
        this.compteurRetours = new AtomicInteger(0);
        this.compteurRecherches = new AtomicInteger(0);

        this.compteurEmpruntsReussis = new AtomicInteger(0);
        this.compteurEmpruntsEchoues = new AtomicInteger(0);
        this.compteurRetoursReussis = new AtomicInteger(0);
        this.compteurRetoursEchoues = new AtomicInteger(0);

        // Timestamp de démarrage
        this.timestampDemarrage = System.currentTimeMillis();
    }

    /**
     * Constructeur avec nombre de permits personnalisé
     *
     * @param nbPermits Nombre de permits du semaphore (opérations simultanées max)
     */
    public SynchroManager(int nbPermits) {
        this.lock = new ReentrantLock(true);
        this.semaphore = new Semaphore(nbPermits, true);

        this.compteurEmprunts = new AtomicInteger(0);
        this.compteurRetours = new AtomicInteger(0);
        this.compteurRecherches = new AtomicInteger(0);

        this.compteurEmpruntsReussis = new AtomicInteger(0);
        this.compteurEmpruntsEchoues = new AtomicInteger(0);
        this.compteurRetoursReussis = new AtomicInteger(0);
        this.compteurRetoursEchoues = new AtomicInteger(0);

        this.timestampDemarrage = System.currentTimeMillis();
    }

    // ==================== GESTION DU LOCK ====================

    /**
     * Acquiert le ReentrantLock
     *
     * UTILISATION :
     * try {
     *     manager.acquireLock();
     *     // Section critique
     * } finally {
     *     manager.releaseLock();
     * }
     */
    public void acquireLock() {
        lock.lock();
    }

    /**
     * Libère le ReentrantLock
     *
     * ⚠️ IMPORTANT : Toujours appeler dans un bloc finally
     */
    public void releaseLock() {
        lock.unlock();
    }

    /**
     * Tente d'acquérir le lock sans bloquer
     *
     * @return true si le lock a été acquis, false sinon
     */
    public boolean tryLock() {
        return lock.tryLock();
    }

    // ==================== GESTION DU SEMAPHORE ====================

    /**
     * Acquiert un permit du semaphore (bloquant)
     *
     * BLOQUE si tous les permits sont utilisés
     * Thread attend qu'un permit soit libéré
     *
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public void acquirePermit() throws InterruptedException {
        semaphore.acquire();
    }

    /**
     * Tente d'acquérir un permit sans bloquer
     *
     * @return true si permit acquis, false si tous occupés
     */
    public boolean tryAcquirePermit() {
        return semaphore.tryAcquire();
    }

    /**
     * Libère un permit du semaphore
     *
     * Permet à un autre thread d'acquérir ce permit
     */
    public void releasePermit() {
        semaphore.release();
    }

    /**
     * Récupère le nombre de permits disponibles
     *
     * @return Nombre de permits libres
     */
    public int getPermitsDisponibles() {
        return semaphore.availablePermits();
    }

    // ==================== GESTION DES COMPTEURS ====================

    /**
     * Incrémente le compteur d'une opération spécifique
     *
     * THREAD-SAFE grâce à AtomicInteger (TD10)
     * Pas besoin de synchronized !
     *
     * @param typeOperation Type d'opération ("EMPRUNT", "RETOUR", "RECHERCHE")
     */
    public void incrementerCompteur(String typeOperation) {
        switch (typeOperation.toUpperCase()) {
            case "EMPRUNT":
                compteurEmprunts.incrementAndGet();
                break;
            case "RETOUR":
                compteurRetours.incrementAndGet();
                break;
            case "RECHERCHE":
                compteurRecherches.incrementAndGet();
                break;
            default:
                System.err.println("Type d'opération inconnu : " + typeOperation);
        }
    }

    /**
     * Incrémente le compteur avec indication de succès/échec
     *
     * @param typeOperation Type d'opération
     * @param succes true si opération réussie, false si échec
     */
    public void incrementerCompteur(String typeOperation, boolean succes) {
        incrementerCompteur(typeOperation);

        if (succes) {
            switch (typeOperation.toUpperCase()) {
                case "EMPRUNT":
                    compteurEmpruntsReussis.incrementAndGet();
                    break;
                case "RETOUR":
                    compteurRetoursReussis.incrementAndGet();
                    break;
            }
        } else {
            switch (typeOperation.toUpperCase()) {
                case "EMPRUNT":
                    compteurEmpruntsEchoues.incrementAndGet();
                    break;
                case "RETOUR":
                    compteurRetoursEchoues.incrementAndGet();
                    break;
            }
        }
    }

    // ==================== GETTERS COMPTEURS ====================

    /**
     * Récupère la valeur actuelle d'un compteur
     *
     * @param typeOperation Type d'opération
     * @return Valeur du compteur
     */
    public int getCompteur(String typeOperation) {
        switch (typeOperation.toUpperCase()) {
            case "EMPRUNT":
                return compteurEmprunts.get();
            case "RETOUR":
                return compteurRetours.get();
            case "RECHERCHE":
                return compteurRecherches.get();
            default:
                return 0;
        }
    }

    /**
     * @return Nombre total d'emprunts tentés
     */
    public int getTotalEmprunts() {
        return compteurEmprunts.get();
    }

    /**
     * @return Nombre total de retours tentés
     */
    public int getTotalRetours() {
        return compteurRetours.get();
    }

    /**
     * @return Nombre total de recherches effectuées
     */
    public int getTotalRecherches() {
        return compteurRecherches.get();
    }

    /**
     * @return Nombre d'emprunts réussis
     */
    public int getEmpruntsReussis() {
        return compteurEmpruntsReussis.get();
    }

    /**
     * @return Nombre d'emprunts échoués
     */
    public int getEmpruntsEchoues() {
        return compteurEmpruntsEchoues.get();
    }

    /**
     * @return Nombre de retours réussis
     */
    public int getRetoursReussis() {
        return compteurRetoursReussis.get();
    }

    /**
     * @return Nombre de retours échoués
     */
    public int getRetoursEchoues() {
        return compteurRetoursEchoues.get();
    }

    /**
     * @return Nombre total d'opérations (emprunts + retours + recherches)
     */
    public int getTotalOperations() {
        return compteurEmprunts.get() + compteurRetours.get() + compteurRecherches.get();
    }

    // ==================== RÉINITIALISATION ====================

    /**
     * Réinitialise tous les compteurs à 0
     *
     * Utile entre différentes simulations
     */
    public void resetCompteurs() {
        compteurEmprunts.set(0);
        compteurRetours.set(0);
        compteurRecherches.set(0);
        compteurEmpruntsReussis.set(0);
        compteurEmpruntsEchoues.set(0);
        compteurRetoursReussis.set(0);
        compteurRetoursEchoues.set(0);
    }

    // ==================== AFFICHAGE STATISTIQUES ====================

    /**
     * Affiche les statistiques complètes en temps réel
     *
     * Format : Console avec cadre et couleurs
     */
    public void afficherStats() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          STATISTIQUES MULTITHREADING EN TEMPS RÉEL        ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        // Opérations totales
        System.out.printf("║ Total opérations          : %5d                          ║%n",
                getTotalOperations());
        System.out.println("║                                                           ║");

        // Détail par type
        System.out.printf("║ • Emprunts totaux         : %5d                          ║%n",
                compteurEmprunts.get());
        System.out.printf("║   - Réussis               : %5d                          ║%n",
                compteurEmpruntsReussis.get());
        System.out.printf("║   - Échoués               : %5d                          ║%n",
                compteurEmpruntsEchoues.get());

        System.out.println("║                                                           ║");

        System.out.printf("║ • Retours totaux          : %5d                          ║%n",
                compteurRetours.get());
        System.out.printf("║   - Réussis               : %5d                          ║%n",
                compteurRetoursReussis.get());
        System.out.printf("║   - Échoués               : %5d                          ║%n",
                compteurRetoursEchoues.get());

        System.out.println("║                                                           ║");

        System.out.printf("║ • Recherches effectuées   : %5d                          ║%n",
                compteurRecherches.get());

        System.out.println("║                                                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        // État du semaphore
        System.out.printf("║ Permits disponibles       : %d / 5                         ║%n",
                semaphore.availablePermits());

        // Durée d'exécution
        long dureeTotale = System.currentTimeMillis() - timestampDemarrage;
        long secondes = dureeTotale / 1000;
        System.out.printf("║ Durée d'exécution         : %d secondes                   ║%n",
                secondes);

        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Affiche les statistiques en version compacte (une ligne)
     */
    public void afficherStatsCompactes() {
        System.out.printf("[STATS] Total: %d | Emprunts: %d (%d✓ %d✗) | " +
                        "Retours: %d (%d✓ %d✗) | Recherches: %d | Permits: %d/5%n",
                getTotalOperations(),
                compteurEmprunts.get(), compteurEmpruntsReussis.get(), compteurEmpruntsEchoues.get(),
                compteurRetours.get(), compteurRetoursReussis.get(), compteurRetoursEchoues.get(),
                compteurRecherches.get(),
                semaphore.availablePermits());
    }

    // ==================== MÉTHODES DE DIAGNOSTIC ====================

    /**
     * Vérifie si le lock est actuellement détenu
     *
     * @return true si le lock est verrouillé
     */
    public boolean isLocked() {
        return lock.isLocked();
    }

    /**
     * Récupère le nombre de threads en attente du lock
     *
     * @return Nombre de threads en file d'attente
     */
    public int getNombreThreadsEnAttente() {
        return lock.getQueueLength();
    }

    /**
     * Affiche l'état actuel de la synchronisation
     */
    public void afficherEtatSynchronisation() {
        System.out.println("\n=== ÉTAT SYNCHRONISATION ===");
        System.out.println("Lock verrouillé : " + (isLocked() ? "OUI" : "NON"));
        System.out.println("Threads en attente du lock : " + getNombreThreadsEnAttente());
        System.out.println("Permits disponibles : " + getPermitsDisponibles() + " / 5");
        System.out.println("===========================\n");
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("SynchroManager{operations=%d, emprunts=%d, retours=%d, recherches=%d, permits=%d/5}",
                getTotalOperations(),
                compteurEmprunts.get(),
                compteurRetours.get(),
                compteurRecherches.get(),
                semaphore.availablePermits());
    }
}

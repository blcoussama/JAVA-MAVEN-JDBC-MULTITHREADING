package com.bibliotech.threads;

import com.bibliotech.services.EmpruntService;
import com.bibliotech.models.Emprunt;
import com.bibliotech.exceptions.*;

import java.util.concurrent.Callable;

/**
 * Tâche asynchrone d'emprunt de livre (Callable)
 *
 * CONCEPTS TD7-9 :
 * - Callable<V> : Interface permettant de retourner une valeur
 * - Future : Résultat récupéré avec future.get()
 * - ExecutorService : Exécution dans un pool de threads
 *
 * DIFFÉRENCE AVEC RUNNABLE :
 * - Runnable.run() : void, pas de retour
 * - Callable.call() : retourne une valeur (String ici)
 *
 * FONCTIONNEMENT :
 * 1. EmpruntTask est soumis à ExecutorService
 * 2. ExecutorService l'exécute dans un thread
 * 3. call() retourne un String (résultat)
 * 4. Future<String> permet de récupérer ce résultat
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class EmpruntTask implements Callable<String> {

    // ==================== ATTRIBUTS ====================

    private final int idMembre;
    private final int idLivre;
    private final EmpruntService empruntService;
    private final SynchroManager synchroManager;

    // Attributs optionnels
    private final String nomThread;
    private final long delaiAvantExecution;  // En millisecondes

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur simple
     *
     * @param idMembre ID du membre emprunteur
     * @param idLivre ID du livre à emprunter
     * @param empruntService Service d'emprunt
     * @param synchroManager Gestionnaire de synchronisation
     */
    public EmpruntTask(int idMembre, int idLivre,
                       EmpruntService empruntService,
                       SynchroManager synchroManager) {
        this.idMembre = idMembre;
        this.idLivre = idLivre;
        this.empruntService = empruntService;
        this.synchroManager = synchroManager;
        this.nomThread = Thread.currentThread().getName();
        this.delaiAvantExecution = 0;
    }

    /**
     * Constructeur avec délai
     *
     * @param idMembre ID du membre
     * @param idLivre ID du livre
     * @param empruntService Service d'emprunt
     * @param synchroManager Gestionnaire de synchronisation
     * @param delaiMs Délai avant exécution en millisecondes
     */
    public EmpruntTask(int idMembre, int idLivre,
                       EmpruntService empruntService,
                       SynchroManager synchroManager,
                       long delaiMs) {
        this.idMembre = idMembre;
        this.idLivre = idLivre;
        this.empruntService = empruntService;
        this.synchroManager = synchroManager;
        this.nomThread = Thread.currentThread().getName();
        this.delaiAvantExecution = delaiMs;
    }

    // ==================== CALLABLE INTERFACE ====================

    /**
     * Exécute la tâche d'emprunt et retourne le résultat
     *
     * CALLABLE<STRING> (TD7-9) :
     * - Retourne un String avec le résultat de l'opération
     * - "SUCCES:ID_EMPRUNT" si réussi
     * - "ECHEC:MESSAGE_ERREUR" si échoué
     *
     * SYNCHRONISATION (TD10) :
     * - Utilise SynchroManager pour coordination
     * - Acquiert un permit du Semaphore
     * - Incrémente les compteurs atomiques
     *
     * @return String décrivant le résultat ("SUCCES:123" ou "ECHEC:message")
     * @throws Exception si erreur inattendue
     */
    @Override
    public String call() throws Exception {
        String threadName = Thread.currentThread().getName();
        long debut = System.currentTimeMillis();

        try {
            // DÉLAI OPTIONNEL (simulation temps de traitement)
            if (delaiAvantExecution > 0) {
                Thread.sleep(delaiAvantExecution);
            }

            // ACQUISITION PERMIT SEMAPHORE (TD10)
            // Limite à 5 opérations simultanées
            System.out.println("[" + threadName + "] Tentative d'acquisition permit...");
            synchroManager.acquirePermit();

            try {
                System.out.println("[" + threadName + "] Permit acquis. Début emprunt " +
                        "Membre #" + idMembre + " Livre #" + idLivre);

                // EFFECTUER L'EMPRUNT
                Emprunt emprunt = empruntService.effectuerEmprunt(idMembre, idLivre);

                // SUCCÈS
                long duree = System.currentTimeMillis() - debut;
                System.out.println("[" + threadName + "] ✓ Emprunt réussi " +
                        "(ID: " + emprunt.getIdEmprunt() + ") en " + duree + "ms");

                // Incrémenter compteur avec succès
                synchroManager.incrementerCompteur("EMPRUNT", true);

                return "SUCCES:" + emprunt.getIdEmprunt();

            } finally {
                // LIBÉRATION PERMIT (TOUJOURS dans finally)
                synchroManager.releasePermit();
                System.out.println("[" + threadName + "] Permit libéré");
            }

        } catch (MembreNonTrouveException e) {
            long duree = System.currentTimeMillis() - debut;
            System.out.println("[" + threadName + "] ✗ Échec emprunt: Membre introuvable " +
                    "en " + duree + "ms");
            synchroManager.incrementerCompteur("EMPRUNT", false);
            return "ECHEC:MEMBRE_INTROUVABLE:" + e.getMessage();

        } catch (LivreNonTrouveException e) {
            long duree = System.currentTimeMillis() - debut;
            System.out.println("[" + threadName + "] ✗ Échec emprunt: Livre introuvable " +
                    "en " + duree + "ms");
            synchroManager.incrementerCompteur("EMPRUNT", false);
            return "ECHEC:LIVRE_INTROUVABLE:" + e.getMessage();

        } catch (LivreIndisponibleException e) {
            long duree = System.currentTimeMillis() - debut;
            System.out.println("[" + threadName + "] ✗ Échec emprunt: Livre indisponible " +
                    "en " + duree + "ms");
            synchroManager.incrementerCompteur("EMPRUNT", false);
            return "ECHEC:LIVRE_INDISPONIBLE:" + e.getMessage();

        } catch (DatabaseException e) {
            long duree = System.currentTimeMillis() - debut;
            System.out.println("[" + threadName + "] ✗ Échec emprunt: Erreur BD " +
                    "en " + duree + "ms");
            synchroManager.incrementerCompteur("EMPRUNT", false);
            return "ECHEC:DATABASE:" + e.getMessage();

        } catch (InterruptedException e) {
            System.out.println("[" + threadName + "] ⚠ Thread interrompu pendant l'attente");
            Thread.currentThread().interrupt();  // Restaurer flag d'interruption
            synchroManager.incrementerCompteur("EMPRUNT", false);
            return "ECHEC:INTERRUPTED";

        } catch (Exception e) {
            long duree = System.currentTimeMillis() - debut;
            System.err.println("[" + threadName + "] ✗ Erreur inattendue en " + duree + "ms");
            e.printStackTrace();
            synchroManager.incrementerCompteur("EMPRUNT", false);
            return "ECHEC:ERREUR_INATTENDUE:" + e.getMessage();
        }
    }

    // ==================== GETTERS ====================

    public int getIdMembre() {
        return idMembre;
    }

    public int getIdLivre() {
        return idLivre;
    }

    public String getNomThread() {
        return nomThread;
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("EmpruntTask{membre=%d, livre=%d, thread=%s}",
                idMembre, idLivre, nomThread);
    }
}
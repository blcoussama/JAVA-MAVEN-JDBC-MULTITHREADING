package com.bibliotech.threads;

import com.bibliotech.services.EmpruntService;
import com.bibliotech.models.Emprunt;
import com.bibliotech.exceptions.DatabaseException;

import java.util.concurrent.Callable;

/**
 * Tâche asynchrone de retour de livre (Callable)
 *
 * CONCEPTS TD7-9 :
 * - Callable<String> : Retourne résultat du retour
 * - Future : Récupération avec future.get()
 * - Gestion d'exceptions métier
 *
 * FONCTIONNEMENT :
 * 1. Recherche un emprunt en cours disponible
 * 2. Effectue le retour via EmpruntService
 * 3. Retourne "SUCCES:ID_EMPRUNT" ou "ECHEC:MESSAGE"
 * 4. Incrémente compteurs SynchroManager
 *
 * DIFFÉRENCE AVEC EmpruntTask :
 * - Pas besoin de paramètres (trouve un emprunt automatiquement)
 * - Ou peut recevoir un ID d'emprunt spécifique
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class RetourTask implements Callable<String> {

    // ==================== ATTRIBUTS ====================

    private final int idEmprunt;
    private final EmpruntService empruntService;
    private final SynchroManager synchroManager;

    // Attributs optionnels
    private final long delaiAvantExecution;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur simple avec ID d'emprunt spécifique
     *
     * @param idEmprunt ID de l'emprunt à retourner
     * @param empruntService Service d'emprunt
     * @param synchroManager Gestionnaire de synchronisation
     */
    public RetourTask(int idEmprunt,
                      EmpruntService empruntService,
                      SynchroManager synchroManager) {
        this.idEmprunt = idEmprunt;
        this.empruntService = empruntService;
        this.synchroManager = synchroManager;
        this.delaiAvantExecution = 0;
    }

    /**
     * Constructeur avec délai
     *
     * @param idEmprunt ID de l'emprunt
     * @param empruntService Service d'emprunt
     * @param synchroManager Gestionnaire de synchronisation
     * @param delaiMs Délai avant exécution en millisecondes
     */
    public RetourTask(int idEmprunt,
                      EmpruntService empruntService,
                      SynchroManager synchroManager,
                      long delaiMs) {
        this.idEmprunt = idEmprunt;
        this.empruntService = empruntService;
        this.synchroManager = synchroManager;
        this.delaiAvantExecution = delaiMs;
    }

    // ==================== CALLABLE INTERFACE ====================

    /**
     * Exécute la tâche de retour et retourne le résultat
     *
     * CALLABLE<STRING> (TD7-9) :
     * - Retourne "SUCCES:ID_EMPRUNT" si réussi
     * - Retourne "ECHEC:MESSAGE" si échoué
     *
     * SYNCHRONISATION (TD10) :
     * - Utilise SynchroManager.incrementerCompteur()
     * - Acquiert permit du Semaphore
     *
     * NOTIFICATION (TD8) :
     * - EmpruntService.retournerLivre() fait signalAll()
     * - Threads en attente dans effectuerEmpruntAvecAttente() se réveillent
     *
     * @return String décrivant le résultat
     * @throws Exception si erreur inattendue
     */
    @Override
    public String call() throws Exception {
        String threadName = Thread.currentThread().getName();
        long debut = System.currentTimeMillis();

        try {
            // DÉLAI OPTIONNEL
            if (delaiAvantExecution > 0) {
                Thread.sleep(delaiAvantExecution);
            }

            // ACQUISITION PERMIT SEMAPHORE (TD10)
            System.out.println("[" + threadName + "] Tentative d'acquisition permit (retour)...");
            synchroManager.acquirePermit();

            try {
                System.out.println("[" + threadName + "] Permit acquis. Début retour " +
                        "Emprunt #" + idEmprunt);

                // EFFECTUER LE RETOUR
                // Cette méthode fait signalAll() pour réveiller threads en attente
                Emprunt empruntRetourne = empruntService.retournerLivre(idEmprunt);

                // SUCCÈS
                long duree = System.currentTimeMillis() - debut;
                System.out.println("[" + threadName + "] ✓ Retour réussi " +
                        "(Emprunt #" + empruntRetourne.getIdEmprunt() + ") " +
                        "Livre: " + empruntRetourne.getLivre().getTitre() +
                        " en " + duree + "ms");

                // Incrémenter compteur avec succès
                synchroManager.incrementerCompteur("RETOUR", true);

                return "SUCCES:" + empruntRetourne.getIdEmprunt();

            } finally {
                // LIBÉRATION PERMIT (TOUJOURS dans finally)
                synchroManager.releasePermit();
                System.out.println("[" + threadName + "] Permit libéré (retour)");
            }

        } catch (DatabaseException e) {
            long duree = System.currentTimeMillis() - debut;

            if (e.getMessage().contains("introuvable")) {
                System.out.println("[" + threadName + "] ✗ Échec retour: Emprunt introuvable " +
                        "en " + duree + "ms");
                synchroManager.incrementerCompteur("RETOUR", false);
                return "ECHEC:EMPRUNT_INTROUVABLE:" + e.getMessage();

            } else if (e.getMessage().contains("n'est pas en cours")) {
                System.out.println("[" + threadName + "] ✗ Échec retour: Emprunt déjà retourné " +
                        "en " + duree + "ms");
                synchroManager.incrementerCompteur("RETOUR", false);
                return "ECHEC:EMPRUNT_DEJA_RETOURNE:" + e.getMessage();

            } else {
                System.out.println("[" + threadName + "] ✗ Échec retour: Erreur BD " +
                        "en " + duree + "ms");
                synchroManager.incrementerCompteur("RETOUR", false);
                return "ECHEC:DATABASE:" + e.getMessage();
            }

        } catch (InterruptedException e) {
            System.out.println("[" + threadName + "] ⚠ Thread interrompu pendant l'attente");
            Thread.currentThread().interrupt();
            synchroManager.incrementerCompteur("RETOUR", false);
            return "ECHEC:INTERRUPTED";

        } catch (Exception e) {
            long duree = System.currentTimeMillis() - debut;
            System.err.println("[" + threadName + "] ✗ Erreur inattendue en " + duree + "ms");
            e.printStackTrace();
            synchroManager.incrementerCompteur("RETOUR", false);
            return "ECHEC:ERREUR_INATTENDUE:" + e.getMessage();
        }
    }

    // ==================== GETTERS ====================

    public int getIdEmprunt() {
        return idEmprunt;
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("RetourTask{emprunt=%d}", idEmprunt);
    }
}
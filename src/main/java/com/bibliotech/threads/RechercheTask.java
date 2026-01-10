package com.bibliotech.threads;

import com.bibliotech.dao.LivreDAO;
import com.bibliotech.models.Livre;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Tâche asynchrone de recherche de livres (Callable)
 *
 * CONCEPTS TD7-9 :
 * - Callable<List<Livre>> : Retourne une liste de livres
 * - Différent de String (démontre généricité de Callable)
 * - Future<List<Livre>> : Récupération de la liste
 *
 * FONCTIONNEMENT :
 * 1. Effectue une recherche selon le mot-clé
 * 2. Retourne la liste des livres trouvés
 * 3. Liste vide si aucun résultat (pas d'exception)
 * 4. Incrémente compteur SynchroManager
 *
 * UTILISATION :
 * Future<List<Livre>> future = executor.submit(new RechercheTask(...));
 * List<Livre> resultats = future.get();
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class RechercheTask implements Callable<List<Livre>> {

    // ==================== ATTRIBUTS ====================

    private final String motCle;
    private final LivreDAO livreDAO;
    private final SynchroManager synchroManager;

    // Attributs optionnels
    private final long delaiAvantExecution;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur simple
     *
     * @param motCle Mot-clé de recherche (titre, auteur, ISBN)
     * @param livreDAO DAO des livres
     * @param synchroManager Gestionnaire de synchronisation
     */
    public RechercheTask(String motCle,
                         LivreDAO livreDAO,
                         SynchroManager synchroManager) {
        this.motCle = motCle;
        this.livreDAO = livreDAO;
        this.synchroManager = synchroManager;
        this.delaiAvantExecution = 0;
    }

    /**
     * Constructeur avec délai
     *
     * @param motCle Mot-clé de recherche
     * @param livreDAO DAO des livres
     * @param synchroManager Gestionnaire de synchronisation
     * @param delaiMs Délai avant exécution en millisecondes
     */
    public RechercheTask(String motCle,
                         LivreDAO livreDAO,
                         SynchroManager synchroManager,
                         long delaiMs) {
        this.motCle = motCle;
        this.livreDAO = livreDAO;
        this.synchroManager = synchroManager;
        this.delaiAvantExecution = delaiMs;
    }

    // ==================== CALLABLE INTERFACE ====================

    /**
     * Exécute la recherche et retourne la liste de résultats
     *
     * CALLABLE<LIST<LIVRE>> (TD7-9) :
     * - Retourne directement une List<Livre>
     * - Liste vide si aucun résultat (pas null)
     * - Exception seulement si erreur BD
     *
     * STRATÉGIE DE RECHERCHE :
     * - Si commence par "978" → recherche par ISBN
     * - Sinon → recherche par titre
     *
     * @return Liste des livres trouvés (peut être vide, jamais null)
     * @throws Exception si erreur BD
     */
    @Override
    public List<Livre> call() throws Exception {
        String threadName = Thread.currentThread().getName();
        long debut = System.currentTimeMillis();

        try {
            // DÉLAI OPTIONNEL
            if (delaiAvantExecution > 0) {
                Thread.sleep(delaiAvantExecution);
            }

            // ACQUISITION PERMIT SEMAPHORE (TD10)
            System.out.println("[" + threadName + "] Tentative d'acquisition permit (recherche)...");
            synchroManager.acquirePermit();

            try {
                System.out.println("[" + threadName + "] Permit acquis. Recherche: \"" + motCle + "\"");

                List<Livre> resultats;

                // STRATÉGIE DE RECHERCHE
                if (motCle == null || motCle.trim().isEmpty()) {
                    // Aucun critère : tous les livres
                    resultats = livreDAO.findAll();

                } else if (motCle.startsWith("978")) {
                    // Recherche par ISBN
                    resultats = livreDAO.findByIsbn(motCle)
                            .map(List::of)
                            .orElse(List.of());

                } else {
                    // Recherche par titre (défaut)
                    resultats = livreDAO.rechercherParTitre(motCle);
                }

                // SUCCÈS
                long duree = System.currentTimeMillis() - debut;
                System.out.println("[" + threadName + "] ✓ Recherche terminée: " +
                        resultats.size() + " résultat(s) trouvé(s) en " + duree + "ms");

                // Afficher les 3 premiers résultats
                if (!resultats.isEmpty()) {
                    int count = 0;
                    for (Livre livre : resultats) {
                        if (count < 3) {
                            System.out.println("  - " + livre.getTitre() + " par " +
                                    livre.getAuteur().getNom());
                        }
                        count++;
                    }
                    if (count > 3) {
                        System.out.println("  ... et " + (count - 3) + " autre(s)");
                    }
                }

                // Incrémenter compteur
                synchroManager.incrementerCompteur("RECHERCHE");

                return resultats;

            } finally {
                // LIBÉRATION PERMIT
                synchroManager.releasePermit();
                System.out.println("[" + threadName + "] Permit libéré (recherche)");
            }

        } catch (DatabaseException e) {
            long duree = System.currentTimeMillis() - debut;
            System.err.println("[" + threadName + "] ✗ Échec recherche: Erreur BD " +
                    "en " + duree + "ms");
            e.printStackTrace();
            synchroManager.incrementerCompteur("RECHERCHE");
            // Retourner liste vide plutôt que throw
            return List.of();

        } catch (InterruptedException e) {
            System.out.println("[" + threadName + "] ⚠ Thread interrompu pendant l'attente");
            Thread.currentThread().interrupt();
            synchroManager.incrementerCompteur("RECHERCHE");
            return List.of();

        } catch (Exception e) {
            long duree = System.currentTimeMillis() - debut;
            System.err.println("[" + threadName + "] ✗ Erreur inattendue en " + duree + "ms");
            e.printStackTrace();
            synchroManager.incrementerCompteur("RECHERCHE");
            return List.of();
        }
    }

    // ==================== GETTERS ====================

    public String getMotCle() {
        return motCle;
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("RechercheTask{motCle='%s'}", motCle);
    }
}

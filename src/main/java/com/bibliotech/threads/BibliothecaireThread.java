package com.bibliotech.threads;

import com.bibliotech.services.EmpruntService;
import com.bibliotech.services.BibliothequeService;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Livre;
import com.bibliotech.exceptions.*;

import java.util.List;
import java.util.Random;

/**
 * Thread simulant un bibliothécaire effectuant des opérations aléatoires
 *
 * CONCEPTS TD6 :
 * - Extends Thread : Création de thread par héritage
 * - run() : Méthode exécutée dans le thread
 * - Thread.sleep() : Pause entre opérations
 * - Thread.currentThread().getName() : Identification du thread
 *
 * FONCTIONNEMENT :
 * 1. Le thread démarre avec start()
 * 2. run() exécute N opérations en boucle
 * 3. Chaque opération est tirée aléatoirement :
 *    - 40% emprunt
 *    - 40% retour
 *    - 20% recherche
 * 4. Pause aléatoire entre opérations (500-2000ms)
 * 5. Utilise SynchroManager pour coordination
 *
 * UTILISATION :
 * BibliothecaireThread b1 = new BibliothecaireThread("Alice", ...);
 * b1.start();  // Démarre le thread
 * b1.join();   // Attend la fin du thread
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class BibliothecaireThread extends Thread {

    // ==================== ATTRIBUTS ====================

    private final String nomBibliothecaire;
    private final EmpruntService empruntService;
    private final BibliothequeService bibliothequeService;
    private final SynchroManager synchroManager;

    // Configuration des opérations
    private final int nombreOperations;
    private final Random random;

    // IDs disponibles pour les opérations
    private static final int[] MEMBRES_IDS = {1, 2, 3, 4, 5, 6};  // IDs membres existants
    private static final int[] LIVRES_IDS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};  // IDs livres existants

    // Compteurs locaux (non thread-safe, juste pour ce thread)
    private int empruntsReussis = 0;
    private int empruntsEchoues = 0;
    private int retoursReussis = 0;
    private int retoursEchoues = 0;
    private int recherchesEffectuees = 0;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur complet
     *
     * @param nomBibliothecaire Nom du bibliothécaire (pour identification)
     * @param empruntService Service d'emprunt
     * @param bibliothequeService Service de bibliothèque
     * @param synchroManager Gestionnaire de synchronisation
     * @param nombreOperations Nombre d'opérations à effectuer
     */
    public BibliothecaireThread(String nomBibliothecaire,
                                EmpruntService empruntService,
                                BibliothequeService bibliothequeService,
                                SynchroManager synchroManager,
                                int nombreOperations) {
        super("Bibliothecaire-" + nomBibliothecaire);  // Nom du thread
        this.nomBibliothecaire = nomBibliothecaire;
        this.empruntService = empruntService;
        this.bibliothequeService = bibliothequeService;
        this.synchroManager = synchroManager;
        this.nombreOperations = nombreOperations;
        this.random = new Random();
    }

    /**
     * Constructeur simplifié (10 opérations par défaut)
     */
    public BibliothecaireThread(String nomBibliothecaire,
                                EmpruntService empruntService,
                                BibliothequeService bibliothequeService,
                                SynchroManager synchroManager) {
        this(nomBibliothecaire, empruntService, bibliothequeService, synchroManager, 10);
    }

    // ==================== RUN METHOD (TD6) ====================

    /**
     * Méthode exécutée par le thread
     *
     * BOUCLE PRINCIPALE :
     * - Effectue N opérations aléatoires
     * - Pause entre chaque opération (500-2000ms)
     * - Gère toutes les exceptions
     * - Affiche résumé à la fin
     *
     * DISTRIBUTION DES OPÉRATIONS :
     * - 0-39 : Emprunt (40%)
     * - 40-79 : Retour (40%)
     * - 80-99 : Recherche (20%)
     */
    @Override
    public void run() {
        System.out.println("\n[" + getName() + "] 🚀 Démarrage - " +
                nombreOperations + " opérations à effectuer");

        long debut = System.currentTimeMillis();

        for (int i = 1; i <= nombreOperations; i++) {
            try {
                System.out.println("\n[" + getName() + "] --- Opération " + i + "/" +
                        nombreOperations + " ---");

                // Tirer opération aléatoire
                int typeOperation = random.nextInt(100);

                if (typeOperation < 40) {
                    // 40% : EMPRUNT
                    effectuerEmprunt();

                } else if (typeOperation < 80) {
                    // 40% : RETOUR
                    effectuerRetour();

                } else {
                    // 20% : RECHERCHE
                    effectuerRecherche();
                }

                // PAUSE ALÉATOIRE entre opérations (simulation temps de traitement)
                int pauseMs = 500 + random.nextInt(1500);  // 500-2000ms
                System.out.println("[" + getName() + "] ⏸ Pause " + pauseMs + "ms");
                Thread.sleep(pauseMs);

            } catch (InterruptedException e) {
                System.err.println("[" + getName() + "] ⚠ Thread interrompu !");
                Thread.currentThread().interrupt();
                break;

            } catch (Exception e) {
                System.err.println("[" + getName() + "] ✗ Erreur inattendue : " +
                        e.getMessage());
                e.printStackTrace();
            }
        }

        // FIN DU THREAD - Afficher résumé
        long duree = System.currentTimeMillis() - debut;
        afficherResume(duree);
    }

    // ==================== OPÉRATION 1 : EMPRUNT ====================

    /**
     * Effectue un emprunt aléatoire
     *
     * PROCESSUS :
     * 1. Choisir membre aléatoire
     * 2. Choisir livre aléatoire
     * 3. Tenter emprunt via EmpruntService
     * 4. Gérer succès/échec
     */
    private void effectuerEmprunt() {
        // Choisir IDs aléatoires
        int idMembre = MEMBRES_IDS[random.nextInt(MEMBRES_IDS.length)];
        int idLivre = LIVRES_IDS[random.nextInt(LIVRES_IDS.length)];

        System.out.println("[" + getName() + "] 📖 Tentative emprunt " +
                "Membre #" + idMembre + " Livre #" + idLivre);

        try {
            // EFFECTUER EMPRUNT
            Emprunt emprunt = empruntService.effectuerEmprunt(idMembre, idLivre);

            // SUCCÈS
            System.out.println("[" + getName() + "] ✅ Emprunt réussi " +
                    "(ID: " + emprunt.getIdEmprunt() + ") - " +
                    "Livre: " + emprunt.getLivre().getTitre());
            empruntsReussis++;
            synchroManager.incrementerCompteur("EMPRUNT", true);

        } catch (MembreNonTrouveException e) {
            System.out.println("[" + getName() + "] ❌ Échec: Membre introuvable");
            empruntsEchoues++;
            synchroManager.incrementerCompteur("EMPRUNT", false);

        } catch (LivreNonTrouveException e) {
            System.out.println("[" + getName() + "] ❌ Échec: Livre introuvable");
            empruntsEchoues++;
            synchroManager.incrementerCompteur("EMPRUNT", false);

        } catch (LivreIndisponibleException e) {
            System.out.println("[" + getName() + "] ❌ Échec: Livre indisponible");
            empruntsEchoues++;
            synchroManager.incrementerCompteur("EMPRUNT", false);

        } catch (DatabaseException e) {
            System.out.println("[" + getName() + "] ❌ Échec: " + e.getMessage());
            empruntsEchoues++;
            synchroManager.incrementerCompteur("EMPRUNT", false);
        }
    }

    // ==================== OPÉRATION 2 : RETOUR ====================

    /**
     * Effectue le retour d'un emprunt en cours
     *
     * PROCESSUS :
     * 1. Récupérer liste des emprunts en cours
     * 2. Si liste vide, échec
     * 3. Choisir un emprunt aléatoire
     * 4. Effectuer retour via EmpruntService
     */
    private void effectuerRetour() {
        try {
            // RÉCUPÉRER EMPRUNTS EN COURS
            List<Emprunt> empruntsEnCours = empruntService.getEmpruntsEnCours();

            if (empruntsEnCours.isEmpty()) {
                System.out.println("[" + getName() + "] ℹ Aucun emprunt en cours à retourner");
                return;
            }

            // CHOISIR EMPRUNT ALÉATOIRE
            Emprunt empruntARetourner = empruntsEnCours.get(
                    random.nextInt(empruntsEnCours.size()));

            int idEmprunt = empruntARetourner.getIdEmprunt();

            System.out.println("[" + getName() + "] 📥 Tentative retour Emprunt #" + idEmprunt +
                    " - Livre: " + empruntARetourner.getLivre().getTitre());

            // EFFECTUER RETOUR
            Emprunt empruntRetourne = empruntService.retournerLivre(idEmprunt);

            // SUCCÈS
            System.out.println("[" + getName() + "] ✅ Retour réussi " +
                    "(Emprunt #" + empruntRetourne.getIdEmprunt() + ") - " +
                    "Durée: " + empruntRetourne.calculerDuree() + " jours");
            retoursReussis++;
            synchroManager.incrementerCompteur("RETOUR", true);

        } catch (DatabaseException e) {
            System.out.println("[" + getName() + "] ❌ Échec retour: " + e.getMessage());
            retoursEchoues++;
            synchroManager.incrementerCompteur("RETOUR", false);
        }
    }

    // ==================== OPÉRATION 3 : RECHERCHE ====================

    /**
     * Effectue une recherche de livres
     *
     * PROCESSUS :
     * 1. Choisir mot-clé aléatoire parmi une liste
     * 2. Effectuer recherche via BibliothequeService
     * 3. Afficher nombre de résultats
     */
    private void effectuerRecherche() {
        // Liste de mots-clés possibles
        String[] motsCles = {
                "Java",
                "Clean",
                "Design",
                "Effective",
                "Code",
                "Patterns",
                "Programming",
                "Software"
        };

        String motCle = motsCles[random.nextInt(motsCles.length)];

        System.out.println("[" + getName() + "] 🔍 Recherche: \"" + motCle + "\"");

        try {
            // EFFECTUER RECHERCHE
            List<Livre> resultats = bibliothequeService.rechercherLivres(motCle);

            // SUCCÈS
            System.out.println("[" + getName() + "] ✅ Recherche terminée: " +
                    resultats.size() + " résultat(s)");

            // Afficher quelques résultats
            if (!resultats.isEmpty()) {
                int count = 0;
                for (Livre livre : resultats) {
                    if (count < 2) {  // Afficher 2 premiers
                        System.out.println("  - " + livre.getTitre() + " (" +
                                (livre.estDisponible() ? "Disponible" : "Indisponible") + ")");
                    }
                    count++;
                }
                if (count > 2) {
                    System.out.println("  ... et " + (count - 2) + " autre(s)");
                }
            }

            recherchesEffectuees++;
            synchroManager.incrementerCompteur("RECHERCHE");

        } catch (DatabaseException e) {
            System.out.println("[" + getName() + "] ❌ Échec recherche: " + e.getMessage());
            recherchesEffectuees++;  // Compter quand même
            synchroManager.incrementerCompteur("RECHERCHE");
        }
    }

    // ==================== RÉSUMÉ FINAL ====================

    /**
     * Affiche le résumé des opérations du thread
     *
     * @param dureeTotale Durée totale d'exécution en millisecondes
     */
    private void afficherResume(long dureeTotale) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         RÉSUMÉ - " + getName() + " (" + nomBibliothecaire + ")");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        int totalOperations = empruntsReussis + empruntsEchoues +
                retoursReussis + retoursEchoues + recherchesEffectuees;

        System.out.printf("║ Total opérations          : %5d                          ║%n",
                totalOperations);
        System.out.println("║                                                           ║");

        System.out.printf("║ • Emprunts                : %5d                          ║%n",
                empruntsReussis + empruntsEchoues);
        System.out.printf("║   - Réussis               : %5d                          ║%n",
                empruntsReussis);
        System.out.printf("║   - Échoués               : %5d                          ║%n",
                empruntsEchoues);

        System.out.println("║                                                           ║");

        System.out.printf("║ • Retours                 : %5d                          ║%n",
                retoursReussis + retoursEchoues);
        System.out.printf("║   - Réussis               : %5d                          ║%n",
                retoursReussis);
        System.out.printf("║   - Échoués               : %5d                          ║%n",
                retoursEchoues);

        System.out.println("║                                                           ║");

        System.out.printf("║ • Recherches              : %5d                          ║%n",
                recherchesEffectuees);

        System.out.println("║                                                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        long secondes = dureeTotale / 1000;
        System.out.printf("║ Durée totale              : %d secondes                   ║%n",
                secondes);

        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
    }

    // ==================== GETTERS ====================

    public String getNomBibliothecaire() {
        return nomBibliothecaire;
    }

    public int getEmpruntsReussis() {
        return empruntsReussis;
    }

    public int getEmpruntsEchoues() {
        return empruntsEchoues;
    }

    public int getRetoursReussis() {
        return retoursReussis;
    }

    public int getRetoursEchoues() {
        return retoursEchoues;
    }

    public int getRecherchesEffectuees() {
        return recherchesEffectuees;
    }

    public int getTotalOperations() {
        return empruntsReussis + empruntsEchoues + retoursReussis +
                retoursEchoues + recherchesEffectuees;
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("BibliothecaireThread{nom='%s', operations=%d/%d}",
                nomBibliothecaire, getTotalOperations(), nombreOperations);
    }
}

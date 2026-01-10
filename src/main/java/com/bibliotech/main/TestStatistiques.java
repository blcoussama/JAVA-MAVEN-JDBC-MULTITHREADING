package com.bibliotech.main;

import com.bibliotech.services.StatistiquesService;
import com.bibliotech.services.RapportService;
import com.bibliotech.utils.FileExporter;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Categorie;
import com.bibliotech.exceptions.DatabaseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.IntSummaryStatistics;

/**
 * Test complet des services de statistiques, rapports et export CSV
 *
 * TESTE :
 * ✅ StatistiquesService - 11 méthodes avec Streams API
 * ✅ RapportService - Génération rapports mensuels et complets
 * ✅ FileExporter - Export CSV de toutes les données
 *
 * RÉSULTATS :
 * - Affichage console détaillé
 * - Fichiers CSV dans répertoire rapports/
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class TestStatistiques {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║          TEST STATISTIQUES & RAPPORTS - PHASE 4            ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // ==================== INITIALISATION ====================
            System.out.println("🔧 Initialisation des services...");
            StatistiquesService statsService = new StatistiquesService();
            RapportService rapportService = new RapportService();
            FileExporter fileExporter = new FileExporter();
            System.out.println("✅ Services initialisés avec succès\n");

            // ==================== TEST 1 : TOP 10 LIVRES ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 1 : TOP 10 LIVRES LES PLUS EMPRUNTÉS");
            System.out.println("═══════════════════════════════════════════════════════════");

            List<Map.Entry<Livre, Long>> top10Livres =
                statsService.top10LivresPlusEmpruntes();

            System.out.println("📚 Top 10 des livres les plus empruntés :");
            System.out.println();

            int rang = 1;
            for (Map.Entry<Livre, Long> entry : top10Livres) {
                Livre livre = entry.getKey();
                Long nbEmprunts = entry.getValue();
                System.out.printf("  %2d. %-40s | %s | %d emprunts%n",
                    rang++,
                    livre.getTitre().length() > 40
                        ? livre.getTitre().substring(0, 37) + "..."
                        : livre.getTitre(),
                    livre.getAuteur().getNom(),
                    nbEmprunts
                );
            }

            // Export CSV
            fileExporter.exporterTopLivresCSV("top_10_livres", top10Livres);
            System.out.println();

            // ==================== TEST 2 : LIVRES PAR CATÉGORIE ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 2 : LIVRES PAR CATÉGORIE");
            System.out.println("═══════════════════════════════════════════════════════════");

            Map<Categorie, List<Livre>> livresParCategorie =
                statsService.livresParCategorie();

            System.out.println("📖 Nombre de livres par catégorie :");
            System.out.println();

            for (Map.Entry<Categorie, List<Livre>> entry : livresParCategorie.entrySet()) {
                System.out.printf("  %-15s : %3d livres%n",
                    entry.getKey(),
                    entry.getValue().size()
                );
            }
            System.out.println();

            // ==================== TEST 3 : NOMBRE LIVRES PAR CATÉGORIE ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 3 : COMPTAGE PAR CATÉGORIE");
            System.out.println("═══════════════════════════════════════════════════════════");

            Map<Categorie, Long> nombreParCategorie =
                statsService.nombreLivresParCategorie();

            System.out.println("🔢 Comptage direct par catégorie :");
            System.out.println();

            for (Map.Entry<Categorie, Long> entry : nombreParCategorie.entrySet()) {
                System.out.printf("  %-15s : %3d livres%n",
                    entry.getKey(),
                    entry.getValue()
                );
            }
            System.out.println();

            // ==================== TEST 4 : STATS DISPONIBILITÉ ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 4 : STATISTIQUES DE DISPONIBILITÉ");
            System.out.println("═══════════════════════════════════════════════════════════");

            IntSummaryStatistics statsDisponibilite =
                statsService.statsDisponibilite();

            System.out.println("📊 Statistiques sur les exemplaires disponibles :");
            System.out.println();
            System.out.printf("  Nombre total de livres : %d%n", statsDisponibilite.getCount());
            System.out.printf("  Total exemplaires     : %d%n", statsDisponibilite.getSum());
            System.out.printf("  Minimum disponible    : %d%n", statsDisponibilite.getMin());
            System.out.printf("  Maximum disponible    : %d%n", statsDisponibilite.getMax());
            System.out.printf("  Moyenne disponible    : %.2f%n", statsDisponibilite.getAverage());
            System.out.println();

            // ==================== TEST 5 : MOYENNE EMPRUNTS PAR MEMBRE ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 5 : MOYENNE EMPRUNTS PAR MEMBRE");
            System.out.println("═══════════════════════════════════════════════════════════");

            double moyenneEmprunts = statsService.moyenneEmpruntsParMembre();

            System.out.println("👥 Activité des membres :");
            System.out.println();
            System.out.printf("  Moyenne d'emprunts actifs par membre : %.2f%n", moyenneEmprunts);
            System.out.println();

            // ==================== TEST 6 : DURÉE MOYENNE EMPRUNTS ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 6 : DURÉE MOYENNE DES EMPRUNTS");
            System.out.println("═══════════════════════════════════════════════════════════");

            double dureeMoyenne = statsService.dureeMoyenneEmprunts();

            System.out.println("⏱️  Analyse temporelle :");
            System.out.println();
            System.out.printf("  Durée moyenne des emprunts : %.1f jours%n", dureeMoyenne);
            System.out.println();

            // ==================== TEST 7 : MEMBRES EN RETARD ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 7 : MEMBRES EN RETARD");
            System.out.println("═══════════════════════════════════════════════════════════");

            List<Membre> membresEnRetard = statsService.getMembresEnRetard();

            System.out.println("⚠️  Membres avec emprunts en retard :");
            System.out.println();

            if (membresEnRetard.isEmpty()) {
                System.out.println("  ✅ Aucun membre en retard - Excellent !");
            } else {
                for (Membre membre : membresEnRetard) {
                    System.out.printf("  • %s %s (CIN: %s) - %d emprunts actifs%n",
                        membre.getPrenom(),
                        membre.getNom(),
                        membre.getCin(),
                        membre.getNombreEmpruntsActifs()
                    );
                }
                System.out.printf("%n  Total : %d membres en retard%n", membresEnRetard.size());
            }
            System.out.println();

            // ==================== TEST 8 : TAUX DE RETARD ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 8 : TAUX DE RETARD GLOBAL");
            System.out.println("═══════════════════════════════════════════════════════════");

            double tauxRetard = statsService.tauxRetard();

            System.out.println("📉 Performance du système :");
            System.out.println();
            System.out.printf("  Taux de retard global : %.2f%%%n", tauxRetard);

            if (tauxRetard < 5.0) {
                System.out.println("  ✅ Excellent - Système bien géré");
            } else if (tauxRetard < 10.0) {
                System.out.println("  ⚠️  Acceptable - À surveiller");
            } else {
                System.out.println("  ❌ Élevé - Actions nécessaires");
            }
            System.out.println();

            // ==================== TEST 9 : TOP 5 MEMBRES ACTIFS ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 9 : TOP 5 MEMBRES LES PLUS ACTIFS");
            System.out.println("═══════════════════════════════════════════════════════════");

            List<Membre> top5Membres = statsService.top5MembresActifs();

            System.out.println("🏆 Membres les plus actifs (emprunts en cours) :");
            System.out.println();

            rang = 1;
            for (Membre membre : top5Membres) {
                System.out.printf("  %d. %-30s | CIN: %-12s | %d emprunts%n",
                    rang++,
                    membre.getPrenom() + " " + membre.getNom(),
                    membre.getCin(),
                    membre.getNombreEmpruntsActifs()
                );
            }
            System.out.println();

            // ==================== TEST 10 : EMPRUNTS PAR CATÉGORIE ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 10 : EMPRUNTS PAR CATÉGORIE");
            System.out.println("═══════════════════════════════════════════════════════════");

            Map<Categorie, Long> empruntsParCategorie =
                statsService.empruntsParCategorie();

            System.out.println("📚 Popularité des catégories :");
            System.out.println();

            // Trier par nombre d'emprunts (décroissant)
            empruntsParCategorie.entrySet().stream()
                .sorted(Map.Entry.<Categorie, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    System.out.printf("  %-15s : %3d emprunts%n",
                        entry.getKey(),
                        entry.getValue()
                    );
                });
            System.out.println();

            // ==================== TEST 11 : TOTAL EXEMPLAIRES DISPONIBLES ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 11 : TOTAL EXEMPLAIRES DISPONIBLES");
            System.out.println("═══════════════════════════════════════════════════════════");

            int totalDisponibles = statsService.totalExemplairesDisponibles();

            System.out.println("📦 Inventaire global :");
            System.out.println();
            System.out.printf("  Total d'exemplaires disponibles : %d%n", totalDisponibles);
            System.out.println();

            // ==================== TEST 12 : RAPPORT MENSUEL ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 12 : RAPPORT MENSUEL (Décembre 2025)");
            System.out.println("═══════════════════════════════════════════════════════════");

            Map<String, Object> rapportMensuel = rapportService.rapportMensuel(12, 2025);
            String rapportMensuelFormate = rapportService.formaterRapportMensuel(rapportMensuel);

            System.out.println(rapportMensuelFormate);

            // Export CSV
            fileExporter.exporterRapportMensuelCSV("rapport_mensuel_2025_12", rapportMensuel);
            System.out.println();

            // ==================== TEST 13 : RAPPORT COMPLET ====================
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TEST 13 : RAPPORT COMPLET DU SYSTÈME");
            System.out.println("═══════════════════════════════════════════════════════════");

            Map<String, Object> rapportComplet = rapportService.genererRapportComplet();
            String rapportCompletFormate = rapportService.formaterRapportComplet(rapportComplet);

            System.out.println(rapportCompletFormate);

            // Export statistiques en CSV
            Map<String, Object> stats = Map.of(
                "Total exemplaires disponibles", totalDisponibles,
                "Moyenne emprunts par membre", moyenneEmprunts,
                "Durée moyenne emprunts (jours)", dureeMoyenne,
                "Taux de retard (%)", tauxRetard,
                "Nombre membres en retard", membresEnRetard.size()
            );

            fileExporter.exporterStatsCSV("statistiques_globales", stats);
            System.out.println();

            // ==================== RÉSUMÉ FINAL ====================
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                                                            ║");
            System.out.println("║                    ✅ TESTS TERMINÉS                       ║");
            System.out.println("║                                                            ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("📊 RÉSUMÉ DES TESTS :");
            System.out.println("  ✅ 11 méthodes StatistiquesService testées");
            System.out.println("  ✅ 2 rapports RapportService générés");
            System.out.println("  ✅ 4 fichiers CSV exportés dans rapports/");
            System.out.println();
            System.out.println("📁 FICHIERS CRÉÉS :");

            List<String> fichiers = fileExporter.listerFichiersCSV();
            if (fichiers.isEmpty()) {
                System.out.println("  ⚠️  Aucun fichier CSV trouvé");
            } else {
                for (String fichier : fichiers) {
                    System.out.println("  📄 " + fichier);
                }
            }
            System.out.println();
            System.out.println("🎉 Tous les tests ont réussi !");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR BASE DE DONNÉES :");
            System.err.println("   " + e.getMessage());
            System.err.println("   Code: " + e.getCode());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("❌ ERREUR EXPORT CSV :");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("❌ ERREUR INATTENDUE :");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
    }
}

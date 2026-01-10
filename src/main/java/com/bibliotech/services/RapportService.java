package com.bibliotech.services;

import com.bibliotech.dao.EmpruntDAO;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Categorie;
import com.bibliotech.exceptions.DatabaseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Service de génération de rapports statistiques
 *
 * Génère des rapports détaillés en utilisant StatistiquesService
 * et en filtrant les données par période.
 *
 * UTILISE STREAMS POUR FILTRAGE PAR DATE :
 * ✅ filter() avec LocalDate
 * ✅ Comparaison de dates
 * ✅ groupingBy() sur données filtrées
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class RapportService {

    // ==================== ATTRIBUTS ====================

    private final EmpruntDAO empruntDAO;
    private final StatistiquesService statistiquesService;

    // ==================== CONSTRUCTEUR ====================

    /**
     * Constructeur avec injection des dépendances
     */
    public RapportService() {
        this.empruntDAO = new EmpruntDAO();
        this.statistiquesService = new StatistiquesService();
    }

    /**
     * Constructeur avec dépendances personnalisées (pour tests)
     *
     * @param empruntDAO DAO des emprunts
     * @param statistiquesService Service de statistiques
     */
    public RapportService(EmpruntDAO empruntDAO, StatistiquesService statistiquesService) {
        this.empruntDAO = empruntDAO;
        this.statistiquesService = statistiquesService;
    }

    // ==================== RAPPORTS MENSUELS ====================

    /**
     * Génère un rapport mensuel complet
     *
     * STREAM COMPLEXE :
     * 1. Filtrer emprunts du mois (filter avec dates)
     * 2. Calculer statistiques sur emprunts filtrés
     * 3. Grouper par catégorie
     * 4. Identifier top membres
     *
     * CONCEPTS STREAMS :
     * ✅ filter() avec LocalDate
     * ✅ getMonthValue() et getYear()
     * ✅ groupingBy() + counting()
     * ✅ sorted() + limit()
     *
     * Le rapport contient :
     * - "total_emprunts" : nombre total d'emprunts du mois
     * - "par_categorie" : Map<Categorie, Long> emprunts par catégorie
     * - "top_5_membres" : Liste des 5 membres les plus actifs du mois
     * - "top_5_livres" : Liste des 5 livres les plus empruntés du mois
     * - "taux_retard" : Pourcentage d'emprunts en retard ce mois
     * - "duree_moyenne" : Durée moyenne des emprunts (en jours)
     *
     * @param mois Mois (1-12)
     * @param annee Année (ex: 2025)
     * @return Map contenant toutes les statistiques du mois
     * @throws DatabaseException si erreur BD
     */
    public Map<String, Object> rapportMensuel(int mois, int annee) 
            throws DatabaseException {
        
        // VALIDATION
        if (mois < 1 || mois > 12) {
            throw new DatabaseException(
                "Mois invalide : " + mois + " (doit être entre 1 et 12)", null);
        }

        if (annee < 2000 || annee > 2100) {
            throw new DatabaseException(
                "Année invalide : " + annee, null);
        }

        // Récupérer tous les emprunts
        List<Emprunt> tousEmprunts = empruntDAO.findAll();

        // ===== FILTRER LES EMPRUNTS DU MOIS =====
        List<Emprunt> empruntsMois = tousEmprunts.stream()
            .filter(e -> e.getDateEmprunt().getMonthValue() == mois)
            .filter(e -> e.getDateEmprunt().getYear() == annee)
            .collect(Collectors.toList());

        // Créer le rapport
        Map<String, Object> rapport = new HashMap<>();

        // ===== STATISTIQUE 1 : Total emprunts =====
        rapport.put("total_emprunts", empruntsMois.size());

        // ===== STATISTIQUE 2 : Emprunts par catégorie =====
        Map<Categorie, Long> parCategorie = empruntsMois.stream()
            .collect(Collectors.groupingBy(
                e -> e.getLivre().getCategorie(),
                Collectors.counting()
            ));
        rapport.put("par_categorie", parCategorie);

        // ===== STATISTIQUE 3 : Top 5 membres du mois =====
        List<Map.Entry<Membre, Long>> top5Membres = empruntsMois.stream()
            .collect(Collectors.groupingBy(
                Emprunt::getMembre,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<Membre, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
        rapport.put("top_5_membres", top5Membres);

        // ===== STATISTIQUE 4 : Top 5 livres du mois =====
        List<Map.Entry<Livre, Long>> top5Livres = empruntsMois.stream()
            .collect(Collectors.groupingBy(
                Emprunt::getLivre,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<Livre, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
        rapport.put("top_5_livres", top5Livres);

        // ===== STATISTIQUE 5 : Taux de retard ce mois =====
        if (!empruntsMois.isEmpty()) {
            long enRetard = empruntsMois.stream()
                .filter(Emprunt::estEnRetard)
                .count();
            double tauxRetard = (enRetard * 100.0) / empruntsMois.size();
            rapport.put("taux_retard", tauxRetard);
        } else {
            rapport.put("taux_retard", 0.0);
        }

        // ===== STATISTIQUE 6 : Durée moyenne des emprunts =====
        double dureeMoyenne = empruntsMois.stream()
            .filter(e -> e.getDateRetourEffective() != null)
            .mapToLong(Emprunt::calculerDuree)
            .average()
            .orElse(0.0);
        rapport.put("duree_moyenne", dureeMoyenne);

        // ===== STATISTIQUE 7 : Période du rapport =====
        rapport.put("mois", mois);
        rapport.put("annee", annee);

        return rapport;
    }

    /**
     * Génère un rapport complet de l'activité globale
     *
     * Combine toutes les statistiques disponibles sans filtrage temporel.
     *
     * Le rapport contient :
     * - "top_10_livres" : Top 10 livres les plus empruntés
     * - "livres_par_categorie" : Nombre de livres par catégorie
     * - "emprunts_par_categorie" : Nombre d'emprunts par catégorie
     * - "moyenne_emprunts_membre" : Moyenne d'emprunts actifs par membre
     * - "duree_moyenne_emprunts" : Durée moyenne des emprunts
     * - "top_5_membres_actifs" : Top 5 membres avec le plus d'emprunts actifs
     * - "taux_retard_global" : Taux de retard global
     * - "stats_disponibilite" : Statistiques sur disponibilité des livres
     * - "membres_en_retard" : Nombre de membres ayant au moins un retard
     * - "total_exemplaires_disponibles" : Total d'exemplaires disponibles
     *
     * @return Map contenant toutes les statistiques globales
     * @throws DatabaseException si erreur BD
     */
    public Map<String, Object> genererRapportComplet() 
            throws DatabaseException {
        
        Map<String, Object> rapport = new HashMap<>();

        // ===== UTILISER StatistiquesService pour chaque statistique =====

        // Top 10 livres
        rapport.put("top_10_livres", 
            statistiquesService.top10LivresPlusEmpruntes());

        // Livres par catégorie
        rapport.put("livres_par_categorie", 
            statistiquesService.nombreLivresParCategorie());

        // Emprunts par catégorie
        rapport.put("emprunts_par_categorie", 
            statistiquesService.empruntsParCategorie());

        // Moyenne emprunts par membre
        rapport.put("moyenne_emprunts_membre", 
            statistiquesService.moyenneEmpruntsParMembre());

        // Durée moyenne emprunts
        rapport.put("duree_moyenne_emprunts", 
            statistiquesService.dureeMoyenneEmprunts());

        // Top 5 membres actifs
        rapport.put("top_5_membres_actifs", 
            statistiquesService.top5MembresActifs());

        // Taux de retard global
        rapport.put("taux_retard_global", 
            statistiquesService.tauxRetard());

        // Statistiques disponibilité
        rapport.put("stats_disponibilite", 
            statistiquesService.statsDisponibilite());

        // Membres en retard
        List<Membre> membresEnRetard = statistiquesService.getMembresEnRetard();
        rapport.put("membres_en_retard", membresEnRetard.size());
        rapport.put("liste_membres_en_retard", membresEnRetard);

        // Total exemplaires disponibles
        rapport.put("total_exemplaires_disponibles", 
            statistiquesService.totalExemplairesDisponibles());

        // ===== Métadonnées du rapport =====
        rapport.put("date_generation", LocalDate.now());
        rapport.put("type_rapport", "COMPLET");

        return rapport;
    }

    // ==================== UTILITAIRES ====================

    /**
     * Formate un rapport mensuel en texte lisible
     *
     * Convertit le Map du rapport en String formatée pour affichage console
     *
     * @param rapport Rapport généré par rapportMensuel()
     * @return String formatée du rapport
     */
    public String formaterRapportMensuel(Map<String, Object> rapport) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n========================================\n");
        sb.append("   RAPPORT MENSUEL\n");
        sb.append("========================================\n\n");

        // Période
        sb.append(String.format("Période : %02d/%d\n\n", 
            rapport.get("mois"), rapport.get("annee")));

        // Total emprunts
        sb.append(String.format("Total emprunts : %d\n\n", 
            rapport.get("total_emprunts")));

        // Par catégorie
        sb.append("Emprunts par catégorie :\n");
        @SuppressWarnings("unchecked")
        Map<Categorie, Long> parCategorie = 
            (Map<Categorie, Long>) rapport.get("par_categorie");
        parCategorie.forEach((cat, count) -> 
            sb.append(String.format("  - %s : %d\n", cat, count))
        );
        sb.append("\n");

        // Taux de retard
        sb.append(String.format("Taux de retard : %.2f%%\n\n", 
            rapport.get("taux_retard")));

        // Durée moyenne
        sb.append(String.format("Durée moyenne : %.1f jours\n\n", 
            rapport.get("duree_moyenne")));

        // Top 5 membres
        sb.append("Top 5 membres :\n");
        @SuppressWarnings("unchecked")
        List<Map.Entry<Membre, Long>> top5Membres = 
            (List<Map.Entry<Membre, Long>>) rapport.get("top_5_membres");
        int rang = 1;
        for (Map.Entry<Membre, Long> entry : top5Membres) {
            sb.append(String.format("  %d. %s %s - %d emprunts\n",
                rang++,
                entry.getKey().getPrenom(),
                entry.getKey().getNom(),
                entry.getValue()
            ));
        }
        sb.append("\n");

        // Top 5 livres
        sb.append("Top 5 livres :\n");
        @SuppressWarnings("unchecked")
        List<Map.Entry<Livre, Long>> top5Livres = 
            (List<Map.Entry<Livre, Long>>) rapport.get("top_5_livres");
        rang = 1;
        for (Map.Entry<Livre, Long> entry : top5Livres) {
            sb.append(String.format("  %d. %s - %d emprunts\n",
                rang++,
                entry.getKey().getTitre(),
                entry.getValue()
            ));
        }

        sb.append("\n========================================\n");

        return sb.toString();
    }

    /**
     * Formate un rapport complet en texte lisible
     *
     * @param rapport Rapport généré par genererRapportComplet()
     * @return String formatée du rapport
     */
    public String formaterRapportComplet(Map<String, Object> rapport) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n========================================\n");
        sb.append("   RAPPORT COMPLET D'ACTIVITÉ\n");
        sb.append("========================================\n\n");

        // Date de génération
        sb.append(String.format("Généré le : %s\n\n", 
            rapport.get("date_generation")));

        // Statistiques globales
        sb.append(String.format("Moyenne emprunts/membre : %.2f\n", 
            rapport.get("moyenne_emprunts_membre")));
        sb.append(String.format("Durée moyenne emprunts : %.1f jours\n", 
            rapport.get("duree_moyenne_emprunts")));
        sb.append(String.format("Taux de retard global : %.2f%%\n", 
            rapport.get("taux_retard_global")));
        sb.append(String.format("Total exemplaires disponibles : %d\n", 
            rapport.get("total_exemplaires_disponibles")));
        sb.append(String.format("Membres en retard : %d\n\n", 
            rapport.get("membres_en_retard")));

        // Top 10 livres
        sb.append("Top 10 livres les plus empruntés :\n");
        @SuppressWarnings("unchecked")
        List<Map.Entry<Livre, Long>> top10 = 
            (List<Map.Entry<Livre, Long>>) rapport.get("top_10_livres");
        int rang = 1;
        for (Map.Entry<Livre, Long> entry : top10) {
            sb.append(String.format("  %d. %s - %d emprunts\n",
                rang++,
                entry.getKey().getTitre(),
                entry.getValue()
            ));
        }

        sb.append("\n========================================\n");

        return sb.toString();
    }

    // ==================== GETTERS (pour tests) ====================

    public EmpruntDAO getEmpruntDAO() {
        return empruntDAO;
    }

    public StatistiquesService getStatistiquesService() {
        return statistiquesService;
    }
}
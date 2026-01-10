package com.bibliotech.utils;

import com.bibliotech.models.Livre;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Categorie;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.IntSummaryStatistics;

/**
 * Classe utilitaire pour l'export de données en format CSV
 *
 * UTILISE LES ENTRÉES/SORTIES (I/O) :
 * ✅ FileWriter - Écriture dans fichiers
 * ✅ BufferedWriter - Buffering pour performance
 * ✅ try-with-resources - Fermeture automatique des ressources
 * ✅ Gestion IOException
 * ✅ Création de répertoires (File.mkdirs())
 *
 * FORMAT CSV :
 * - Séparateur : virgule (,)
 * - Encodage : UTF-8
 * - En-tête : Toujours présent sur première ligne
 * - Échappement : Guillemets doubles si virgule dans valeur
 *
 * RÉPERTOIRE PAR DÉFAUT : rapports/
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class FileExporter {

    // ==================== CONSTANTES ====================

    private static final String REPERTOIRE_RAPPORTS = "rapports/";
    private static final String SEPARATEUR_CSV = ",";
    private static final String EXTENSION_CSV = ".csv";
    private static final DateTimeFormatter FORMATTER_DATE = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    // ==================== CONSTRUCTEUR ====================

    /**
     * Constructeur par défaut
     * Crée le répertoire des rapports s'il n'existe pas
     */
    public FileExporter() {
        creerRepertoireRapports();
    }

    // ==================== MÉTHODES PRINCIPALES ====================

    /**
     * Exporte une liste générique de données en CSV
     *
     * MÉTHODE GÉNÉRIQUE :
     * - Accepte List<?> pour flexibilité
     * - Détecte automatiquement le type d'objet
     * - Appelle la méthode d'export appropriée
     *
     * TYPES SUPPORTÉS :
     * - List<Livre>
     * - List<Membre>
     * - List<Emprunt>
     * - List<Map.Entry<Livre, Long>> (pour top livres)
     * - List<Map.Entry<Membre, Long>> (pour top membres)
     *
     * CONCEPTS I/O :
     * ✅ try-with-resources
     * ✅ FileWriter avec chemin
     * ✅ BufferedWriter pour performance
     * ✅ Gestion IOException
     *
     * @param fichier Nom du fichier (avec ou sans extension)
     * @param donnees Liste de données à exporter
     * @throws IOException si erreur d'écriture
     */
    public void exporterRapportCSV(String fichier, List<?> donnees) 
            throws IOException {
        
        // Validation
        if (fichier == null || fichier.trim().isEmpty()) {
            throw new IOException("Le nom du fichier ne peut pas être vide");
        }

        if (donnees == null) {
            throw new IOException("Les données ne peuvent pas être null");
        }

        // Ajouter extension si absente
        String nomFichier = fichier.endsWith(EXTENSION_CSV) 
                          ? fichier 
                          : fichier + EXTENSION_CSV;

        String cheminComplet = REPERTOIRE_RAPPORTS + nomFichier;

        // Si la liste est vide, créer fichier avec en-tête seulement
        if (donnees.isEmpty()) {
            try (FileWriter writer = new FileWriter(cheminComplet);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write("Aucune donnée disponible\n");
            }
            return;
        }

        // Détecter le type de données et exporter
        Object premierElement = donnees.get(0);

        if (premierElement instanceof Livre) {
            @SuppressWarnings("unchecked")
            List<Livre> livres = (List<Livre>) donnees;
            exporterLivresCSV(cheminComplet, livres);
            
        } else if (premierElement instanceof Membre) {
            @SuppressWarnings("unchecked")
            List<Membre> membres = (List<Membre>) donnees;
            exporterMembresCSV(cheminComplet, membres);
            
        } else if (premierElement instanceof Emprunt) {
            @SuppressWarnings("unchecked")
            List<Emprunt> emprunts = (List<Emprunt>) donnees;
            exporterEmpruntsCSV(cheminComplet, emprunts);
            
        } else if (premierElement instanceof Map.Entry) {
            // Détecter si c'est Map.Entry<Livre, Long> ou Map.Entry<Membre, Long>
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) premierElement;
            if (entry.getKey() instanceof Livre) {
                @SuppressWarnings("unchecked")
                List<Map.Entry<Livre, Long>> topLivres = (List<Map.Entry<Livre, Long>>) donnees;
                exporterTopLivresCSVInternal(cheminComplet, topLivres);
            } else if (entry.getKey() instanceof Membre) {
                @SuppressWarnings("unchecked")
                List<Map.Entry<Membre, Long>> topMembres = (List<Map.Entry<Membre, Long>>) donnees;
                exporterTopMembresCSVInternal(cheminComplet, topMembres);
            } else {
                throw new IOException("Type de Map.Entry non supporté : " + 
                                    entry.getKey().getClass().getSimpleName());
            }
            
        } else {
            throw new IOException("Type de données non supporté : " + 
                                premierElement.getClass().getSimpleName());
        }
    }

    /**
     * Exporte le top des livres les plus empruntés en CSV
     *
     * FORMAT :
     * Rang,ISBN,Titre,Auteur,Catégorie,Nombre Emprunts
     *
     * UTILISATION TYPIQUE :
     * - Export du résultat de top10LivresPlusEmpruntes()
     *
     * @param fichier Nom du fichier
     * @param topLivres Liste des livres avec leur nombre d'emprunts
     * @throws IOException si erreur d'écriture
     */
    public void exporterTopLivresCSV(String fichier, 
                                     List<Map.Entry<Livre, Long>> topLivres) 
            throws IOException {
        
        if (fichier == null || fichier.trim().isEmpty()) {
            throw new IOException("Le nom du fichier ne peut pas être vide");
        }

        String nomFichier = fichier.endsWith(EXTENSION_CSV) 
                          ? fichier 
                          : fichier + EXTENSION_CSV;

        String cheminComplet = REPERTOIRE_RAPPORTS + nomFichier;

        exporterTopLivresCSVInternal(cheminComplet, topLivres);
    }

    /**
     * Exporte des statistiques en CSV
     *
     * FORMAT :
     * Catégorie,Valeur
     * (chaque statistique sur une ligne)
     *
     * UTILISATION TYPIQUE :
     * - Export de statistiques de disponibilité
     * - Export de compteurs (total livres, total membres, etc.)
     * - Export de moyennes
     *
     * @param fichier Nom du fichier
     * @param statistiques Map contenant les statistiques (nom -> valeur)
     * @throws IOException si erreur d'écriture
     */
    public void exporterStatsCSV(String fichier, Map<String, Object> statistiques) 
            throws IOException {
        
        if (fichier == null || fichier.trim().isEmpty()) {
            throw new IOException("Le nom du fichier ne peut pas être vide");
        }

        if (statistiques == null || statistiques.isEmpty()) {
            throw new IOException("Les statistiques ne peuvent pas être vides");
        }

        String nomFichier = fichier.endsWith(EXTENSION_CSV) 
                          ? fichier 
                          : fichier + EXTENSION_CSV;

        String cheminComplet = REPERTOIRE_RAPPORTS + nomFichier;

        // Écriture du CSV avec try-with-resources
        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // En-tête
            bw.write("Statistique" + SEPARATEUR_CSV + "Valeur\n");

            // Données
            for (Map.Entry<String, Object> entry : statistiques.entrySet()) {
                String nom = entry.getKey();
                Object valeur = entry.getValue();

                // Formater la valeur selon son type
                String valeurStr = formaterValeur(valeur);

                bw.write(echapperCSV(nom) + SEPARATEUR_CSV + valeurStr + "\n");
            }
        }

        System.out.println("✅ Statistiques exportées : " + cheminComplet);
    }

    /**
     * Exporte un rapport mensuel complet en CSV
     *
     * INCLUT :
     * - Métadonnées (mois, année, date génération)
     * - Statistiques principales
     * - Top 5 membres
     * - Top 5 livres
     * - Emprunts par catégorie
     *
     * @param fichier Nom du fichier
     * @param rapport Map contenant toutes les données du rapport
     * @throws IOException si erreur d'écriture
     */
    public void exporterRapportMensuelCSV(String fichier, Map<String, Object> rapport) 
            throws IOException {
        
        if (fichier == null || fichier.trim().isEmpty()) {
            throw new IOException("Le nom du fichier ne peut pas être vide");
        }

        if (rapport == null || rapport.isEmpty()) {
            throw new IOException("Le rapport ne peut pas être vide");
        }

        String nomFichier = fichier.endsWith(EXTENSION_CSV) 
                          ? fichier 
                          : fichier + EXTENSION_CSV;

        String cheminComplet = REPERTOIRE_RAPPORTS + nomFichier;

        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // ===== SECTION 1 : Métadonnées =====
            bw.write("=== RAPPORT MENSUEL ===\n");
            bw.write("Mois" + SEPARATEUR_CSV + rapport.get("mois") + "\n");
            bw.write("Année" + SEPARATEUR_CSV + rapport.get("annee") + "\n");
            bw.write("\n");

            // ===== SECTION 2 : Statistiques principales =====
            bw.write("=== STATISTIQUES PRINCIPALES ===\n");
            bw.write("Total emprunts" + SEPARATEUR_CSV + 
                    rapport.get("total_emprunts") + "\n");
            bw.write("Taux de retard" + SEPARATEUR_CSV + 
                    String.format("%.2f%%", rapport.get("taux_retard")) + "\n");
            bw.write("Durée moyenne" + SEPARATEUR_CSV + 
                    String.format("%.1f jours", rapport.get("duree_moyenne")) + "\n");
            bw.write("\n");

            // ===== SECTION 3 : Emprunts par catégorie =====
            bw.write("=== EMPRUNTS PAR CATEGORIE ===\n");
            bw.write("Catégorie" + SEPARATEUR_CSV + "Nombre\n");

            @SuppressWarnings("unchecked")
            Map<Categorie, Long> parCategorie = 
                (Map<Categorie, Long>) rapport.get("par_categorie");

            for (Map.Entry<Categorie, Long> entry : parCategorie.entrySet()) {
                bw.write(entry.getKey().name() + SEPARATEUR_CSV + 
                        entry.getValue() + "\n");
            }
            bw.write("\n");

            // ===== SECTION 4 : Top 5 membres =====
            bw.write("=== TOP 5 MEMBRES ===\n");
            bw.write("Rang" + SEPARATEUR_CSV + "Nom" + SEPARATEUR_CSV + 
                    "Prénom" + SEPARATEUR_CSV + "Emprunts\n");

            @SuppressWarnings("unchecked")
            List<Map.Entry<Membre, Long>> top5Membres = 
                (List<Map.Entry<Membre, Long>>) rapport.get("top_5_membres");

            int rang = 1;
            for (Map.Entry<Membre, Long> entry : top5Membres) {
                Membre m = entry.getKey();
                bw.write(rang++ + SEPARATEUR_CSV + 
                        echapperCSV(m.getNom()) + SEPARATEUR_CSV + 
                        echapperCSV(m.getPrenom()) + SEPARATEUR_CSV + 
                        entry.getValue() + "\n");
            }
            bw.write("\n");

            // ===== SECTION 5 : Top 5 livres =====
            bw.write("=== TOP 5 LIVRES ===\n");
            bw.write("Rang" + SEPARATEUR_CSV + "Titre" + SEPARATEUR_CSV + 
                    "Auteur" + SEPARATEUR_CSV + "Emprunts\n");

            @SuppressWarnings("unchecked")
            List<Map.Entry<Livre, Long>> top5Livres = 
                (List<Map.Entry<Livre, Long>>) rapport.get("top_5_livres");

            rang = 1;
            for (Map.Entry<Livre, Long> entry : top5Livres) {
                Livre l = entry.getKey();
                bw.write(rang++ + SEPARATEUR_CSV + 
                        echapperCSV(l.getTitre()) + SEPARATEUR_CSV + 
                        echapperCSV(l.getAuteur().getNom()) + SEPARATEUR_CSV + 
                        entry.getValue() + "\n");
            }
        }

        System.out.println("✅ Rapport mensuel exporté : " + cheminComplet);
    }

    // ==================== MÉTHODES INTERNES D'EXPORT ====================

    /**
     * Exporte une liste de livres en CSV (méthode interne)
     */
    private void exporterLivresCSV(String cheminComplet, List<Livre> livres) 
            throws IOException {
        
        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // En-tête
            bw.write("ID" + SEPARATEUR_CSV + 
                    "ISBN" + SEPARATEUR_CSV + 
                    "Titre" + SEPARATEUR_CSV + 
                    "Auteur" + SEPARATEUR_CSV + 
                    "Catégorie" + SEPARATEUR_CSV + 
                    "Année" + SEPARATEUR_CSV + 
                    "Exemplaires" + SEPARATEUR_CSV + 
                    "Disponibles\n");

            // Données
            for (Livre livre : livres) {
                bw.write(livre.getIdLivre() + SEPARATEUR_CSV);
                bw.write(echapperCSV(livre.getIsbn()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(livre.getTitre()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(livre.getAuteur().getNom() + " " + 
                        livre.getAuteur().getPrenom()) + SEPARATEUR_CSV);
                bw.write(livre.getCategorie().name() + SEPARATEUR_CSV);
                bw.write(livre.getAnneePublication() + SEPARATEUR_CSV);
                bw.write(livre.getNombreExemplaires() + SEPARATEUR_CSV);
                bw.write(livre.getDisponibles() + "\n");
            }
        }

        System.out.println("✅ Livres exportés : " + cheminComplet);
    }

    /**
     * Exporte une liste de membres en CSV (méthode interne)
     */
    private void exporterMembresCSV(String cheminComplet, List<Membre> membres) 
            throws IOException {
        
        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // En-tête
            bw.write("ID" + SEPARATEUR_CSV + 
                    "CIN" + SEPARATEUR_CSV + 
                    "Nom" + SEPARATEUR_CSV + 
                    "Prénom" + SEPARATEUR_CSV + 
                    "Email" + SEPARATEUR_CSV + 
                    "Téléphone" + SEPARATEUR_CSV + 
                    "Date Inscription" + SEPARATEUR_CSV + 
                    "Emprunts Actifs\n");

            // Données
            for (Membre membre : membres) {
                bw.write(membre.getIdMembre() + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getCin()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getNom()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getPrenom()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getEmail()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getTelephone()) + SEPARATEUR_CSV);
                bw.write(membre.getDateInscription() + SEPARATEUR_CSV);
                bw.write(membre.getNombreEmpruntsActifs() + "\n");
            }
        }

        System.out.println("✅ Membres exportés : " + cheminComplet);
    }

    /**
     * Exporte une liste d'emprunts en CSV (méthode interne)
     */
    private void exporterEmpruntsCSV(String cheminComplet, List<Emprunt> emprunts) 
            throws IOException {
        
        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // En-tête
            bw.write("ID" + SEPARATEUR_CSV + 
                    "Membre" + SEPARATEUR_CSV + 
                    "Livre" + SEPARATEUR_CSV + 
                    "Date Emprunt" + SEPARATEUR_CSV + 
                    "Date Retour Prévue" + SEPARATEUR_CSV + 
                    "Date Retour Effective" + SEPARATEUR_CSV + 
                    "Statut" + SEPARATEUR_CSV + 
                    "Durée (jours)\n");

            // Données
            for (Emprunt emprunt : emprunts) {
                bw.write(emprunt.getIdEmprunt() + SEPARATEUR_CSV);
                bw.write(echapperCSV(emprunt.getMembre().getNom() + " " + 
                        emprunt.getMembre().getPrenom()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(emprunt.getLivre().getTitre()) + SEPARATEUR_CSV);
                bw.write(emprunt.getDateEmprunt() + SEPARATEUR_CSV);
                bw.write(emprunt.getDateRetourPrevue() + SEPARATEUR_CSV);
                bw.write((emprunt.getDateRetourEffective() != null 
                         ? emprunt.getDateRetourEffective().toString() 
                         : "En cours") + SEPARATEUR_CSV);
                bw.write(emprunt.getStatut().name() + SEPARATEUR_CSV);
                bw.write(emprunt.calculerDuree() + "\n");
            }
        }

        System.out.println("✅ Emprunts exportés : " + cheminComplet);
    }

    /**
     * Exporte le top des livres (méthode interne)
     */
    private void exporterTopLivresCSVInternal(String cheminComplet, 
                                             List<Map.Entry<Livre, Long>> topLivres) 
            throws IOException {
        
        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // En-tête
            bw.write("Rang" + SEPARATEUR_CSV + 
                    "ISBN" + SEPARATEUR_CSV + 
                    "Titre" + SEPARATEUR_CSV + 
                    "Auteur" + SEPARATEUR_CSV + 
                    "Catégorie" + SEPARATEUR_CSV + 
                    "Nombre Emprunts\n");

            // Données
            int rang = 1;
            for (Map.Entry<Livre, Long> entry : topLivres) {
                Livre livre = entry.getKey();
                Long nbEmprunts = entry.getValue();

                bw.write(rang++ + SEPARATEUR_CSV);
                bw.write(echapperCSV(livre.getIsbn()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(livre.getTitre()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(livre.getAuteur().getNom() + " " + 
                        livre.getAuteur().getPrenom()) + SEPARATEUR_CSV);
                bw.write(livre.getCategorie().name() + SEPARATEUR_CSV);
                bw.write(nbEmprunts + "\n");
            }
        }

        System.out.println("✅ Top livres exporté : " + cheminComplet);
    }

    /**
     * Exporte le top des membres (méthode interne)
     */
    private void exporterTopMembresCSVInternal(String cheminComplet, 
                                              List<Map.Entry<Membre, Long>> topMembres) 
            throws IOException {
        
        try (FileWriter writer = new FileWriter(cheminComplet);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // En-tête
            bw.write("Rang" + SEPARATEUR_CSV + 
                    "CIN" + SEPARATEUR_CSV + 
                    "Nom" + SEPARATEUR_CSV + 
                    "Prénom" + SEPARATEUR_CSV + 
                    "Nombre Emprunts\n");

            // Données
            int rang = 1;
            for (Map.Entry<Membre, Long> entry : topMembres) {
                Membre membre = entry.getKey();
                Long nbEmprunts = entry.getValue();

                bw.write(rang++ + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getCin()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getNom()) + SEPARATEUR_CSV);
                bw.write(echapperCSV(membre.getPrenom()) + SEPARATEUR_CSV);
                bw.write(nbEmprunts + "\n");
            }
        }

        System.out.println("✅ Top membres exporté : " + cheminComplet);
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Crée le répertoire des rapports s'il n'existe pas
     */
    private void creerRepertoireRapports() {
        File repertoire = new File(REPERTOIRE_RAPPORTS);
        if (!repertoire.exists()) {
            if (repertoire.mkdirs()) {
                System.out.println("📁 Répertoire créé : " + REPERTOIRE_RAPPORTS);
            }
        }
    }

    /**
     * Échappe les valeurs CSV (ajoute guillemets si virgule présente)
     *
     * @param valeur Valeur à échapper
     * @return Valeur échappée
     */
    private String echapperCSV(String valeur) {
        if (valeur == null) {
            return "";
        }

        // Si la valeur contient une virgule, guillemets ou saut de ligne
        if (valeur.contains(SEPARATEUR_CSV) || 
            valeur.contains("\"") || 
            valeur.contains("\n")) {
            // Échapper les guillemets doubles en les doublant
            valeur = valeur.replace("\"", "\"\"");
            // Entourer de guillemets
            return "\"" + valeur + "\"";
        }

        return valeur;
    }

    /**
     * Formate une valeur selon son type pour l'export CSV
     *
     * @param valeur Valeur à formater
     * @return String formatée
     */
    private String formaterValeur(Object valeur) {
        if (valeur == null) {
            return "N/A";
        }

        if (valeur instanceof Double) {
            return String.format("%.2f", (Double) valeur);
        }

        if (valeur instanceof Float) {
            return String.format("%.2f", (Float) valeur);
        }

        if (valeur instanceof IntSummaryStatistics) {
            IntSummaryStatistics stats = (IntSummaryStatistics) valeur;
            return String.format("count=%d,sum=%d,min=%d,max=%d,avg=%.2f",
                stats.getCount(), stats.getSum(), stats.getMin(), 
                stats.getMax(), stats.getAverage());
        }

        if (valeur instanceof List) {
            List<?> liste = (List<?>) valeur;
            return String.valueOf(liste.size()) + " éléments";
        }

        if (valeur instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) valeur;
            return String.valueOf(map.size()) + " entrées";
        }

        return valeur.toString();
    }

    /**
     * Génère un nom de fichier unique avec timestamp
     *
     * @param prefixe Préfixe du nom de fichier
     * @return Nom de fichier unique
     */
    public String genererNomFichierUnique(String prefixe) {
        String timestamp = LocalDateTime.now().format(FORMATTER_DATE);
        return prefixe + "_" + timestamp + EXTENSION_CSV;
    }

    /**
     * Vérifie si un fichier existe dans le répertoire des rapports
     *
     * @param nomFichier Nom du fichier
     * @return true si le fichier existe
     */
    public boolean fichierExiste(String nomFichier) {
        String nomComplet = nomFichier.endsWith(EXTENSION_CSV) 
                          ? nomFichier 
                          : nomFichier + EXTENSION_CSV;
        File fichier = new File(REPERTOIRE_RAPPORTS + nomComplet);
        return fichier.exists();
    }

    /**
     * Supprime un fichier du répertoire des rapports
     *
     * @param nomFichier Nom du fichier à supprimer
     * @return true si suppression réussie
     */
    public boolean supprimerFichier(String nomFichier) {
        String nomComplet = nomFichier.endsWith(EXTENSION_CSV) 
                          ? nomFichier 
                          : nomFichier + EXTENSION_CSV;
        File fichier = new File(REPERTOIRE_RAPPORTS + nomComplet);
        return fichier.delete();
    }

    /**
     * Liste tous les fichiers CSV dans le répertoire des rapports
     *
     * @return Liste des noms de fichiers
     */
    public List<String> listerFichiersCSV() {
        File repertoire = new File(REPERTOIRE_RAPPORTS);
        List<String> fichiers = new java.util.ArrayList<>();

        if (repertoire.exists() && repertoire.isDirectory()) {
        	File[] files = repertoire.listFiles((_, name) -> 
            name.toLowerCase().endsWith(EXTENSION_CSV));

            if (files != null) {
                for (File file : files) {
                    fichiers.add(file.getName());
                }
            }
        }

        return fichiers;
    }

    // ==================== GETTERS ====================

    public String getRepertoireRapports() {
        return REPERTOIRE_RAPPORTS;
    }
}
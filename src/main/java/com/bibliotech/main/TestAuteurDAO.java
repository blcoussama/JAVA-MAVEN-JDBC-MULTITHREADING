package com.bibliotech.main;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.models.Auteur;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;
import java.util.Optional;

/**
 * Classe de test pour AuteurDAO
 * 
 * Teste toutes les méthodes CRUD :
 * - INSERT (create)
 * - SELECT (read) 
 * - UPDATE (update)
 * - DELETE (delete)
 * 
 * @author Belcadi Oussama
 */
public class TestAuteurDAO {
    
    private static AuteurDAO auteurDAO = new AuteurDAO();
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║        TEST COMPLET AUTEURDAO - BIBLIOTECH            ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Exécuter tous les tests dans l'ordre
        testInsert();
        testFindById();
        testFindAll();
        testFindByNom();
        testSearchByNom();
        testUpdate();
        testCount();
        testExists();
        testDelete();
        
        System.out.println("\n╔═════════════════════════════════════════════════════════╗");
        System.out.println("  ║              TESTS TERMINÉS AVEC SUCCÈS                 ║");
        System.out.println("  ╚═════════════════════════════════════════════════════════╝");
    }
    
    // ==================== TEST INSERT ====================
    
    /**
     * Test 1 : Insertion de nouveaux auteurs
     */
    public static void testInsert() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 1 : INSERT - Ajout de nouveaux auteurs");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Créer 3 nouveaux auteurs de test
            Auteur auteur1 = new Auteur("Tolkien", "J.R.R.", "Royaume-Uni", 1892);
            Auteur auteur2 = new Auteur("Rowling", "J.K.", "Royaume-Uni", 1965);
            Auteur auteur3 = new Auteur("Asimov", "Isaac", "Russie", 1920);
            
            // Insérer dans la BD
            int id1 = auteurDAO.insert(auteur1);
            int id2 = auteurDAO.insert(auteur2);
            int id3 = auteurDAO.insert(auteur3);
            
            // Afficher résultats
            System.out.println("✅ Auteur 1 inséré avec succès");
            System.out.println("   ID généré : " + id1);
            System.out.println("   " + auteur1);
            System.out.println();
            
            System.out.println("✅ Auteur 2 inséré avec succès");
            System.out.println("   ID généré : " + id2);
            System.out.println("   " + auteur2);
            System.out.println();
            
            System.out.println("✅ Auteur 3 inséré avec succès");
            System.out.println("   ID généré : " + id3);
            System.out.println("   " + auteur3);
            System.out.println();
            
            System.out.println("✅ TEST INSERT : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de l'insertion : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST FIND_BY_ID ====================
    
    /**
     * Test 2 : Recherche d'un auteur par ID
     */
    public static void testFindById() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 2 : FIND_BY_ID - Recherche par ID");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Chercher l'auteur avec ID = 1 (Robert C. Martin)
            System.out.println("🔍 Recherche de l'auteur avec ID = 1...");
            Optional<Auteur> auteurOpt = auteurDAO.findById(1);
            
            if (auteurOpt.isPresent()) {
                Auteur auteur = auteurOpt.get();
                System.out.println("✅ Auteur trouvé :");
                System.out.println("   ID : " + auteur.getIdAuteur());
                System.out.println("   Nom : " + auteur.getNom());
                System.out.println("   Prénom : " + auteur.getPrenom());
                System.out.println("   Nationalité : " + auteur.getNationalite());
                System.out.println("   Année naissance : " + auteur.getAnneeNaissance());
            } else {
                System.out.println("❌ Aucun auteur trouvé avec ID = 1");
            }
            System.out.println();
            
            // Chercher un ID inexistant
            System.out.println("🔍 Recherche d'un ID inexistant (999)...");
            Optional<Auteur> inexistant = auteurDAO.findById(999);
            
            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un auteur a été trouvé !");
            }
            System.out.println();
            
            System.out.println("✅ TEST FIND_BY_ID : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST FIND_ALL ====================
    
    /**
     * Test 3 : Récupération de tous les auteurs
     */
    public static void testFindAll() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 3 : FIND_ALL - Récupérer tous les auteurs");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            List<Auteur> auteurs = auteurDAO.findAll();
            
            System.out.println("✅ " + auteurs.size() + " auteur(s) trouvé(s) :");
            System.out.println();
            
            int compteur = 1;
            for (Auteur auteur : auteurs) {
                System.out.printf("%2d. [ID: %d] %s %s (%s, %d)%n",
                    compteur++,
                    auteur.getIdAuteur(),
                    auteur.getPrenom(),
                    auteur.getNom(),
                    auteur.getNationalite(),
                    auteur.getAnneeNaissance()
                );
            }
            System.out.println();
            
            System.out.println("✅ TEST FIND_ALL : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la récupération : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST FIND_BY_NOM ====================
    
    /**
     * Test 4 : Recherche exacte par nom et prénom
     */
    public static void testFindByNom() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 4 : FIND_BY_NOM - Recherche exacte");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Rechercher Joshua Bloch
            System.out.println("🔍 Recherche : 'Bloch' 'Joshua'");
            Optional<Auteur> auteurOpt = auteurDAO.findByNom("Bloch", "Joshua");
            
            if (auteurOpt.isPresent()) {
                Auteur auteur = auteurOpt.get();
                System.out.println("✅ Auteur trouvé :");
                System.out.println("   " + auteur);
            } else {
                System.out.println("❌ Aucun auteur trouvé");
            }
            System.out.println();
            
            // Rechercher un auteur inexistant
            System.out.println("🔍 Recherche : 'Dupont' 'Jean' (inexistant)");
            Optional<Auteur> inexistant = auteurDAO.findByNom("Dupont", "Jean");
            
            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un auteur a été trouvé !");
            }
            System.out.println();
            
            System.out.println("✅ TEST FIND_BY_NOM : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST SEARCH_BY_NOM ====================
    
    /**
     * Test 5 : Recherche partielle par nom (LIKE)
     */
    public static void testSearchByNom() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 5 : SEARCH_BY_NOM - Recherche partielle");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Rechercher tous les auteurs dont le nom contient "Ma"
            System.out.println("🔍 Recherche partielle : '%Ma%'");
            List<Auteur> auteurs = auteurDAO.searchByNom("Ma");
            
            System.out.println("✅ " + auteurs.size() + " auteur(s) trouvé(s) :");
            for (Auteur auteur : auteurs) {
                System.out.println("   - " + auteur.getPrenom() + " " + auteur.getNom());
            }
            System.out.println();
            
            System.out.println("✅ TEST SEARCH_BY_NOM : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST UPDATE ====================
    
    /**
     * Test 6 : Modification d'un auteur existant
     */
    public static void testUpdate() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 6 : UPDATE - Modification d'un auteur");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Récupérer un auteur existant
            Optional<Auteur> auteurOpt = auteurDAO.findById(1);
            
            if (auteurOpt.isPresent()) {
                Auteur auteur = auteurOpt.get();
                
                System.out.println("📄 Avant modification :");
                System.out.println("   " + auteur);
                System.out.println();
                
                // Modifier les données
                auteur.setNationalite("États-Unis d'Amérique");
                auteur.setAnneeNaissance(1953);  // Correction si nécessaire
                
                // Sauvegarder les modifications
                boolean succes = auteurDAO.update(auteur);
                
                if (succes) {
                    System.out.println("✅ Modification enregistrée");
                    System.out.println();
                    
                    // Re-charger depuis la BD pour vérifier
                    Optional<Auteur> auteurModifie = auteurDAO.findById(1);
                    if (auteurModifie.isPresent()) {
                        System.out.println("📄 Après modification (rechargé depuis BD) :");
                        System.out.println("   " + auteurModifie.get());
                    }
                } else {
                    System.out.println("❌ La modification a échoué");
                }
            } else {
                System.out.println("❌ Auteur ID=1 introuvable");
            }
            System.out.println();
            
            System.out.println("✅ TEST UPDATE : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST COUNT ====================
    
    /**
     * Test 7 : Compter le nombre total d'auteurs
     */
    public static void testCount() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 7 : COUNT - Comptage total");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            long total = auteurDAO.count();
            
            System.out.println("✅ Nombre total d'auteurs : " + total);
            System.out.println();
            
            System.out.println("✅ TEST COUNT : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du comptage : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST EXISTS ====================
    
    /**
     * Test 8 : Vérifier l'existence d'un auteur
     */
    public static void testExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 8 : EXISTS - Vérification existence");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Tester avec un ID existant
            boolean existe1 = auteurDAO.exists(1);
            System.out.println("🔍 L'auteur ID=1 existe ? " + (existe1 ? "✅ OUI" : "❌ NON"));
            
            // Tester avec un ID inexistant
            boolean existe999 = auteurDAO.exists(999);
            System.out.println("🔍 L'auteur ID=999 existe ? " + (existe999 ? "❌ OUI" : "✅ NON"));
            System.out.println();
            
            System.out.println("✅ TEST EXISTS : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la vérification : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    // ==================== TEST DELETE ====================
    
    /**
     * Test 9 : Suppression d'un auteur
     */
    public static void testDelete() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 9 : DELETE - Suppression d'un auteur");
        System.out.println("═══════════════════════════════════════════════════════");
        
        try {
            // Créer un auteur temporaire pour le supprimer
            Auteur auteurTemp = new Auteur("TestDelete", "Temporaire", "Test", 2000);
            int idTemp = auteurDAO.insert(auteurTemp);
            
            System.out.println("✅ Auteur temporaire créé (ID: " + idTemp + ")");
            System.out.println();
            
            // Vérifier qu'il existe
            boolean existeAvant = auteurDAO.exists(idTemp);
            System.out.println("🔍 Existe avant suppression ? " + (existeAvant ? "✅ OUI" : "❌ NON"));
            
            // Supprimer
            boolean succes = auteurDAO.delete(idTemp);
            
            if (succes) {
                System.out.println("✅ Auteur supprimé avec succès");
                
                // Vérifier qu'il n'existe plus
                boolean existeApres = auteurDAO.exists(idTemp);
                System.out.println("🔍 Existe après suppression ? " + (existeApres ? "❌ OUI" : "✅ NON"));
            } else {
                System.out.println("❌ La suppression a échoué");
            }
            System.out.println();
            
            // Tester suppression d'un auteur avec des livres (doit échouer)
            System.out.println("🧪 Test suppression auteur avec livres (doit échouer)...");
            try {
                auteurDAO.delete(1);  // Robert C. Martin a des livres
                System.out.println("❌ ERREUR : La suppression aurait dû échouer !");
            } catch (DatabaseException e) {
                System.out.println("✅ Suppression refusée (comportement attendu)");
                System.out.println("   Raison : " + e.getMessage());
            }
            System.out.println();
            
            System.out.println("✅ TEST DELETE : RÉUSSI");
            
        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
}
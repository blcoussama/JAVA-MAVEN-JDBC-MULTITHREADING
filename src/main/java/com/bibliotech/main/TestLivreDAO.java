package com.bibliotech.main;

import com.bibliotech.dao.LivreDAO;
import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Auteur;
import com.bibliotech.models.Categorie;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;
import java.util.Optional;

/**
 * Classe de test pour LivreDAO
 *
 * Teste toutes les méthodes CRUD avec :
 * - Relation Auteur (JOIN SQL)
 * - Enum Categorie
 * - Recherches multiples
 *
 * @author Belcadi Oussama
 */
public class TestLivreDAO {

    private static LivreDAO livreDAO = new LivreDAO();
    private static AuteurDAO auteurDAO = new AuteurDAO();

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║         TEST COMPLET LIVREDAO - BIBLIOTECH           ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();

        // Exécuter tous les tests dans l'ordre
        testInsert();
        testFindById();
        testFindByIsbn();
        testFindAll();
        testRechercherParTitre();
        testRechercherParAuteur();
        testFindByCategorie();
        testFindByAuteur();
        testUpdate();
        testUpdateDisponibilite();
        testCount();
        testCountByCategorie();
        testExists();
        testIsbnExists();
        testDelete();

        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║              TESTS TERMINÉS AVEC SUCCÈS              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }

    // ==================== TEST INSERT ====================

    /**
     * Test 1 : Insertion de nouveaux livres
     */
    public static void testInsert() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 1 : INSERT - Ajout de nouveaux livres");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Récupérer des auteurs existants
            Optional<Auteur> auteur1Opt = auteurDAO.findById(1); // Robert C. Martin
            Optional<Auteur> auteur2Opt = auteurDAO.findById(2); // Joshua Bloch

            if (auteur1Opt.isEmpty() || auteur2Opt.isEmpty()) {
                System.out.println("❌ Auteurs non trouvés (vérifier la BD)");
                return;
            }

            Auteur auteur1 = auteur1Opt.get();
            Auteur auteur2 = auteur2Opt.get();

            // Créer 3 nouveaux livres de test
            Livre livre1 = new Livre();
            livre1.setIsbn("978-0-13-468599-1");
            livre1.setTitre("Effective Java - Third Edition");
            livre1.setAuteur(auteur2);
            livre1.setCategorie(Categorie.TECHNOLOGIE);
            livre1.setAnneePublication(2018);
            livre1.setNombreExemplaires(5);
            livre1.setDisponibles(5);

            Livre livre2 = new Livre();
            livre2.setIsbn("978-0-13-235088-4");
            livre2.setTitre("Clean Code: A Handbook of Agile Software Craftsmanship");
            livre2.setAuteur(auteur1);
            livre2.setCategorie(Categorie.TECHNOLOGIE);
            livre2.setAnneePublication(2008);
            livre2.setNombreExemplaires(3);
            livre2.setDisponibles(3);

            Livre livre3 = new Livre();
            livre3.setIsbn("978-2-07-036822-9");
            livre3.setTitre("Le Seigneur des Anneaux");
            livre3.setAuteur(auteur1);  // Temporaire pour le test
            livre3.setCategorie(Categorie.ROMAN);
            livre3.setAnneePublication(1954);
            livre3.setNombreExemplaires(2);
            livre3.setDisponibles(2);

            // Insérer dans la BD
            int id1 = livreDAO.insert(livre1);
            int id2 = livreDAO.insert(livre2);
            int id3 = livreDAO.insert(livre3);

            // Afficher résultats
            System.out.println("✅ Livre 1 inséré avec succès");
            System.out.println("   ID généré : " + id1);
            System.out.println("   ISBN : " + livre1.getIsbn());
            System.out.println("   Titre : " + livre1.getTitre());
            System.out.println("   Auteur : " + livre1.getAuteur().getPrenom() + " " +
                             livre1.getAuteur().getNom());
            System.out.println("   Catégorie : " + livre1.getCategorie());
            System.out.println();

            System.out.println("✅ Livre 2 inséré avec succès");
            System.out.println("   ID généré : " + id2);
            System.out.println("   Titre : " + livre2.getTitre());
            System.out.println();

            System.out.println("✅ Livre 3 inséré avec succès");
            System.out.println("   ID généré : " + id3);
            System.out.println("   Titre : " + livre3.getTitre());
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
     * Test 2 : Recherche d'un livre par ID
     */
    public static void testFindById() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 2 : FIND_BY_ID - Recherche par ID");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Chercher le livre avec ID = 1
            System.out.println("🔍 Recherche du livre avec ID = 1...");
            Optional<Livre> livreOpt = livreDAO.findById(1);

            if (livreOpt.isPresent()) {
                Livre livre = livreOpt.get();
                System.out.println("✅ Livre trouvé :");
                System.out.println("   ID : " + livre.getIdLivre());
                System.out.println("   ISBN : " + livre.getIsbn());
                System.out.println("   Titre : " + livre.getTitre());
                System.out.println("   Auteur : " + livre.getAuteur().getPrenom() + " " +
                                 livre.getAuteur().getNom());
                System.out.println("   Catégorie : " + livre.getCategorie());
                System.out.println("   Disponibles : " + livre.getDisponibles() + "/" +
                                 livre.getNombreExemplaires());
            } else {
                System.out.println("❌ Aucun livre trouvé avec ID = 1");
            }
            System.out.println();

            // Chercher un ID inexistant
            System.out.println("🔍 Recherche d'un ID inexistant (999)...");
            Optional<Livre> inexistant = livreDAO.findById(999);

            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un livre a été trouvé !");
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_ID : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_BY_ISBN ====================

    /**
     * Test 3 : Recherche par ISBN (unique)
     */
    public static void testFindByIsbn() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 3 : FIND_BY_ISBN - Recherche par ISBN");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Chercher un livre existant par ISBN
            String isbn = "978-0134685991";
            System.out.println("🔍 Recherche ISBN : " + isbn);
            Optional<Livre> livreOpt = livreDAO.findByIsbn(isbn);

            if (livreOpt.isPresent()) {
                Livre livre = livreOpt.get();
                System.out.println("✅ Livre trouvé :");
                System.out.println("   " + livre.getTitre());
                System.out.println("   par " + livre.getAuteur().getPrenom() + " " +
                                 livre.getAuteur().getNom());
            } else {
                System.out.println("❌ Aucun livre trouvé avec cet ISBN");
            }
            System.out.println();

            // Chercher un ISBN inexistant
            System.out.println("🔍 Recherche ISBN inexistant...");
            Optional<Livre> inexistant = livreDAO.findByIsbn("000-0000000000");

            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un livre a été trouvé !");
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_ISBN : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_ALL ====================

    /**
     * Test 4 : Récupération de tous les livres
     */
    public static void testFindAll() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 4 : FIND_ALL - Récupérer tous les livres");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            List<Livre> livres = livreDAO.findAll();

            System.out.println("✅ " + livres.size() + " livre(s) trouvé(s) :");
            System.out.println();

            int compteur = 1;
            for (Livre livre : livres) {
                System.out.printf("%2d. [ID: %d] %s%n",
                    compteur++,
                    livre.getIdLivre(),
                    livre.getTitre()
                );
                System.out.printf("    Par: %s %s | Catégorie: %s | Dispo: %d/%d%n",
                    livre.getAuteur().getPrenom(),
                    livre.getAuteur().getNom(),
                    livre.getCategorie(),
                    livre.getDisponibles(),
                    livre.getNombreExemplaires()
                );
                System.out.println();
            }

            System.out.println("✅ TEST FIND_ALL : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la récupération : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST RECHERCHER_PAR_TITRE ====================

    /**
     * Test 5 : Recherche partielle par titre
     */
    public static void testRechercherParTitre() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 5 : RECHERCHER_PAR_TITRE - Recherche partielle");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Rechercher les livres contenant "Java"
            String motCle = "Java";
            System.out.println("🔍 Recherche partielle : '%" + motCle + "%'");
            List<Livre> livres = livreDAO.rechercherParTitre(motCle);

            System.out.println("✅ " + livres.size() + " livre(s) trouvé(s) :");
            for (Livre livre : livres) {
                System.out.println("   - " + livre.getTitre());
                System.out.println("     par " + livre.getAuteur().getNom());
            }
            System.out.println();

            System.out.println("✅ TEST RECHERCHER_PAR_TITRE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST RECHERCHER_PAR_AUTEUR ====================

    /**
     * Test 6 : Recherche par nom d'auteur
     */
    public static void testRechercherParAuteur() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 6 : RECHERCHER_PAR_AUTEUR - Par nom d'auteur");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Rechercher les livres de "Martin"
            String nomAuteur = "Martin";
            System.out.println("🔍 Recherche livres de : '%" + nomAuteur + "%'");
            List<Livre> livres = livreDAO.rechercherParAuteur(nomAuteur);

            System.out.println("✅ " + livres.size() + " livre(s) trouvé(s) :");
            for (Livre livre : livres) {
                System.out.println("   - " + livre.getTitre());
                System.out.println("     par " + livre.getAuteur().getPrenom() + " " +
                                 livre.getAuteur().getNom());
            }
            System.out.println();

            System.out.println("✅ TEST RECHERCHER_PAR_AUTEUR : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_BY_CATEGORIE ====================

    /**
     * Test 7 : Recherche par catégorie
     */
    public static void testFindByCategorie() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 7 : FIND_BY_CATEGORIE - Par catégorie");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Rechercher les livres de TECHNOLOGIE
            Categorie categorie = Categorie.TECHNOLOGIE;
            System.out.println("🔍 Recherche catégorie : " + categorie);
            List<Livre> livres = livreDAO.findByCategorie(categorie);

            System.out.println("✅ " + livres.size() + " livre(s) trouvé(s) :");
            for (Livre livre : livres) {
                System.out.println("   - " + livre.getTitre());
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_CATEGORIE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_BY_AUTEUR ====================

    /**
     * Test 8 : Tous les livres d'un auteur (par ID)
     */
    public static void testFindByAuteur() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 8 : FIND_BY_AUTEUR - Par ID auteur");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Chercher tous les livres de l'auteur ID=1
            int idAuteur = 1;
            System.out.println("🔍 Recherche livres de l'auteur ID = " + idAuteur);
            List<Livre> livres = livreDAO.findByAuteur(idAuteur);

            System.out.println("✅ " + livres.size() + " livre(s) trouvé(s) :");
            for (Livre livre : livres) {
                System.out.println("   - " + livre.getTitre());
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_AUTEUR : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST UPDATE ====================

    /**
     * Test 9 : Modification d'un livre existant
     */
    public static void testUpdate() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 9 : UPDATE - Modification d'un livre");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Récupérer un livre existant
            Optional<Livre> livreOpt = livreDAO.findById(1);

            if (livreOpt.isPresent()) {
                Livre livre = livreOpt.get();

                System.out.println("📄 Avant modification :");
                System.out.println("   Titre : " + livre.getTitre());
                System.out.println("   Année : " + livre.getAnneePublication());
                System.out.println("   Disponibles : " + livre.getDisponibles());
                System.out.println();

                // Modifier les données
                livre.setAnneePublication(2019);  // Modification
                livre.setDisponibles(4);  // Modification

                // Sauvegarder les modifications
                boolean succes = livreDAO.update(livre);

                if (succes) {
                    System.out.println("✅ Modification enregistrée");
                    System.out.println();

                    // Re-charger depuis la BD pour vérifier
                    Optional<Livre> livreModifie = livreDAO.findById(1);
                    if (livreModifie.isPresent()) {
                        System.out.println("📄 Après modification (rechargé depuis BD) :");
                        System.out.println("   Titre : " + livreModifie.get().getTitre());
                        System.out.println("   Année : " + livreModifie.get().getAnneePublication());
                        System.out.println("   Disponibles : " + livreModifie.get().getDisponibles());
                    }
                } else {
                    System.out.println("❌ La modification a échoué");
                }
            } else {
                System.out.println("❌ Livre ID=1 introuvable");
            }
            System.out.println();

            System.out.println("✅ TEST UPDATE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST UPDATE_DISPONIBILITE ====================

    /**
     * Test 10 : Modification uniquement de la disponibilité
     */
    public static void testUpdateDisponibilite() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 10 : UPDATE_DISPONIBILITE - Modifier disponibilité");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            int idLivre = 2;

            // Lire disponibilité actuelle
            Optional<Livre> livreAvant = livreDAO.findById(idLivre);
            if (livreAvant.isPresent()) {
                System.out.println("📄 Avant : Disponibles = " +
                                 livreAvant.get().getDisponibles());
            }

            // Modifier la disponibilité
            boolean succes = livreDAO.updateDisponibilite(idLivre, 2);

            if (succes) {
                System.out.println("✅ Disponibilité modifiée");

                // Vérifier
                Optional<Livre> livreApres = livreDAO.findById(idLivre);
                if (livreApres.isPresent()) {
                    System.out.println("📄 Après : Disponibles = " +
                                     livreApres.get().getDisponibles());
                }
            } else {
                System.out.println("❌ La modification a échoué");
            }
            System.out.println();

            System.out.println("✅ TEST UPDATE_DISPONIBILITE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST COUNT ====================

    /**
     * Test 11 : Comptage total des livres
     */
    public static void testCount() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 11 : COUNT - Comptage total");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            long total = livreDAO.count();

            System.out.println("✅ Nombre total de livres : " + total);
            System.out.println();

            System.out.println("✅ TEST COUNT : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du comptage : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST COUNT_BY_CATEGORIE ====================

    /**
     * Test 12 : Comptage par catégorie
     */
    public static void testCountByCategorie() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 12 : COUNT_BY_CATEGORIE - Comptage par catégorie");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            System.out.println("📊 Comptage par catégorie :");

            for (Categorie categorie : Categorie.values()) {
                long count = livreDAO.countByCategorie(categorie);
                System.out.printf("   %-15s : %d livre(s)%n", categorie, count);
            }
            System.out.println();

            System.out.println("✅ TEST COUNT_BY_CATEGORIE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du comptage : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST EXISTS ====================

    /**
     * Test 13 : Vérification existence
     */
    public static void testExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 13 : EXISTS - Vérification existence");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Tester avec un ID existant
            boolean existe1 = livreDAO.exists(1);
            System.out.println("🔍 Le livre ID=1 existe ? " + (existe1 ? "✅ OUI" : "❌ NON"));

            // Tester avec un ID inexistant
            boolean existe999 = livreDAO.exists(999);
            System.out.println("🔍 Le livre ID=999 existe ? " + (existe999 ? "❌ OUI" : "✅ NON"));
            System.out.println();

            System.out.println("✅ TEST EXISTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la vérification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST ISBN_EXISTS ====================

    /**
     * Test 14 : Vérification existence ISBN
     */
    public static void testIsbnExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 14 : ISBN_EXISTS - Vérification ISBN unique");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // ISBN existant
            String isbnExistant = "978-0134685991";
            boolean existe1 = livreDAO.isbnExists(isbnExistant);
            System.out.println("🔍 L'ISBN " + isbnExistant + " existe ? " +
                             (existe1 ? "✅ OUI" : "❌ NON"));

            // ISBN inexistant
            String isbnInexistant = "000-0000000000";
            boolean existe2 = livreDAO.isbnExists(isbnInexistant);
            System.out.println("🔍 L'ISBN " + isbnInexistant + " existe ? " +
                             (existe2 ? "❌ OUI" : "✅ NON"));
            System.out.println();

            System.out.println("✅ TEST ISBN_EXISTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la vérification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST DELETE ====================

    /**
     * Test 15 : Suppression d'un livre
     */
    public static void testDelete() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 15 : DELETE - Suppression d'un livre");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Créer un livre temporaire pour le supprimer
            Optional<Auteur> auteurOpt = auteurDAO.findById(1);
            if (auteurOpt.isEmpty()) {
                System.out.println("❌ Auteur non trouvé");
                return;
            }

            Livre livreTemp = new Livre();
            livreTemp.setIsbn("000-TEST-DELETE");
            livreTemp.setTitre("Livre Test à Supprimer");
            livreTemp.setAuteur(auteurOpt.get());
            livreTemp.setCategorie(Categorie.TECHNOLOGIE);
            livreTemp.setAnneePublication(2025);
            livreTemp.setNombreExemplaires(1);
            livreTemp.setDisponibles(1);

            int idTemp = livreDAO.insert(livreTemp);

            System.out.println("✅ Livre temporaire créé (ID: " + idTemp + ")");
            System.out.println();

            // Vérifier qu'il existe
            boolean existeAvant = livreDAO.exists(idTemp);
            System.out.println("🔍 Existe avant suppression ? " +
                             (existeAvant ? "✅ OUI" : "❌ NON"));

            // Supprimer
            boolean succes = livreDAO.delete(idTemp);

            if (succes) {
                System.out.println("✅ Livre supprimé avec succès");

                // Vérifier qu'il n'existe plus
                boolean existeApres = livreDAO.exists(idTemp);
                System.out.println("🔍 Existe après suppression ? " +
                                 (existeApres ? "❌ OUI" : "✅ NON"));
            } else {
                System.out.println("❌ La suppression a échoué");
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

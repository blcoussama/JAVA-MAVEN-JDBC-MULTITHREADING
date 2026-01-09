package com.bibliotech.main;

import com.bibliotech.services.BibliothequeService;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Auteur;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Categorie;
import com.bibliotech.exceptions.LivreNonTrouveException;
import com.bibliotech.exceptions.MembreNonTrouveException;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;

/**
 * Tests du BibliothequeService
 *
 * Teste toutes les fonctionnalités du service :
 * - Gestion des livres (ajout, recherche, disponibilité)
 * - Gestion des membres (inscription, recherche)
 * - Validations métier
 * - Gestion des exceptions
 * - Statistiques
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class TestBibliothequeService {

    private static BibliothequeService service;

    // Compteurs de tests
    private static int testsTotal = 0;
    private static int testsReussis = 0;

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("    TESTS BibliothequeService - PHASE 3");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // Initialisation
        service = new BibliothequeService();

        try {
            // ========== TESTS GESTION LIVRES ==========
            testAjouterLivreAvecAuteurExistant();
            testAjouterLivreAvecNouvelAuteur();
            testAjouterLivreIsbnDuplique();
            testAjouterLivreValidationEchec();

            testRechercherLivresParTitre();
            testRechercherLivresParAuteur();
            testRechercherLivresParIsbn();
            testRechercherLivresSansCritere();

            testAfficherDisponibilite();
            testGetLivresDisponibles();

            testGetLivreParIsbn();
            testGetLivreParIsbnIntrouvable();

            testGetLivresParCategorie();

            // ========== TESTS GESTION MEMBRES ==========
            testInscrireMembre();
            testInscrireMembreCinDuplique();
            testInscrireMembreEmailDuplique();
            testInscrireMembreValidationEchec();

            testRechercherMembre();
            testRechercherMembreIntrouvable();

            testRechercherMembresParNom();
            testGetMembresActifs();
            testMembrePeutEmprunter();

            // ========== TESTS STATISTIQUES ==========
            testGetNombreTotalLivres();
            testGetNombreTotalMembres();
            testGetNombreLivresParCategorie();

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR FATALE LORS DES TESTS :");
            e.printStackTrace();
        }

        // Résultats finaux
        afficherResultatsFinaux();
    }

    // ==================== TESTS GESTION LIVRES ====================

    /**
     * TEST 1 : Ajouter un livre avec un auteur existant
     */
    private static void testAjouterLivreAvecAuteurExistant() {
        testsTotal++;
        System.out.println("TEST 1 : AJOUTER_LIVRE_AVEC_AUTEUR_EXISTANT");

        try {
            // Créer auteur (doit déjà exister dans la BD : Bloch Joshua)
            Auteur auteur = new Auteur("Bloch", "Joshua");
            auteur.setIdAuteur(2); // ID existant

            // Créer livre
            Livre livre = new Livre(
                "978-0134685999",
                "Effective Java - Edition Test",
                auteur,
                Categorie.TECHNOLOGIE,
                3
            );
            livre.setAnneePublication(2024);

            // Ajouter le livre
            int idLivre = service.ajouterLivre(livre);

            System.out.println("  ✓ Livre ajouté avec succès (ID: " + idLivre + ")");
            System.out.println("  ✓ Auteur utilisé : " + auteur);
            System.out.println("  ✓ Livre : " + livre);

            testsReussis++;
            System.out.println("  ✅ TEST 1 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 1 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 2 : Ajouter un livre avec un nouvel auteur
     */
    private static void testAjouterLivreAvecNouvelAuteur() {
        testsTotal++;
        System.out.println("TEST 2 : AJOUTER_LIVRE_AVEC_NOUVEL_AUTEUR");

        try {
            // Créer nouvel auteur (sans ID)
            Auteur auteur = new Auteur(
                "Kernighan",
                "Brian",
                "Canada",
                1942
            );

            // Créer livre
            Livre livre = new Livre(
                "978-0131103627",
                "The C Programming Language",
                auteur,
                Categorie.TECHNOLOGIE,
                2
            );
            livre.setAnneePublication(1988);

            // Ajouter le livre (doit créer l'auteur automatiquement)
            int idLivre = service.ajouterLivre(livre);

            System.out.println("  ✓ Livre ajouté avec succès (ID: " + idLivre + ")");
            System.out.println("  ✓ Auteur créé avec ID : " + livre.getAuteur().getIdAuteur());
            System.out.println("  ✓ Livre : " + livre);

            testsReussis++;
            System.out.println("  ✅ TEST 2 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 2 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 3 : Ajouter un livre avec ISBN dupliqué (doit échouer)
     */
    private static void testAjouterLivreIsbnDuplique() {
        testsTotal++;
        System.out.println("TEST 3 : AJOUTER_LIVRE_ISBN_DUPLIQUÉ");

        try {
            // Créer livre avec ISBN existant
            Auteur auteur = new Auteur("Test", "Auteur");
            auteur.setIdAuteur(1);

            Livre livre = new Livre(
                "978-0134685991", // ISBN déjà existant
                "Livre Test",
                auteur,
                Categorie.ROMAN,
                1
            );

            // Doit lancer DatabaseException
            service.ajouterLivre(livre);

            // Si on arrive ici, le test a échoué
            System.err.println("  ❌ TEST 3 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (DatabaseException e) {
            if (e.getMessage().contains("existe déjà")) {
                System.out.println("  ✓ Exception correctement levée : " + e.getMessage());
                testsReussis++;
                System.out.println("  ✅ TEST 3 RÉUSSI\n");
            } else {
                System.err.println("  ❌ TEST 3 ÉCHOUÉ : Mauvais message d'exception");
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("  ❌ TEST 3 ÉCHOUÉ : Mauvais type d'exception");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 4 : Validation métier - livre sans titre (doit échouer)
     */
    private static void testAjouterLivreValidationEchec() {
        testsTotal++;
        System.out.println("TEST 4 : AJOUTER_LIVRE_VALIDATION_ÉCHEC");

        try {
            // Créer livre sans titre
            Auteur auteur = new Auteur("Test", "Auteur");
            auteur.setIdAuteur(1);

            Livre livre = new Livre();
            livre.setIsbn("978-1234567890");
            livre.setTitre("");  // Titre vide : INVALIDE
            livre.setAuteur(auteur);
            livre.setCategorie(Categorie.SCIENCE);
            livre.setNombreExemplaires(1);
            livre.setDisponibles(1);

            // Doit lancer DatabaseException
            service.ajouterLivre(livre);

            System.err.println("  ❌ TEST 4 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (DatabaseException e) {
            if (e.getMessage().contains("titre") || e.getMessage().contains("obligatoire")) {
                System.out.println("  ✓ Validation échouée comme attendu : " + e.getMessage());
                testsReussis++;
                System.out.println("  ✅ TEST 4 RÉUSSI\n");
            } else {
                System.err.println("  ❌ TEST 4 ÉCHOUÉ : Mauvais message");
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("  ❌ TEST 4 ÉCHOUÉ");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 5 : Rechercher livres par titre
     */
    private static void testRechercherLivresParTitre() {
        testsTotal++;
        System.out.println("TEST 5 : RECHERCHER_LIVRES_PAR_TITRE");

        try {
            // Recherche partielle sur titre
            List<Livre> resultats = service.rechercherLivres("Java");

            System.out.println("  ✓ Recherche 'Java' : " + resultats.size() + " résultat(s)");

            if (resultats.isEmpty()) {
                System.out.println("  ⚠️  Aucun résultat (base vide ?)");
            } else {
                for (Livre livre : resultats) {
                    System.out.println("    - " + livre.getTitre());
                }
            }

            testsReussis++;
            System.out.println("  ✅ TEST 5 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 5 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 6 : Rechercher livres par auteur
     */
    private static void testRechercherLivresParAuteur() {
        testsTotal++;
        System.out.println("TEST 6 : RECHERCHER_LIVRES_PAR_AUTEUR");

        try {
            // Recherche avec espace (détecte auteur)
            List<Livre> resultats = service.rechercherLivres("Robert Martin");

            System.out.println("  ✓ Recherche 'Robert Martin' : " + resultats.size() + " résultat(s)");

            for (Livre livre : resultats) {
                System.out.println("    - " + livre.getTitre() +
                                 " par " + livre.getAuteur());
            }

            testsReussis++;
            System.out.println("  ✅ TEST 6 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 6 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 7 : Rechercher livre par ISBN
     */
    private static void testRechercherLivresParIsbn() {
        testsTotal++;
        System.out.println("TEST 7 : RECHERCHER_LIVRES_PAR_ISBN");

        try {
            // Recherche par ISBN (commence par 978)
            List<Livre> resultats = service.rechercherLivres("978-0134685991");

            if (resultats.size() == 1) {
                System.out.println("  ✓ ISBN trouvé : " + resultats.get(0).getTitre());
                testsReussis++;
                System.out.println("  ✅ TEST 7 RÉUSSI\n");
            } else {
                System.err.println("  ❌ TEST 7 ÉCHOUÉ : " + resultats.size() + " résultat(s)");
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("  ❌ TEST 7 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 8 : Rechercher sans critère (retourne tous les livres)
     */
    private static void testRechercherLivresSansCritere() {
        testsTotal++;
        System.out.println("TEST 8 : RECHERCHER_LIVRES_SANS_CRITÈRE");

        try {
            // Recherche vide = tous les livres
            List<Livre> resultats = service.rechercherLivres("");

            System.out.println("  ✓ Nombre total de livres : " + resultats.size());

            testsReussis++;
            System.out.println("  ✅ TEST 8 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 8 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 9 : Afficher disponibilité de tous les livres
     */
    private static void testAfficherDisponibilite() {
        testsTotal++;
        System.out.println("TEST 9 : AFFICHER_DISPONIBILITÉ");

        try {
            List<Livre> livres = service.afficherDisponibilite();

            System.out.println("  ✓ Livres dans le catalogue : " + livres.size());
            System.out.println("\n  📚 CATALOGUE COMPLET :");
            System.out.println("  " + "═".repeat(80));

            for (Livre livre : livres) {
                String dispo = livre.estDisponible() ? "✓ DISPONIBLE" : "✗ INDISPONIBLE";
                System.out.printf("  %-40s %-20s %s (%d/%d)%n",
                    livre.getTitre(),
                    livre.getAuteur().getNom(),
                    dispo,
                    livre.getDisponibles(),
                    livre.getNombreExemplaires()
                );
            }
            System.out.println("  " + "═".repeat(80));

            testsReussis++;
            System.out.println("  ✅ TEST 9 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 9 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 10 : Récupérer uniquement les livres disponibles
     */
    private static void testGetLivresDisponibles() {
        testsTotal++;
        System.out.println("TEST 10 : GET_LIVRES_DISPONIBLES");

        try {
            List<Livre> disponibles = service.getLivresDisponibles();

            System.out.println("  ✓ Livres disponibles : " + disponibles.size());

            for (Livre livre : disponibles) {
                System.out.println("    - " + livre.getTitre() +
                                 " (" + livre.getDisponibles() + " exemplaire(s))");
            }

            testsReussis++;
            System.out.println("  ✅ TEST 10 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 10 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 11 : Récupérer un livre par ISBN (avec exception)
     */
    private static void testGetLivreParIsbn() {
        testsTotal++;
        System.out.println("TEST 11 : GET_LIVRE_PAR_ISBN");

        try {
            Livre livre = service.getLivreParIsbn("978-0134685991");

            System.out.println("  ✓ Livre trouvé : " + livre.getTitre());
            System.out.println("  ✓ Auteur : " + livre.getAuteur());

            testsReussis++;
            System.out.println("  ✅ TEST 11 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 11 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 12 : Récupérer livre par ISBN introuvable (doit lancer exception)
     */
    private static void testGetLivreParIsbnIntrouvable() {
        testsTotal++;
        System.out.println("TEST 12 : GET_LIVRE_PAR_ISBN_INTROUVABLE");

        try {
            service.getLivreParIsbn("978-9999999999");

            System.err.println("  ❌ TEST 12 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (LivreNonTrouveException e) {
            System.out.println("  ✓ LivreNonTrouveException correctement levée");
            System.out.println("  ✓ Message : " + e.getMessage());
            System.out.println("  ✓ Code : " + e.getCode());

            testsReussis++;
            System.out.println("  ✅ TEST 12 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 12 ÉCHOUÉ : Mauvais type d'exception");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 13 : Récupérer livres par catégorie
     */
    private static void testGetLivresParCategorie() {
        testsTotal++;
        System.out.println("TEST 13 : GET_LIVRES_PAR_CATÉGORIE");

        try {
            List<Livre> livres = service.getLivresParCategorie(Categorie.TECHNOLOGIE);

            System.out.println("  ✓ Livres TECHNOLOGIE : " + livres.size());

            for (Livre livre : livres) {
                System.out.println("    - " + livre.getTitre());
            }

            testsReussis++;
            System.out.println("  ✅ TEST 13 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 13 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    // ==================== TESTS GESTION MEMBRES ====================

    /**
     * TEST 14 : Inscrire un nouveau membre
     */
    private static void testInscrireMembre() {
        testsTotal++;
        System.out.println("TEST 14 : INSCRIRE_MEMBRE");

        try {
            Membre membre = new Membre(
                "ZZ123456",
                "Idrissi",
                "Karim",
                "karim.idrissi@email.com",
                "0656789012"
            );

            int idMembre = service.inscrireMembre(membre);

            System.out.println("  ✓ Membre inscrit avec succès (ID: " + idMembre + ")");
            System.out.println("  ✓ Membre : " + membre);
            System.out.println("  ✓ Date inscription : " + membre.getDateInscription());
            System.out.println("  ✓ Emprunts actifs : " + membre.getNombreEmpruntsActifs());

            testsReussis++;
            System.out.println("  ✅ TEST 14 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 14 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 15 : Inscrire membre avec CIN dupliqué (doit échouer)
     */
    private static void testInscrireMembreCinDuplique() {
        testsTotal++;
        System.out.println("TEST 15 : INSCRIRE_MEMBRE_CIN_DUPLIQUÉ");

        try {
            Membre membre = new Membre(
                "AB123456", // CIN existant
                "Test",
                "Membre",
                "test.unique@email.com"
            );

            service.inscrireMembre(membre);

            System.err.println("  ❌ TEST 15 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (DatabaseException e) {
            if (e.getMessage().contains("CIN") && e.getMessage().contains("existe")) {
                System.out.println("  ✓ Exception correctement levée : " + e.getMessage());
                testsReussis++;
                System.out.println("  ✅ TEST 15 RÉUSSI\n");
            } else {
                System.err.println("  ❌ TEST 15 ÉCHOUÉ : Mauvais message");
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("  ❌ TEST 15 ÉCHOUÉ");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 16 : Inscrire membre avec email dupliqué (doit échouer)
     */
    private static void testInscrireMembreEmailDuplique() {
        testsTotal++;
        System.out.println("TEST 16 : INSCRIRE_MEMBRE_EMAIL_DUPLIQUÉ");

        try {
            Membre membre = new Membre(
                "XX999999",
                "Test",
                "Membre",
                "ahmed.alami@email.com" // Email existant
            );

            service.inscrireMembre(membre);

            System.err.println("  ❌ TEST 16 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (DatabaseException e) {
            if (e.getMessage().contains("email") && e.getMessage().contains("existe")) {
                System.out.println("  ✓ Exception correctement levée : " + e.getMessage());
                testsReussis++;
                System.out.println("  ✅ TEST 16 RÉUSSI\n");
            } else {
                System.err.println("  ❌ TEST 16 ÉCHOUÉ : Mauvais message");
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("  ❌ TEST 16 ÉCHOUÉ");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 17 : Validation métier - membre sans email (doit échouer)
     */
    private static void testInscrireMembreValidationEchec() {
        testsTotal++;
        System.out.println("TEST 17 : INSCRIRE_MEMBRE_VALIDATION_ÉCHEC");

        try {
            Membre membre = new Membre();
            membre.setCin("YY888888");
            membre.setNom("Test");
            membre.setPrenom("Membre");
            membre.setEmail(""); // Email vide : INVALIDE

            service.inscrireMembre(membre);

            System.err.println("  ❌ TEST 17 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (DatabaseException e) {
            if (e.getMessage().contains("email") || e.getMessage().contains("obligatoire")) {
                System.out.println("  ✓ Validation échouée comme attendu : " + e.getMessage());
                testsReussis++;
                System.out.println("  ✅ TEST 17 RÉUSSI\n");
            } else {
                System.err.println("  ❌ TEST 17 ÉCHOUÉ : Mauvais message");
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("  ❌ TEST 17 ÉCHOUÉ");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 18 : Rechercher un membre par CIN
     */
    private static void testRechercherMembre() {
        testsTotal++;
        System.out.println("TEST 18 : RECHERCHER_MEMBRE");

        try {
            Membre membre = service.rechercherMembre("AB123456");

            System.out.println("  ✓ Membre trouvé : " + membre.getNom() + " " + membre.getPrenom());
            System.out.println("  ✓ Email : " + membre.getEmail());
            System.out.println("  ✓ Emprunts actifs : " + membre.getNombreEmpruntsActifs());

            testsReussis++;
            System.out.println("  ✅ TEST 18 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 18 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 19 : Rechercher membre introuvable (doit lancer exception)
     */
    private static void testRechercherMembreIntrouvable() {
        testsTotal++;
        System.out.println("TEST 19 : RECHERCHER_MEMBRE_INTROUVABLE");

        try {
            service.rechercherMembre("XX999999");

            System.err.println("  ❌ TEST 19 ÉCHOUÉ : Exception attendue non levée");
            System.out.println();

        } catch (MembreNonTrouveException e) {
            System.out.println("  ✓ MembreNonTrouveException correctement levée");
            System.out.println("  ✓ Message : " + e.getMessage());
            System.out.println("  ✓ Code : " + e.getCode());

            testsReussis++;
            System.out.println("  ✅ TEST 19 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 19 ÉCHOUÉ : Mauvais type d'exception");
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 20 : Rechercher membres par nom
     */
    private static void testRechercherMembresParNom() {
        testsTotal++;
        System.out.println("TEST 20 : RECHERCHER_MEMBRES_PAR_NOM");

        try {
            List<Membre> membres = service.rechercherMembresParNom("Alami");

            System.out.println("  ✓ Membres trouvés : " + membres.size());

            for (Membre membre : membres) {
                System.out.println("    - " + membre.getNom() + " " + membre.getPrenom());
            }

            testsReussis++;
            System.out.println("  ✅ TEST 20 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 20 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 21 : Récupérer membres actifs (avec emprunts)
     */
    private static void testGetMembresActifs() {
        testsTotal++;
        System.out.println("TEST 21 : GET_MEMBRES_ACTIFS");

        try {
            List<Membre> actifs = service.getMembresActifs();

            System.out.println("  ✓ Membres actifs : " + actifs.size());

            for (Membre membre : actifs) {
                System.out.println("    - " + membre.getNom() + " : " +
                                 membre.getNombreEmpruntsActifs() + " emprunt(s)");
            }

            testsReussis++;
            System.out.println("  ✅ TEST 21 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 21 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 22 : Vérifier si un membre peut emprunter
     */
    private static void testMembrePeutEmprunter() {
        testsTotal++;
        System.out.println("TEST 22 : MEMBRE_PEUT_EMPRUNTER");

        try {
            boolean peutEmprunter = service.membrePeutEmprunter("AB123456");

            System.out.println("  ✓ Membre AB123456 peut emprunter : " +
                             (peutEmprunter ? "OUI" : "NON"));

            testsReussis++;
            System.out.println("  ✅ TEST 22 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 22 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    // ==================== TESTS STATISTIQUES ====================

    /**
     * TEST 23 : Compter le nombre total de livres
     */
    private static void testGetNombreTotalLivres() {
        testsTotal++;
        System.out.println("TEST 23 : GET_NOMBRE_TOTAL_LIVRES");

        try {
            long total = service.getNombreTotalLivres();

            System.out.println("  ✓ Nombre total de livres : " + total);

            testsReussis++;
            System.out.println("  ✅ TEST 23 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 23 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 24 : Compter le nombre total de membres
     */
    private static void testGetNombreTotalMembres() {
        testsTotal++;
        System.out.println("TEST 24 : GET_NOMBRE_TOTAL_MEMBRES");

        try {
            long total = service.getNombreTotalMembres();

            System.out.println("  ✓ Nombre total de membres : " + total);

            testsReussis++;
            System.out.println("  ✅ TEST 24 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 24 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * TEST 25 : Compter livres par catégorie
     */
    private static void testGetNombreLivresParCategorie() {
        testsTotal++;
        System.out.println("TEST 25 : GET_NOMBRE_LIVRES_PAR_CATÉGORIE");

        try {
            System.out.println("\n  📊 RÉPARTITION PAR CATÉGORIE :");
            System.out.println("  " + "═".repeat(50));

            for (Categorie cat : Categorie.values()) {
                long nombre = service.getNombreLivresParCategorie(cat);
                System.out.printf("  %-20s : %d livre(s)%n", cat, nombre);
            }

            System.out.println("  " + "═".repeat(50));

            testsReussis++;
            System.out.println("  ✅ TEST 25 RÉUSSI\n");

        } catch (Exception e) {
            System.err.println("  ❌ TEST 25 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    // ==================== RÉSULTATS ====================

    private static void afficherResultatsFinaux() {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("    RÉSULTATS FINAUX");
        System.out.println("═══════════════════════════════════════════════════════════");

        System.out.println("\n  Tests exécutés : " + testsTotal);
        System.out.println("  Tests réussis  : " + testsReussis);
        System.out.println("  Tests échoués  : " + (testsTotal - testsReussis));

        double pourcentage = (testsTotal > 0)
            ? (testsReussis * 100.0 / testsTotal)
            : 0;

        System.out.printf("  Taux de réussite : %.1f%%%n", pourcentage);

        if (testsReussis == testsTotal) {
            System.out.println("\n  ✅✅✅ TOUS LES TESTS ONT RÉUSSI ! ✅✅✅");
        } else {
            System.out.println("\n  ⚠️  CERTAINS TESTS ONT ÉCHOUÉ");
        }

        System.out.println("═══════════════════════════════════════════════════════════\n");
    }
}

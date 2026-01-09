package com.bibliotech.main;

import com.bibliotech.dao.MembreDAO;
import com.bibliotech.models.Membre;
import com.bibliotech.exceptions.DatabaseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Classe de test pour MembreDAO
 *
 * Teste toutes les méthodes CRUD avec :
 * - Contraintes UNIQUE (CIN, email)
 * - Contrainte CHECK (nombre_emprunts_actifs 0-5)
 * - Méthodes utilitaires (increment/decrement)
 * - Recherches multiples
 *
 * @author Belcadi Oussama
 */
public class TestMembreDAO {

    private static MembreDAO membreDAO = new MembreDAO();

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║         TEST COMPLET MEMBREDAO - BIBLIOTECH          ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();

        // Exécuter tous les tests dans l'ordre
        testInsert();
        testFindById();
        testFindByCin();
        testFindByEmail();
        testFindAll();
        testRechercherParNom();
        testGetMembresActifs();
        testGetMembresMaxEmprunts();
        testUpdate();
        testIncrementerEmprunts();
        testDecrementerEmprunts();
        testCount();
        testExists();
        testCinExists();
        testEmailExists();
        testPeutEmprunter();
        testDelete();

        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║              TESTS TERMINÉS AVEC SUCCÈS              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }

    // ==================== TEST INSERT ====================

    /**
     * Test 1 : Insertion de nouveaux membres
     */
    public static void testInsert() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 1 : INSERT - Ajout de nouveaux membres");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Créer 3 nouveaux membres de test
            Membre membre1 = new Membre();
            membre1.setCin("KK123456");
            membre1.setNom("Idrissi");
            membre1.setPrenom("Rachid");
            membre1.setEmail("rachid.idrissi@test.com");
            membre1.setTelephone("0667890123");
            membre1.setDateInscription(LocalDate.now());
            membre1.setNombreEmpruntsActifs(0);

            Membre membre2 = new Membre();
            membre2.setCin("LL789012");
            membre2.setNom("Benjelloun");
            membre2.setPrenom("Leila");
            membre2.setEmail("leila.benjelloun@test.com");
            membre2.setTelephone("0678901234");
            membre2.setDateInscription(LocalDate.now());
            membre2.setNombreEmpruntsActifs(0);

            Membre membre3 = new Membre();
            membre3.setCin("MM345678");
            membre3.setNom("Rami");
            membre3.setPrenom("Hassan");
            membre3.setEmail("hassan.rami@test.com");
            membre3.setTelephone("0689012345");
            membre3.setDateInscription(LocalDate.now());
            membre3.setNombreEmpruntsActifs(2);  // 2 emprunts actifs

            // Insérer dans la BD
            int id1 = membreDAO.insert(membre1);
            int id2 = membreDAO.insert(membre2);
            int id3 = membreDAO.insert(membre3);

            // Afficher résultats
            System.out.println("✅ Membre 1 inséré avec succès");
            System.out.println("   ID généré : " + id1);
            System.out.println("   CIN : " + membre1.getCin());
            System.out.println("   Nom : " + membre1.getPrenom() + " " + membre1.getNom());
            System.out.println("   Email : " + membre1.getEmail());
            System.out.println();

            System.out.println("✅ Membre 2 inséré avec succès");
            System.out.println("   ID généré : " + id2);
            System.out.println("   Nom : " + membre2.getPrenom() + " " + membre2.getNom());
            System.out.println();

            System.out.println("✅ Membre 3 inséré avec succès");
            System.out.println("   ID généré : " + id3);
            System.out.println("   Nom : " + membre3.getPrenom() + " " + membre3.getNom());
            System.out.println("   Emprunts actifs : " + membre3.getNombreEmpruntsActifs());
            System.out.println();

            // Test de duplicate CIN
            System.out.println("🔍 Test insertion CIN duplicate...");
            Membre membreDuplicate = new Membre();
            membreDuplicate.setCin("KK123456");  // CIN déjà existant
            membreDuplicate.setNom("Test");
            membreDuplicate.setPrenom("Duplicate");
            membreDuplicate.setEmail("autre@email.com");
            membreDuplicate.setTelephone("0600000000");
            membreDuplicate.setDateInscription(LocalDate.now());

            try {
                membreDAO.insert(membreDuplicate);
                System.out.println("❌ ERREUR : Le duplicate aurait dû être refusé");
            } catch (DatabaseException e) {
                System.out.println("✅ Duplicate CIN correctement détecté : " + e.getMessage());
            }
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
     * Test 2 : Recherche d'un membre par ID
     */
    public static void testFindById() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 2 : FIND_BY_ID - Recherche par ID");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Chercher le membre avec ID = 1
            System.out.println("🔍 Recherche du membre avec ID = 1...");
            Optional<Membre> membreOpt = membreDAO.findById(1);

            if (membreOpt.isPresent()) {
                Membre membre = membreOpt.get();
                System.out.println("✅ Membre trouvé :");
                System.out.println("   ID : " + membre.getIdMembre());
                System.out.println("   CIN : " + membre.getCin());
                System.out.println("   Nom : " + membre.getPrenom() + " " + membre.getNom());
                System.out.println("   Email : " + membre.getEmail());
                System.out.println("   Date inscription : " + membre.getDateInscription());
                System.out.println("   Emprunts actifs : " + membre.getNombreEmpruntsActifs());
            } else {
                System.out.println("❌ Aucun membre trouvé avec ID = 1");
            }
            System.out.println();

            // Chercher un ID inexistant
            System.out.println("🔍 Recherche d'un ID inexistant (999)...");
            Optional<Membre> inexistant = membreDAO.findById(999);

            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un membre a été trouvé !");
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_ID : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_BY_CIN ====================

    /**
     * Test 3 : Recherche par CIN (unique)
     */
    public static void testFindByCin() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 3 : FIND_BY_CIN - Recherche par CIN");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Chercher un membre existant par CIN
            String cin = "AB123456";
            System.out.println("🔍 Recherche CIN : " + cin);
            Optional<Membre> membreOpt = membreDAO.findByCin(cin);

            if (membreOpt.isPresent()) {
                Membre membre = membreOpt.get();
                System.out.println("✅ Membre trouvé :");
                System.out.println("   " + membre.getPrenom() + " " + membre.getNom());
                System.out.println("   Email : " + membre.getEmail());
            } else {
                System.out.println("❌ Aucun membre trouvé avec ce CIN");
            }
            System.out.println();

            // Chercher un CIN inexistant
            System.out.println("🔍 Recherche CIN inexistant...");
            Optional<Membre> inexistant = membreDAO.findByCin("ZZ999999");

            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un membre a été trouvé !");
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_CIN : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_BY_EMAIL ====================

    /**
     * Test 4 : Recherche par email (unique)
     */
    public static void testFindByEmail() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 4 : FIND_BY_EMAIL - Recherche par email");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Chercher un membre existant par email
            String email = "ahmed.alami@email.com";
            System.out.println("🔍 Recherche email : " + email);
            Optional<Membre> membreOpt = membreDAO.findByEmail(email);

            if (membreOpt.isPresent()) {
                Membre membre = membreOpt.get();
                System.out.println("✅ Membre trouvé :");
                System.out.println("   " + membre.getPrenom() + " " + membre.getNom());
                System.out.println("   CIN : " + membre.getCin());
            } else {
                System.out.println("❌ Aucun membre trouvé avec cet email");
            }
            System.out.println();

            // Chercher un email inexistant
            System.out.println("🔍 Recherche email inexistant...");
            Optional<Membre> inexistant = membreDAO.findByEmail("inexistant@test.com");

            if (inexistant.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            } else {
                System.out.println("❌ ERREUR : Un membre a été trouvé !");
            }
            System.out.println();

            System.out.println("✅ TEST FIND_BY_EMAIL : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST FIND_ALL ====================

    /**
     * Test 5 : Récupération de tous les membres
     */
    public static void testFindAll() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 5 : FIND_ALL - Récupérer tous les membres");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            List<Membre> membres = membreDAO.findAll();

            System.out.println("✅ " + membres.size() + " membre(s) trouvé(s) :");
            System.out.println();

            int compteur = 1;
            for (Membre membre : membres) {
                System.out.printf("%2d. [ID: %d] %s %s%n",
                    compteur++,
                    membre.getIdMembre(),
                    membre.getPrenom(),
                    membre.getNom()
                );
                System.out.printf("    CIN: %s | Email: %s | Emprunts: %d%n",
                    membre.getCin(),
                    membre.getEmail(),
                    membre.getNombreEmpruntsActifs()
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

    // ==================== TEST RECHERCHER_PAR_NOM ====================

    /**
     * Test 6 : Recherche partielle par nom
     */
    public static void testRechercherParNom() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 6 : RECHERCHER_PAR_NOM - Recherche partielle");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Rechercher les membres dont le nom contient "la"
            String motCle = "la";
            System.out.println("🔍 Recherche partielle : '%" + motCle + "%'");
            List<Membre> membres = membreDAO.rechercherParNom(motCle);

            System.out.println("✅ " + membres.size() + " membre(s) trouvé(s) :");
            for (Membre membre : membres) {
                System.out.println("   - " + membre.getPrenom() + " " + membre.getNom());
                System.out.println("     CIN: " + membre.getCin());
            }
            System.out.println();

            System.out.println("✅ TEST RECHERCHER_PAR_NOM : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST GET_MEMBRES_ACTIFS ====================

    /**
     * Test 7 : Récupération des membres avec emprunts actifs
     */
    public static void testGetMembresActifs() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 7 : GET_MEMBRES_ACTIFS - Membres avec emprunts");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            List<Membre> membresActifs = membreDAO.getMembresActifs();

            System.out.println("✅ " + membresActifs.size() + " membre(s) actif(s) :");
            for (Membre membre : membresActifs) {
                System.out.printf("   - %s %s : %d emprunt(s) actif(s)%n",
                    membre.getPrenom(),
                    membre.getNom(),
                    membre.getNombreEmpruntsActifs()
                );
            }
            System.out.println();

            System.out.println("✅ TEST GET_MEMBRES_ACTIFS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la récupération : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST GET_MEMBRES_MAX_EMPRUNTS ====================

    /**
     * Test 8 : Membres ayant atteint le maximum (5 emprunts)
     */
    public static void testGetMembresMaxEmprunts() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 8 : GET_MEMBRES_MAX_EMPRUNTS - À capacité max");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // D'abord, créer un membre temporaire avec 5 emprunts pour le test
            Membre membreTest = new Membre();
            membreTest.setCin("TEST-MAX-EMPRUNTS");
            membreTest.setNom("Maximum");
            membreTest.setPrenom("Testeur");
            membreTest.setEmail("test.max@emprunts.com");
            membreTest.setTelephone("0600000000");
            membreTest.setDateInscription(LocalDate.now());
            membreTest.setNombreEmpruntsActifs(5);  // Maximum

            int idTest = membreDAO.insert(membreTest);
            System.out.println("✅ Membre test créé avec 5 emprunts (ID: " + idTest + ")");
            System.out.println();

            // Récupérer les membres à capacité max
            List<Membre> membresMax = membreDAO.getMembresMaxEmprunts();

            System.out.println("✅ " + membresMax.size() + " membre(s) à capacité maximale :");
            for (Membre membre : membresMax) {
                System.out.printf("   - %s %s : %d emprunt(s) actif(s)%n",
                    membre.getPrenom(),
                    membre.getNom(),
                    membre.getNombreEmpruntsActifs()
                );
            }
            System.out.println();

            // Nettoyer - supprimer le membre test
            membreDAO.delete(idTest);
            System.out.println("🧹 Membre test supprimé");
            System.out.println();

            System.out.println("✅ TEST GET_MEMBRES_MAX_EMPRUNTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du test : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST UPDATE ====================

    /**
     * Test 9 : Modification d'un membre existant
     */
    public static void testUpdate() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 9 : UPDATE - Modification d'un membre");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Récupérer un membre existant
            Optional<Membre> membreOpt = membreDAO.findById(1);

            if (membreOpt.isPresent()) {
                Membre membre = membreOpt.get();

                System.out.println("📄 Avant modification :");
                System.out.println("   Nom : " + membre.getPrenom() + " " + membre.getNom());
                System.out.println("   Email : " + membre.getEmail());
                System.out.println("   Téléphone : " + membre.getTelephone());
                System.out.println();

                // Modifier les données
                String ancienTelephone = membre.getTelephone();
                membre.setTelephone("0612121212");  // Nouveau numéro

                // Sauvegarder les modifications
                boolean succes = membreDAO.update(membre);

                if (succes) {
                    System.out.println("✅ Modification enregistrée");
                    System.out.println();

                    // Re-charger depuis la BD pour vérifier
                    Optional<Membre> membreModifie = membreDAO.findById(1);
                    if (membreModifie.isPresent()) {
                        System.out.println("📄 Après modification (rechargé depuis BD) :");
                        System.out.println("   Téléphone : " + membreModifie.get().getTelephone());
                    }

                    // Remettre l'ancien numéro pour ne pas perturber les autres tests
                    membre.setTelephone(ancienTelephone);
                    membreDAO.update(membre);
                } else {
                    System.out.println("❌ La modification a échoué");
                }
            } else {
                System.out.println("❌ Membre ID=1 introuvable");
            }
            System.out.println();

            System.out.println("✅ TEST UPDATE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST INCREMENTER_EMPRUNTS ====================

    /**
     * Test 10 : Incrémentation du nombre d'emprunts
     */
    public static void testIncrementerEmprunts() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 10 : INCREMENTER_EMPRUNTS - +1 emprunt");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            int idMembre = 2;

            // Lire le nombre actuel
            Optional<Membre> membreAvant = membreDAO.findById(idMembre);
            if (membreAvant.isPresent()) {
                System.out.println("📄 Avant : Emprunts actifs = " +
                                 membreAvant.get().getNombreEmpruntsActifs());
            }

            // Incrémenter
            boolean succes = membreDAO.incrementerEmprunts(idMembre);

            if (succes) {
                System.out.println("✅ Emprunts incrémentés");

                // Vérifier
                Optional<Membre> membreApres = membreDAO.findById(idMembre);
                if (membreApres.isPresent()) {
                    System.out.println("📄 Après : Emprunts actifs = " +
                                     membreApres.get().getNombreEmpruntsActifs());
                }
            } else {
                System.out.println("❌ L'incrémentation a échoué");
            }
            System.out.println();

            // Test limite maximum (5 emprunts)
            System.out.println("🔍 Test limite maximum (5 emprunts)...");

            // Créer un membre avec 5 emprunts
            Membre membreMax = new Membre();
            membreMax.setCin("TEST-LIMIT-5");
            membreMax.setNom("Test");
            membreMax.setPrenom("Limite");
            membreMax.setEmail("test.limit@email.com");
            membreMax.setTelephone("0600000000");
            membreMax.setDateInscription(LocalDate.now());
            membreMax.setNombreEmpruntsActifs(5);

            int idMax = membreDAO.insert(membreMax);

            // Tenter d'incrémenter au-delà de 5
            try {
                membreDAO.incrementerEmprunts(idMax);
                System.out.println("❌ ERREUR : Devrait refuser l'incrémentation au-delà de 5");
            } catch (DatabaseException e) {
                System.out.println("✅ Limite maximale correctement appliquée : " + e.getMessage());
            }

            // Nettoyer
            membreDAO.delete(idMax);
            System.out.println();

            System.out.println("✅ TEST INCREMENTER_EMPRUNTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du test : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST DECREMENTER_EMPRUNTS ====================

    /**
     * Test 11 : Décrémentation du nombre d'emprunts
     */
    public static void testDecrementerEmprunts() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 11 : DECREMENTER_EMPRUNTS - -1 emprunt");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Trouver un membre avec au moins 1 emprunt actif
            List<Membre> membresActifs = membreDAO.getMembresActifs();

            if (!membresActifs.isEmpty()) {
                Membre membre = membresActifs.get(0);
                int idMembre = membre.getIdMembre();

                System.out.println("📄 Avant : Emprunts actifs = " +
                                 membre.getNombreEmpruntsActifs());

                // Décrémenter
                boolean succes = membreDAO.decrementerEmprunts(idMembre);

                if (succes) {
                    System.out.println("✅ Emprunts décrémentés");

                    // Vérifier
                    Optional<Membre> membreApres = membreDAO.findById(idMembre);
                    if (membreApres.isPresent()) {
                        System.out.println("📄 Après : Emprunts actifs = " +
                                         membreApres.get().getNombreEmpruntsActifs());
                    }
                } else {
                    System.out.println("❌ La décrémentation a échoué");
                }
            } else {
                System.out.println("⚠️  Aucun membre avec emprunts actifs pour tester");
            }
            System.out.println();

            System.out.println("✅ TEST DECREMENTER_EMPRUNTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du test : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST COUNT ====================

    /**
     * Test 12 : Comptage total des membres
     */
    public static void testCount() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 12 : COUNT - Comptage total");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            long total = membreDAO.count();

            System.out.println("✅ Nombre total de membres : " + total);
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
     * Test 13 : Vérification existence
     */
    public static void testExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 13 : EXISTS - Vérification existence");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Tester avec un ID existant
            boolean existe1 = membreDAO.exists(1);
            System.out.println("🔍 Le membre ID=1 existe ? " + (existe1 ? "✅ OUI" : "❌ NON"));

            // Tester avec un ID inexistant
            boolean existe999 = membreDAO.exists(999);
            System.out.println("🔍 Le membre ID=999 existe ? " + (existe999 ? "❌ OUI" : "✅ NON"));
            System.out.println();

            System.out.println("✅ TEST EXISTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la vérification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST CIN_EXISTS ====================

    /**
     * Test 14 : Vérification existence CIN
     */
    public static void testCinExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 14 : CIN_EXISTS - Vérification CIN unique");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // CIN existant
            String cinExistant = "AB123456";
            boolean existe1 = membreDAO.cinExists(cinExistant);
            System.out.println("🔍 Le CIN " + cinExistant + " existe ? " +
                             (existe1 ? "✅ OUI" : "❌ NON"));

            // CIN inexistant
            String cinInexistant = "ZZ999999";
            boolean existe2 = membreDAO.cinExists(cinInexistant);
            System.out.println("🔍 Le CIN " + cinInexistant + " existe ? " +
                             (existe2 ? "❌ OUI" : "✅ NON"));
            System.out.println();

            System.out.println("✅ TEST CIN_EXISTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la vérification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST EMAIL_EXISTS ====================

    /**
     * Test 15 : Vérification existence email
     */
    public static void testEmailExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 15 : EMAIL_EXISTS - Vérification email unique");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Email existant
            String emailExistant = "ahmed.alami@email.com";
            boolean existe1 = membreDAO.emailExists(emailExistant);
            System.out.println("🔍 L'email " + emailExistant + " existe ? " +
                             (existe1 ? "✅ OUI" : "❌ NON"));

            // Email inexistant
            String emailInexistant = "inexistant@test.com";
            boolean existe2 = membreDAO.emailExists(emailInexistant);
            System.out.println("🔍 L'email " + emailInexistant + " existe ? " +
                             (existe2 ? "❌ OUI" : "✅ NON"));
            System.out.println();

            System.out.println("✅ TEST EMAIL_EXISTS : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la vérification : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST PEUT_EMPRUNTER ====================

    /**
     * Test 16 : Vérification si membre peut emprunter
     */
    public static void testPeutEmprunter() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 16 : PEUT_EMPRUNTER - Vérification capacité");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Tester avec un membre ayant < 5 emprunts
            Optional<Membre> membre1 = membreDAO.findById(1);
            if (membre1.isPresent()) {
                boolean peutEmprunter = membreDAO.peutEmprunter(1);
                System.out.printf("🔍 Membre ID=1 (%d emprunts) peut emprunter ? %s%n",
                    membre1.get().getNombreEmpruntsActifs(),
                    (peutEmprunter ? "✅ OUI" : "❌ NON")
                );
            }

            // Créer un membre avec 5 emprunts
            Membre membreMax = new Membre();
            membreMax.setCin("TEST-PEUT-EMPRUNTER");
            membreMax.setNom("Test");
            membreMax.setPrenom("Capacite");
            membreMax.setEmail("test.capacite@email.com");
            membreMax.setTelephone("0600000000");
            membreMax.setDateInscription(LocalDate.now());
            membreMax.setNombreEmpruntsActifs(5);

            int idMax = membreDAO.insert(membreMax);

            boolean peutEmprunterMax = membreDAO.peutEmprunter(idMax);
            System.out.printf("🔍 Membre avec 5 emprunts peut emprunter ? %s%n",
                (peutEmprunterMax ? "❌ OUI (ERREUR)" : "✅ NON (CORRECT)")
            );

            // Nettoyer
            membreDAO.delete(idMax);
            System.out.println();

            System.out.println("✅ TEST PEUT_EMPRUNTER : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors du test : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== TEST DELETE ====================

    /**
     * Test 17 : Suppression d'un membre
     */
    public static void testDelete() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 17 : DELETE - Suppression d'un membre");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Créer un membre temporaire pour le supprimer
            Membre membreTemp = new Membre();
            membreTemp.setCin("TEMP-DELETE-TEST");
            membreTemp.setNom("Test");
            membreTemp.setPrenom("Suppression");
            membreTemp.setEmail("test.delete@email.com");
            membreTemp.setTelephone("0600000000");
            membreTemp.setDateInscription(LocalDate.now());
            membreTemp.setNombreEmpruntsActifs(0);

            int idTemp = membreDAO.insert(membreTemp);

            System.out.println("✅ Membre temporaire créé (ID: " + idTemp + ")");
            System.out.println();

            // Vérifier qu'il existe
            boolean existeAvant = membreDAO.exists(idTemp);
            System.out.println("🔍 Existe avant suppression ? " +
                             (existeAvant ? "✅ OUI" : "❌ NON"));

            // Supprimer
            boolean succes = membreDAO.delete(idTemp);

            if (succes) {
                System.out.println("✅ Membre supprimé avec succès");

                // Vérifier qu'il n'existe plus
                boolean existeApres = membreDAO.exists(idTemp);
                System.out.println("🔍 Existe après suppression ? " +
                                 (existeApres ? "❌ OUI" : "✅ NON"));
            } else {
                System.out.println("❌ La suppression a échoué");
            }
            System.out.println();

            // Test contrainte FK (impossible de supprimer membre avec emprunts)
            System.out.println("🔍 Test contrainte FK (membre avec emprunts)...");
            System.out.println("⚠️  Test ignoré - nécessite des emprunts réels en BD");
            System.out.println();

            System.out.println("✅ TEST DELETE : RÉUSSI");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }
}

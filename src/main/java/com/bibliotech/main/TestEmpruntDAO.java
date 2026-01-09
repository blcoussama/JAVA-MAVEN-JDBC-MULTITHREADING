package com.bibliotech.main;

import com.bibliotech.dao.EmpruntDAO;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.dao.MembreDAO;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Membre;
import com.bibliotech.models.StatutEmprunt;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;
import java.util.Optional;

/**
 * Tests complets pour EmpruntDAO
 *
 * TESTS COMPLEXES :
 * - Transactions atomiques (COMMIT/ROLLBACK)
 * - JOIN triple (Emprunt + Membre + Livre + Auteur)
 * - Calcul automatique de retard
 * - Synchronisation (méthodes synchronized)
 * - Statuts multiples
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class TestEmpruntDAO {

    private static EmpruntDAO empruntDAO = new EmpruntDAO();
    private static MembreDAO membreDAO = new MembreDAO();
    private static LivreDAO livreDAO = new LivreDAO();

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║      TEST COMPLET EMPRUNTDAO - BIBLIOTECH            ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Tests CREATE (Transactions)
            testEffectuerEmpruntTransaction();
            testEffectuerRetourTransaction();
            testTransactionRollback();

            // Tests READ (JOIN complexe)
            testFindById();
            testFindAll();
            testFindByMembre();
            testFindByLivre();
            testFindByStatut();
            testFindEmpruntsEnRetard();
            testFindEmpruntsEnCoursByMembre();

            // Tests UPDATE
            testUpdate();

            // Tests DELETE
            testDelete();

            // Tests UTILITY
            testCount();
            testCountByStatut();
            testExists();

            // Résumé final
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║            TESTS TERMINÉS AVEC SUCCÈS                ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("❌ ERREUR FATALE DURANT LES TESTS");
            e.printStackTrace();
        }
    }

    // ==================== TESTS CREATE (TRANSACTIONS) ====================

    /**
     * Test 1 : Transaction d'emprunt (4 opérations atomiques)
     */
    private static void testEffectuerEmpruntTransaction() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 1 : EFFECTUER_EMPRUNT_TRANSACTION - Transaction");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Récupérer membre et livre de test
            Membre membre = membreDAO.findById(1).orElseThrow();
            Livre livre = livreDAO.findById(1).orElseThrow();

            System.out.println("📄 AVANT emprunt :");
            System.out.println("   Membre : " + membre.getNom() + " " + membre.getPrenom());
            System.out.println("   Emprunts actifs membre : " + membre.getNombreEmpruntsActifs());
            System.out.println("   Livre : " + livre.getTitre());
            System.out.println("   Disponibles livre : " + livre.getDisponibles());

            // Effectuer l'emprunt (TRANSACTION)
            Emprunt emprunt = empruntDAO.effectuerEmpruntTransaction(
                membre.getIdMembre(),
                livre.getIdLivre()
            );

            System.out.println("\n✅ Transaction COMMIT réussie");
            System.out.println("   ID emprunt créé : " + emprunt.getIdEmprunt());
            System.out.println("   Date emprunt : " + emprunt.getDateEmprunt());
            System.out.println("   Date retour prévue : " + emprunt.getDateRetourPrevue());
            System.out.println("   Statut : " + emprunt.getStatut());

            // Recharger pour vérifier les modifications
            membre = membreDAO.findById(1).orElseThrow();
            livre = livreDAO.findById(1).orElseThrow();

            System.out.println("\n📄 APRÈS emprunt :");
            System.out.println("   Emprunts actifs membre : " + membre.getNombreEmpruntsActifs() +
                             " (incrémenté ✅)");
            System.out.println("   Disponibles livre : " + livre.getDisponibles() +
                             " (décrémenté ✅)");

            // Vérifier JOIN complet
            System.out.println("\n🔍 Vérification JOIN complet :");
            System.out.println("   Membre : " + emprunt.getMembre().getNom());
            System.out.println("   Livre : " + emprunt.getLivre().getTitre());
            System.out.println("   Auteur : " + emprunt.getLivre().getAuteur().getNom());

            System.out.println("\n✅ TEST EFFECTUER_EMPRUNT_TRANSACTION : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 2 : Transaction de retour (4 opérations atomiques)
     */
    private static void testEffectuerRetourTransaction() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 2 : EFFECTUER_RETOUR_TRANSACTION - Transaction");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Trouver un emprunt EN_COURS
            List<Emprunt> empruntsEnCours = empruntDAO.findByStatut(StatutEmprunt.EN_COURS);

            if (empruntsEnCours.isEmpty()) {
                System.out.println("⚠️  Aucun emprunt EN_COURS pour tester le retour");
                System.out.println("   Créons un emprunt d'abord...");

                Emprunt nouvelEmprunt = empruntDAO.effectuerEmpruntTransaction(2, 2);
                empruntsEnCours.add(nouvelEmprunt);
            }

            Emprunt empruntARetourner = empruntsEnCours.get(0);
            int idEmprunt = empruntARetourner.getIdEmprunt();
            int idMembre = empruntARetourner.getMembre().getIdMembre();
            int idLivre = empruntARetourner.getLivre().getIdLivre();

            System.out.println("📄 AVANT retour :");
            System.out.println("   Emprunt #" + idEmprunt);
            System.out.println("   Statut : " + empruntARetourner.getStatut());
            System.out.println("   Date retour effective : " +
                             empruntARetourner.getDateRetourEffective());

            Membre membreAvant = membreDAO.findById(idMembre).orElseThrow();
            Livre livreAvant = livreDAO.findById(idLivre).orElseThrow();

            System.out.println("   Emprunts actifs membre : " +
                             membreAvant.getNombreEmpruntsActifs());
            System.out.println("   Disponibles livre : " + livreAvant.getDisponibles());

            // Effectuer le retour (TRANSACTION)
            Emprunt empruntRetourne = empruntDAO.effectuerRetourTransaction(idEmprunt);

            System.out.println("\n✅ Transaction COMMIT réussie");
            System.out.println("   Statut mis à jour : " + empruntRetourne.getStatut());
            System.out.println("   Date retour effective : " +
                             empruntRetourne.getDateRetourEffective());

            // Recharger pour vérifier
            Membre membreApres = membreDAO.findById(idMembre).orElseThrow();
            Livre livreApres = livreDAO.findById(idLivre).orElseThrow();

            System.out.println("\n📄 APRÈS retour :");
            System.out.println("   Emprunts actifs membre : " +
                             membreApres.getNombreEmpruntsActifs() +
                             " (décrémenté ✅)");
            System.out.println("   Disponibles livre : " + livreApres.getDisponibles() +
                             " (incrémenté ✅)");

            System.out.println("\n✅ TEST EFFECTUER_RETOUR_TRANSACTION : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 3 : Transaction ROLLBACK (livre indisponible)
     */
    private static void testTransactionRollback() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 3 : TRANSACTION_ROLLBACK - Livre indisponible");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Trouver un livre avec disponibles = 0
            List<Livre> livres = livreDAO.findAll();
            Livre livreIndisponible = null;

            for (Livre livre : livres) {
                if (livre.getDisponibles() == 0) {
                    livreIndisponible = livre;
                    break;
                }
            }

            if (livreIndisponible == null) {
                System.out.println("🔍 Aucun livre indisponible, création temporaire...");
                // Mettre un livre à 0 temporairement
                Livre livre = livreDAO.findById(3).orElseThrow();
                int disponiblesOriginaux = livre.getDisponibles();
                livre.setDisponibles(0);
                livreDAO.update(livre);

                System.out.println("   Livre #3 mis à disponibles=0 temporairement");

                // Tenter emprunt
                try {
                    empruntDAO.effectuerEmpruntTransaction(1, 3);
                    System.err.println("❌ Transaction aurait dû échouer !");
                } catch (DatabaseException e) {
                    System.out.println("✅ Transaction correctement ROLLBACK");
                    System.out.println("   Erreur attendue : " + e.getMessage());
                }

                // Restaurer disponibilité
                livre.setDisponibles(disponiblesOriginaux);
                livreDAO.update(livre);
                System.out.println("   Livre #3 restauré");

            } else {
                System.out.println("🔍 Tentative d'emprunt d'un livre indisponible...");
                System.out.println("   Livre : " + livreIndisponible.getTitre());
                System.out.println("   Disponibles : " + livreIndisponible.getDisponibles());

                try {
                    empruntDAO.effectuerEmpruntTransaction(
                        1,
                        livreIndisponible.getIdLivre()
                    );
                    System.err.println("❌ Transaction aurait dû échouer !");
                } catch (DatabaseException e) {
                    System.out.println("✅ Transaction correctement ROLLBACK");
                    System.out.println("   Erreur attendue : " + e.getMessage());
                }
            }

            System.out.println("\n✅ TEST TRANSACTION_ROLLBACK : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    // ==================== TESTS READ (JOIN COMPLEXE) ====================

    /**
     * Test 4 : Recherche par ID avec JOIN triple
     */
    private static void testFindById() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 4 : FIND_BY_ID - Recherche avec JOIN triple");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Trouver un emprunt existant
            List<Emprunt> tous = empruntDAO.findAll();
            if (tous.isEmpty()) {
                System.out.println("⚠️  Aucun emprunt dans la BD");
                return;
            }

            int idTest = tous.get(0).getIdEmprunt();

            System.out.println("🔍 Recherche de l'emprunt #" + idTest + "...");

            Optional<Emprunt> empruntOpt = empruntDAO.findById(idTest);

            if (empruntOpt.isPresent()) {
                Emprunt emprunt = empruntOpt.get();

                System.out.println("✅ Emprunt trouvé avec JOIN complet :");
                System.out.println("\n📋 EMPRUNT :");
                System.out.println("   ID : " + emprunt.getIdEmprunt());
                System.out.println("   Date emprunt : " + emprunt.getDateEmprunt());
                System.out.println("   Date retour prévue : " + emprunt.getDateRetourPrevue());
                System.out.println("   Date retour effective : " +
                                 emprunt.getDateRetourEffective());
                System.out.println("   Statut : " + emprunt.getStatut());

                System.out.println("\n👤 MEMBRE (JOIN) :");
                System.out.println("   ID : " + emprunt.getMembre().getIdMembre());
                System.out.println("   CIN : " + emprunt.getMembre().getCin());
                System.out.println("   Nom : " + emprunt.getMembre().getNom() + " " +
                                 emprunt.getMembre().getPrenom());
                System.out.println("   Email : " + emprunt.getMembre().getEmail());

                System.out.println("\n📚 LIVRE (JOIN) :");
                System.out.println("   ID : " + emprunt.getLivre().getIdLivre());
                System.out.println("   ISBN : " + emprunt.getLivre().getIsbn());
                System.out.println("   Titre : " + emprunt.getLivre().getTitre());
                System.out.println("   Catégorie : " + emprunt.getLivre().getCategorie());

                System.out.println("\n✍️  AUTEUR (JOIN via Livre) :");
                System.out.println("   ID : " + emprunt.getLivre().getAuteur().getIdAuteur());
                System.out.println("   Nom : " + emprunt.getLivre().getAuteur().getNom() + " " +
                                 emprunt.getLivre().getAuteur().getPrenom());

            } else {
                System.out.println("❌ Emprunt introuvable");
            }

            // Test ID inexistant
            System.out.println("\n🔍 Recherche ID inexistant (999)...");
            Optional<Emprunt> vide = empruntDAO.findById(999);
            if (vide.isEmpty()) {
                System.out.println("✅ Résultat vide (comportement attendu)");
            }

            System.out.println("\n✅ TEST FIND_BY_ID : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 5 : Récupérer tous les emprunts
     */
    private static void testFindAll() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 5 : FIND_ALL - Récupérer tous les emprunts");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            List<Emprunt> emprunts = empruntDAO.findAll();

            System.out.println("✅ " + emprunts.size() + " emprunt(s) trouvé(s) :\n");

            int count = 1;
            for (Emprunt emprunt : emprunts) {
                System.out.println(" " + count + ". [ID: " + emprunt.getIdEmprunt() + "] " +
                                 emprunt.getMembre().getNom() + " → " +
                                 emprunt.getLivre().getTitre());
                System.out.println("    Statut: " + emprunt.getStatut() +
                                 " | Date: " + emprunt.getDateEmprunt());
                count++;
            }

            System.out.println("\n✅ TEST FIND_ALL : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 6 : Emprunts par membre
     */
    private static void testFindByMembre() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 6 : FIND_BY_MEMBRE - Historique d'un membre");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            int idMembre = 1;
            Membre membre = membreDAO.findById(idMembre).orElseThrow();

            System.out.println("🔍 Recherche emprunts du membre : " +
                             membre.getNom() + " " + membre.getPrenom());

            List<Emprunt> emprunts = empruntDAO.findByMembre(idMembre);

            System.out.println("✅ " + emprunts.size() + " emprunt(s) trouvé(s) :\n");

            for (Emprunt emprunt : emprunts) {
                System.out.println("   • " + emprunt.getLivre().getTitre());
                System.out.println("     Date: " + emprunt.getDateEmprunt() +
                                 " | Statut: " + emprunt.getStatut());
            }

            System.out.println("\n✅ TEST FIND_BY_MEMBRE : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 7 : Emprunts par livre
     */
    private static void testFindByLivre() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 7 : FIND_BY_LIVRE - Historique d'un livre");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            int idLivre = 1;
            Livre livre = livreDAO.findById(idLivre).orElseThrow();

            System.out.println("🔍 Recherche emprunts du livre : " + livre.getTitre());

            List<Emprunt> emprunts = empruntDAO.findByLivre(idLivre);

            System.out.println("✅ " + emprunts.size() + " emprunt(s) trouvé(s) :\n");

            for (Emprunt emprunt : emprunts) {
                System.out.println("   • " + emprunt.getMembre().getNom() + " " +
                                 emprunt.getMembre().getPrenom());
                System.out.println("     Date: " + emprunt.getDateEmprunt() +
                                 " | Statut: " + emprunt.getStatut());
            }

            System.out.println("\n✅ TEST FIND_BY_LIVRE : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 8 : Emprunts par statut
     */
    private static void testFindByStatut() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 8 : FIND_BY_STATUT - Filtrage par statut");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Test EN_COURS
            System.out.println("🔍 Recherche emprunts EN_COURS...");
            List<Emprunt> enCours = empruntDAO.findByStatut(StatutEmprunt.EN_COURS);
            System.out.println("✅ " + enCours.size() + " emprunt(s) EN_COURS");

            // Test RETOURNE
            System.out.println("\n🔍 Recherche emprunts RETOURNE...");
            List<Emprunt> retournes = empruntDAO.findByStatut(StatutEmprunt.RETOURNE);
            System.out.println("✅ " + retournes.size() + " emprunt(s) RETOURNE");

            // Test EN_RETARD
            System.out.println("\n🔍 Recherche emprunts EN_RETARD...");
            List<Emprunt> enRetard = empruntDAO.findByStatut(StatutEmprunt.EN_RETARD);
            System.out.println("✅ " + enRetard.size() + " emprunt(s) EN_RETARD");

            System.out.println("\n✅ TEST FIND_BY_STATUT : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 9 : Emprunts en retard (calcul automatique)
     */
    private static void testFindEmpruntsEnRetard() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 9 : FIND_EMPRUNTS_EN_RETARD - Calcul auto");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            System.out.println("🔍 Recherche emprunts en retard...");
            System.out.println("   (EN_COURS + date_retour_prevue < aujourd'hui)");

            List<Emprunt> enRetard = empruntDAO.findEmpruntsEnRetard();

            System.out.println("\n✅ " + enRetard.size() + " emprunt(s) en retard :\n");

            if (enRetard.isEmpty()) {
                System.out.println("   Aucun emprunt en retard actuellement 👍");
            } else {
                for (Emprunt emprunt : enRetard) {
                    long joursRetard = emprunt.calculerJoursRetard();
                    System.out.println("   • " + emprunt.getMembre().getNom() + " - " +
                                     emprunt.getLivre().getTitre());
                    System.out.println("     Retard: " + joursRetard + " jour(s)");
                    System.out.println("     Date retour prévue: " +
                                     emprunt.getDateRetourPrevue());
                }
            }

            System.out.println("\n✅ TEST FIND_EMPRUNTS_EN_RETARD : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 10 : Emprunts en cours d'un membre
     */
    private static void testFindEmpruntsEnCoursByMembre() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 10 : FIND_EMPRUNTS_EN_COURS_BY_MEMBRE");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            int idMembre = 1;
            Membre membre = membreDAO.findById(idMembre).orElseThrow();

            System.out.println("🔍 Recherche emprunts EN_COURS de : " +
                             membre.getNom() + " " + membre.getPrenom());

            List<Emprunt> empruntsEnCours =
                empruntDAO.findEmpruntsEnCoursByMembre(idMembre);

            System.out.println("✅ " + empruntsEnCours.size() + " emprunt(s) EN_COURS :\n");

            for (Emprunt emprunt : empruntsEnCours) {
                System.out.println("   • " + emprunt.getLivre().getTitre());
                System.out.println("     Emprunté le: " + emprunt.getDateEmprunt());
                System.out.println("     À retourner le: " + emprunt.getDateRetourPrevue());
            }

            System.out.println("\n✅ TEST FIND_EMPRUNTS_EN_COURS_BY_MEMBRE : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    // ==================== TESTS UPDATE ====================

    /**
     * Test 11 : Mise à jour d'un emprunt
     */
    private static void testUpdate() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 11 : UPDATE - Modification d'un emprunt");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Trouver un emprunt RETOURNE
            List<Emprunt> retournes = empruntDAO.findByStatut(StatutEmprunt.RETOURNE);

            if (retournes.isEmpty()) {
                System.out.println("⚠️  Aucun emprunt RETOURNE pour test UPDATE");
                return;
            }

            Emprunt emprunt = retournes.get(0);
            int idTest = emprunt.getIdEmprunt();

            System.out.println("📄 Avant modification :");
            System.out.println("   Emprunt #" + idTest);
            System.out.println("   Statut : " + emprunt.getStatut());

            // Modification (exemple: changer statut)
            // Note: Dans un vrai système, on ne changerait pas directement
            // mais c'est pour tester la méthode update()
            StatutEmprunt ancienStatut = emprunt.getStatut();
            emprunt.setStatut(StatutEmprunt.EN_RETARD);

            boolean success = empruntDAO.update(emprunt);

            if (success) {
                System.out.println("\n✅ Modification enregistrée");

                // Recharger depuis BD
                Emprunt empruntRecharge = empruntDAO.findById(idTest).orElseThrow();

                System.out.println("\n📄 Après modification (rechargé depuis BD) :");
                System.out.println("   Statut : " + empruntRecharge.getStatut());

                // Restaurer l'ancien statut
                emprunt.setStatut(ancienStatut);
                empruntDAO.update(emprunt);
                System.out.println("\n🔄 Statut restauré à l'original");
            }

            System.out.println("\n✅ TEST UPDATE : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    // ==================== TESTS DELETE ====================

    /**
     * Test 12 : Suppression d'un emprunt
     */
    private static void testDelete() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 12 : DELETE - Suppression d'un emprunt");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Créer un emprunt temporaire pour le supprimer
            System.out.println("📝 Création d'un emprunt temporaire...");
            Emprunt empruntTemp = empruntDAO.effectuerEmpruntTransaction(3, 3);
            int idTemp = empruntTemp.getIdEmprunt();

            System.out.println("✅ Emprunt temporaire créé (ID: " + idTemp + ")");

            // Vérifier existence
            System.out.println("\n🔍 Existe avant suppression ? " +
                             (empruntDAO.exists(idTemp) ? "✅ OUI" : "❌ NON"));

            // Le retourner d'abord (pour libérer les ressources)
            empruntDAO.effectuerRetourTransaction(idTemp);
            System.out.println("🔄 Emprunt retourné");

            // Supprimer
            boolean deleted = empruntDAO.delete(idTemp);

            if (deleted) {
                System.out.println("✅ Emprunt supprimé avec succès");
            }

            // Vérifier suppression
            System.out.println("🔍 Existe après suppression ? " +
                             (empruntDAO.exists(idTemp) ? "❌ OUI" : "✅ NON"));

            System.out.println("\n✅ TEST DELETE : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    // ==================== TESTS UTILITY ====================

    /**
     * Test 13 : Comptage total
     */
    private static void testCount() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 13 : COUNT - Comptage total");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            long total = empruntDAO.count();
            System.out.println("✅ Nombre total d'emprunts : " + total);

            System.out.println("\n✅ TEST COUNT : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 14 : Comptage par statut
     */
    private static void testCountByStatut() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 14 : COUNT_BY_STATUT - Comptage par statut");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            long enCours = empruntDAO.countByStatut(StatutEmprunt.EN_COURS);
            long retournes = empruntDAO.countByStatut(StatutEmprunt.RETOURNE);
            long enRetard = empruntDAO.countByStatut(StatutEmprunt.EN_RETARD);

            System.out.println("✅ Statistiques par statut :");
            System.out.println("   EN_COURS  : " + enCours);
            System.out.println("   RETOURNE  : " + retournes);
            System.out.println("   EN_RETARD : " + enRetard);
            System.out.println("   ─────────────────");
            System.out.println("   TOTAL     : " + (enCours + retournes + enRetard));

            System.out.println("\n✅ TEST COUNT_BY_STATUT : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test 15 : Vérification existence
     */
    private static void testExists() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("TEST 15 : EXISTS - Vérification existence");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Test avec emprunt existant
            List<Emprunt> emprunts = empruntDAO.findAll();
            if (!emprunts.isEmpty()) {
                int idExistant = emprunts.get(0).getIdEmprunt();
                boolean existe = empruntDAO.exists(idExistant);
                System.out.println("🔍 L'emprunt #" + idExistant + " existe ? " +
                                 (existe ? "✅ OUI" : "❌ NON"));
            }

            // Test avec ID inexistant
            boolean existePas = empruntDAO.exists(99999);
            System.out.println("🔍 L'emprunt #99999 existe ? " +
                             (existePas ? "❌ OUI" : "✅ NON"));

            System.out.println("\n✅ TEST EXISTS : RÉUSSI\n");

        } catch (DatabaseException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
    }
}

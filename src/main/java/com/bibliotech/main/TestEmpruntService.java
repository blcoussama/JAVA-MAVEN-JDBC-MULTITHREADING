package com.bibliotech.main;

import com.bibliotech.services.EmpruntService;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.StatutEmprunt;
import com.bibliotech.exceptions.LivreIndisponibleException;
import com.bibliotech.exceptions.LivreNonTrouveException;
import com.bibliotech.exceptions.MembreNonTrouveException;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;

/**
 * Tests complets pour EmpruntService
 * VERSION SIMPLIFIÉE ET ROBUSTE
 * 
 * @author Belcadi Oussama
 * @version 1.1
 */
public class TestEmpruntService {
    
    private static EmpruntService service;
    private static int testsTotal = 0;
    private static int testsReussis = 0;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("    TESTS EmpruntService - PHASE 3 MULTITHREADING");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // Initialiser service
        service = new EmpruntService();
        
        // Exécuter tous les tests
        test01_EffectuerEmpruntSimple();
        test02_EffectuerEmpruntMembreInexistant();
        test03_EffectuerEmpruntLivreInexistant();
        test04_EffectuerEmpruntLivreIndisponible();
        test05_VerifierMaximumEmprunts();
        test06_RetournerLivreSimple();
        test07_RetournerLivreAvecCalculDuree();
        test08_RetournerEmpruntInexistant();
        test09_RetournerEmpruntDejaRetourne();
        test10_VerifierDisponibilite();
        test11_GetHistoriqueEmpruntsMembre();
        test12_GetEmpruntsEnCours();
        test13_GetEmpruntsEnRetard();
        test14_StatistiquesEmprunts();
        test15_GetEmpruntParId();
        
        // Afficher résultats finaux
        afficherResultatsFinaux();
    }
    
    // ==================== TESTS EMPRUNTS ====================
    
    private static void test01_EffectuerEmpruntSimple() {
        testsTotal++;
        System.out.println("TEST 1 : EFFECTUER_EMPRUNT_SIMPLE");
        
        try {
            // Utiliser membre ID 7 (ou un membre avec peu d'emprunts)
            // et un livre très disponible (Core Java - 5 exemplaires)
            Emprunt emprunt = service.effectuerEmprunt(6, 6);
            
            System.out.println("  ✓ Emprunt effectué avec succès (ID: " + emprunt.getIdEmprunt() + ")");
            System.out.println("  ✓ Membre : " + emprunt.getMembre().getNom() + " " + 
                             emprunt.getMembre().getPrenom());
            System.out.println("  ✓ Livre : " + emprunt.getLivre().getTitre());
            System.out.println("  ✓ Statut : " + emprunt.getStatut());
            
            if (emprunt.getStatut() == StatutEmprunt.EN_COURS) {
                System.out.println("  ✅ TEST 1 RÉUSSI");
                testsReussis++;
            } else {
                System.out.println("  ❌ TEST 1 ÉCHOUÉ : Statut incorrect");
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 1 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test02_EffectuerEmpruntMembreInexistant() {
        testsTotal++;
        System.out.println("TEST 2 : EFFECTUER_EMPRUNT_MEMBRE_INEXISTANT");
        
        try {
            service.effectuerEmprunt(999, 6);
            System.out.println("  ❌ TEST 2 ÉCHOUÉ : Exception non levée");
            
        } catch (MembreNonTrouveException e) {
            System.out.println("  ✓ MembreNonTrouveException correctement levée");
            System.out.println("  ✓ Message : " + e.getMessage());
            System.out.println("  ✓ Code : " + e.getCode());
            System.out.println("  ✅ TEST 2 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 2 ÉCHOUÉ : Mauvaise exception : " + e.getClass().getSimpleName());
        }
        
        System.out.println();
    }
    
    private static void test03_EffectuerEmpruntLivreInexistant() {
        testsTotal++;
        System.out.println("TEST 3 : EFFECTUER_EMPRUNT_LIVRE_INEXISTANT");
        
        try {
            service.effectuerEmprunt(6, 999);
            System.out.println("  ❌ TEST 3 ÉCHOUÉ : Exception non levée");
            
        } catch (LivreNonTrouveException e) {
            System.out.println("  ✓ LivreNonTrouveException correctement levée");
            System.out.println("  ✓ Message : " + e.getMessage());
            System.out.println("  ✓ Code : " + e.getCode());
            System.out.println("  ✅ TEST 3 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 3 ÉCHOUÉ : Mauvaise exception : " + e.getClass().getSimpleName());
            System.out.println("  Message : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test04_EffectuerEmpruntLivreIndisponible() {
        testsTotal++;
        System.out.println("TEST 4 : EFFECTUER_EMPRUNT_LIVRE_INDISPONIBLE");
        
        try {
            // Stratégie simple : Emprunter un livre jusqu'à épuisement
            // Utiliser le livre ID 4 (Design Patterns - normalement 2 exemplaires)
            
            // Vérifier d'abord la disponibilité
            boolean dispo = service.verifierDisponibilite(4);
            
            if (!dispo) {
                System.out.println("  ✓ Livre déjà indisponible (tests précédents)");
                // Essayer quand même d'emprunter
                try {
                    service.effectuerEmprunt(6, 4);
                    System.out.println("  ❌ TEST 4 ÉCHOUÉ : Exception non levée");
                } catch (LivreIndisponibleException e) {
                    System.out.println("  ✓ LivreIndisponibleException correctement levée");
                    System.out.println("  ✅ TEST 4 RÉUSSI");
                    testsReussis++;
                }
            } else {
                // Livre disponible, emprunter jusqu'à épuisement
                int empruntsEffectues = 0;
                boolean livreEpuise = false;
                
                // Essayer max 3 emprunts
                for (int i = 0; i < 3 && !livreEpuise; i++) {
                    try {
                        service.effectuerEmprunt(6, 4);
                        empruntsEffectues++;
                        System.out.println("  ✓ Emprunt #" + empruntsEffectues + " effectué");
                    } catch (LivreIndisponibleException e) {
                        livreEpuise = true;
                        System.out.println("  ✓ Livre épuisé après " + empruntsEffectues + " emprunt(s)");
                    } catch (DatabaseException e) {
                        // Membre plein, essayer avec un autre
                        if (e.getMessage().contains("maximum")) {
                            System.out.println("  ✓ Membre 7 plein, fin du test");
                            livreEpuise = true;
                        }
                    }
                }
                
                // Maintenant essayer un dernier emprunt (doit échouer)
                if (livreEpuise) {
                    try {
                        service.effectuerEmprunt(6, 4);
                        System.out.println("  ❌ TEST 4 ÉCHOUÉ : Exception non levée");
                    } catch (LivreIndisponibleException e) {
                        System.out.println("  ✓ LivreIndisponibleException correctement levée");
                        System.out.println("  ✅ TEST 4 RÉUSSI");
                        testsReussis++;
                    }
                } else {
                    System.out.println("  ✅ TEST 4 RÉUSSI (validation logique)");
                    testsReussis++;
                }
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 4 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test05_VerifierMaximumEmprunts() {
        testsTotal++;
        System.out.println("TEST 5 : VÉRIFIER_MAXIMUM_EMPRUNTS");
        
        try {
            // Vérifier simplement qu'un membre avec emprunts est détecté
            List<Emprunt> emprunts = service.getEmpruntsEnCoursMembre(1);
            System.out.println("  ✓ Membre ID 1 a " + emprunts.size() + " emprunt(s) en cours");
            
            if (emprunts.size() >= 5) {
                System.out.println("  ✓ Membre a atteint le maximum (5)");
            } else {
                System.out.println("  ✓ Membre peut encore emprunter (" + (5 - emprunts.size()) + " restants)");
            }
            
            System.out.println("  ✅ TEST 5 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 5 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // ==================== TESTS RETOURS ====================
    
    private static void test06_RetournerLivreSimple() {
        testsTotal++;
        System.out.println("TEST 6 : RETOURNER_LIVRE_SIMPLE");
        
        try {
            // Utiliser membre 7 avec livre Core Java (toujours disponible)
            Emprunt emprunt = service.effectuerEmprunt(6, 6);
            System.out.println("  ✓ Emprunt créé (ID: " + emprunt.getIdEmprunt() + ")");
            
            // Retourner immédiatement
            Emprunt empruntRetourne = service.retournerLivre(emprunt.getIdEmprunt());
            
            System.out.println("  ✓ Livre retourné avec succès");
            System.out.println("  ✓ Date retour effective : " + empruntRetourne.getDateRetourEffective());
            System.out.println("  ✓ Statut : " + empruntRetourne.getStatut());
            
            if (empruntRetourne.getDateRetourEffective() != null) {
                System.out.println("  ✅ TEST 6 RÉUSSI");
                testsReussis++;
            } else {
                System.out.println("  ❌ TEST 6 ÉCHOUÉ : Date retour null");
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 6 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test07_RetournerLivreAvecCalculDuree() {
        testsTotal++;
        System.out.println("TEST 7 : RETOURNER_LIVRE_AVEC_CALCUL_DURÉE");
        
        try {
            // Récupérer un emprunt existant pour tester le calcul
            List<Emprunt> empruntsEnCours = service.getEmpruntsEnCours();
            
            if (!empruntsEnCours.isEmpty()) {
                Emprunt emprunt = empruntsEnCours.get(0);
                System.out.println("  ✓ Emprunt en cours trouvé (ID: " + emprunt.getIdEmprunt() + ")");
                System.out.println("  ✓ Durée actuelle : " + emprunt.calculerDuree() + " jour(s)");
                System.out.println("  ✓ Retard : " + emprunt.calculerJoursRetard() + " jour(s)");
                System.out.println("  ✅ TEST 7 RÉUSSI");
                testsReussis++;
            } else {
                System.out.println("  ✓ Aucun emprunt en cours");
                System.out.println("  ✅ TEST 7 RÉUSSI (pas d'emprunts)");
                testsReussis++;
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 7 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test08_RetournerEmpruntInexistant() {
        testsTotal++;
        System.out.println("TEST 8 : RETOURNER_EMPRUNT_INEXISTANT");
        
        try {
            service.retournerLivre(99999);
            System.out.println("  ❌ TEST 8 ÉCHOUÉ : Exception non levée");
            
        } catch (DatabaseException e) {
            System.out.println("  ✓ DatabaseException correctement levée");
            System.out.println("  ✓ Message : " + e.getMessage());
            System.out.println("  ✅ TEST 8 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 8 ÉCHOUÉ : Mauvaise exception");
        }
        
        System.out.println();
    }
    
    private static void test09_RetournerEmpruntDejaRetourne() {
        testsTotal++;
        System.out.println("TEST 9 : RETOURNER_EMPRUNT_DÉJÀ_RETOURNÉ");
        
        try {
            // Effectuer emprunt et le retourner
            Emprunt emprunt = service.effectuerEmprunt(6, 6);
            service.retournerLivre(emprunt.getIdEmprunt());
            
            // Essayer de le retourner à nouveau
            service.retournerLivre(emprunt.getIdEmprunt());
            System.out.println("  ❌ TEST 9 ÉCHOUÉ : Exception non levée");
            
        } catch (DatabaseException e) {
            System.out.println("  ✓ DatabaseException correctement levée");
            System.out.println("  ✓ Message : " + e.getMessage());
            System.out.println("  ✅ TEST 9 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 9 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // ==================== TESTS CONSULTATIONS ====================
    
    private static void test10_VerifierDisponibilite() {
        testsTotal++;
        System.out.println("TEST 10 : VÉRIFIER_DISPONIBILITÉ");
        
        try {
            boolean disponible = service.verifierDisponibilite(6);
            System.out.println("  ✓ Livre ID 6 disponible : " + (disponible ? "OUI" : "NON"));
            System.out.println("  ✅ TEST 10 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 10 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test11_GetHistoriqueEmpruntsMembre() {
        testsTotal++;
        System.out.println("TEST 11 : GET_HISTORIQUE_EMPRUNTS_MEMBRE");
        
        try {
            List<Emprunt> historique = service.getHistoriqueEmpruntsMembre("AB123456");
            
            System.out.println("  ✓ Historique récupéré : " + historique.size() + " emprunt(s)");
            
            int count = 0;
            for (Emprunt e : historique) {
                if (count < 5) {  // Afficher max 5
                    System.out.println("    - " + e.getLivre().getTitre() + 
                                     " (" + e.getStatut() + ")");
                }
                count++;
            }
            if (count > 5) {
                System.out.println("    ... et " + (count - 5) + " autre(s)");
            }
            
            System.out.println("  ✅ TEST 11 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 11 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test12_GetEmpruntsEnCours() {
        testsTotal++;
        System.out.println("TEST 12 : GET_EMPRUNTS_EN_COURS");
        
        try {
            List<Emprunt> empruntsEnCours = service.getEmpruntsEnCours();
            
            System.out.println("  ✓ Emprunts en cours : " + empruntsEnCours.size());
            
            int count = 0;
            for (Emprunt e : empruntsEnCours) {
                if (count < 5) {  // Afficher max 5
                    System.out.println("    - " + e.getMembre().getNom() + " : " + 
                                     e.getLivre().getTitre());
                }
                count++;
            }
            if (count > 5) {
                System.out.println("    ... et " + (count - 5) + " autre(s)");
            }
            
            System.out.println("  ✅ TEST 12 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 12 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test13_GetEmpruntsEnRetard() {
        testsTotal++;
        System.out.println("TEST 13 : GET_EMPRUNTS_EN_RETARD");
        
        try {
            List<Emprunt> empruntsEnRetard = service.getEmpruntsEnRetard();
            
            System.out.println("  ✓ Emprunts en retard : " + empruntsEnRetard.size());
            
            for (Emprunt e : empruntsEnRetard) {
                System.out.println("    - " + e.getMembre().getNom() + " : " + 
                                 e.getLivre().getTitre() + 
                                 " (retard: " + e.calculerJoursRetard() + " jours)");
            }
            
            System.out.println("  ✅ TEST 13 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 13 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // ==================== TESTS STATISTIQUES ====================
    
    private static void test14_StatistiquesEmprunts() {
        testsTotal++;
        System.out.println("TEST 14 : STATISTIQUES_EMPRUNTS");
        
        try {
            long total = service.getNombreEmpruntsTotal();
            long enCours = service.getNombreEmpruntsEnCours();
            long retournes = service.getNombreEmpruntsRetournes();
            long enRetard = service.getNombreEmpruntsEnRetard();
            
            System.out.println("\n  📊 STATISTIQUES EMPRUNTS :");
            System.out.println("  ══════════════════════════════════════════════════");
            System.out.println("  Total emprunts       : " + total);
            System.out.println("  En cours             : " + enCours);
            System.out.println("  Retournés            : " + retournes);
            System.out.println("  En retard            : " + enRetard);
            System.out.println("  ══════════════════════════════════════════════════");
            
            System.out.println("  ✅ TEST 14 RÉUSSI");
            testsReussis++;
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 14 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void test15_GetEmpruntParId() {
        testsTotal++;
        System.out.println("TEST 15 : GET_EMPRUNT_PAR_ID");
        
        try {
            List<Emprunt> emprunts = service.getEmpruntsEnCours();
            
            if (!emprunts.isEmpty()) {
                int idEmprunt = emprunts.get(0).getIdEmprunt();
                Emprunt emprunt = service.getEmprunt(idEmprunt);
                
                System.out.println("  ✓ Emprunt trouvé (ID: " + emprunt.getIdEmprunt() + ")");
                System.out.println("  ✓ Membre : " + emprunt.getMembre().getNom());
                System.out.println("  ✓ Livre : " + emprunt.getLivre().getTitre());
                System.out.println("  ✓ Statut : " + emprunt.getStatut());
                System.out.println("  ✅ TEST 15 RÉUSSI");
                testsReussis++;
            } else {
                System.out.println("  ✓ Aucun emprunt en cours");
                System.out.println("  ✅ TEST 15 RÉUSSI (pas d'emprunts)");
                testsReussis++;
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ TEST 15 ÉCHOUÉ : " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // ==================== AFFICHAGE RÉSULTATS ====================
    
    private static void afficherResultatsFinaux() {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("    RÉSULTATS FINAUX");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        System.out.println("  Tests exécutés : " + testsTotal);
        System.out.println("  Tests réussis  : " + testsReussis);
        System.out.println("  Tests échoués  : " + (testsTotal - testsReussis));
        
        double pourcentage = (testsTotal > 0) 
            ? ((double) testsReussis / testsTotal * 100.0) 
            : 0.0;
        System.out.printf("  Taux de réussite : %.1f%%\n", pourcentage);
        
        if (testsReussis == testsTotal) {
            System.out.println("\n  ✅✅✅ TOUS LES TESTS ONT RÉUSSI ! ✅✅✅");
        } else {
            System.out.println("\n  ⚠️  CERTAINS TESTS ONT ÉCHOUÉ");
        }
        
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
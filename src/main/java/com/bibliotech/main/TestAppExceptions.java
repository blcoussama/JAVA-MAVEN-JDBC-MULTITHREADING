package com.bibliotech.main;

import com.bibliotech.model.*;
import com.bibliotech.service.BibliothequeService;
import com.bibliotech.exception.BibliothequeException;
import java.util.List;

public class TestAppExceptions {
    public static void main(String[] args) {
        BibliothequeService service = new BibliothequeService();

        System.out.println("=== 🛡️ TEST DES EXCEPTIONS ET VALIDATIONS ===");
        
        System.out.println("\n--- 👤 PARTIE AUTEUR ---");
        
        // --- TEST 1 : LIVRE SANS TITRE (Validation via Service) ---
        try {
            System.out.print("1. Test Sauvegarde livre sans titre : ");
            service.sauvegarderLivre(new Livre("", null, Categorie.INFORMATIQUE));
        } catch (BibliothequeException e) {
            // Utilisation de System.out pour garantir l'ordre chronologique
            System.out.println("❌ Erreur interceptée -> " + e.getMessage());
        }

        // --- TEST 2 : AUTEUR SANS NOM ---
        try {
            System.out.print("2. Test Sauvegarde auteur sans nom   : ");
            service.sauvegarderAuteur(new Auteur("", "Jean"));
        } catch (BibliothequeException e) {
            System.out.println("❌ Erreur interceptée -> " + e.getMessage());
        }

        // --- TEST 3 : RECHERCHE ID INEXISTANT ---
        try {
            System.out.print("3. Test Recherche Auteur ID 9999    : ");
            service.chercherAuteurParId(9999);
        } catch (BibliothequeException e) {
            System.out.println("❌ Erreur interceptée -> " + e.getMessage());
        }

        System.out.println("\n--- 📚 PARTIE LIVRE ---");

        try {
            // SCÉNARIO 1 : Chargement normal
            System.out.print("1. Chargement des données de la base : ");
            List<Livre> livres = service.chargerTousLesLivres();
            System.out.println("✅ Succès (" + livres.size() + " livres récupérés)");

            // SCÉNARIO 2 : Erreur Métier lors d'une tentative d'ajout
            System.out.print("2. Test validation titre vide        : ");
            // On récupère un auteur existant pour le test s'il y en a un
            Auteur auteurTest = (livres.isEmpty()) ? new Auteur("Test", "Test") : livres.get(0).getAuteur();
            Livre livreSansTitre = new Livre("", auteurTest, Categorie.INFORMATIQUE);
            
            service.sauvegarderLivre(livreSansTitre);

        } catch (BibliothequeException e) {
            System.out.println("❌ Erreur interceptée -> " + e.getMessage());
            
            // Affichage de la cause technique si elle existe (ex: SQLException)
            if (e.getCause() != null) {
                System.out.println("   🔍 [Détail Technique] : " + e.getCause().getMessage());
            }
        }

        System.out.println("\n=== 🎯 FIN DES TESTS DE SÉCURITÉ ===");
    }
}
package com.bibliotech.main;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.model.Livre;
import com.bibliotech.model.Categorie;
import com.bibliotech.service.BibliothequeService;
import java.util.*;

public class TestAppCollections {
    public static void main(String[] args) throws BibliothequeException {
        
        BibliothequeService service = new BibliothequeService();

        // --- SOURCE DE DONNÉES ---
        // On récupère une LIST car c'est l'ordre de la base de données qui compte.
        List<Livre> tousLesLivres = service.chargerTousLesLivres();

        
        System.out.println("=== 📊 PHASE 1 : LA LISTE (ArrayList) ===");
        // Pourquoi ? Pour le stockage brut et le filtrage manuel.
        List<Livre> infoLivres = service.filtrerParCategorie(tousLesLivres, Categorie.INFORMATIQUE);
        System.out.println("Livres trouvés en INFORMATIQUE : " + infoLivres.size());
        for(Livre l : infoLivres) {
            System.out.println("   - " + l.getTitre());
        }

        
        System.out.println("\n=== 📊 PHASE 2 : LE SET (HashSet) ===");
        // Pourquoi ? Pour l'unicité. On veut savoir quels genres existent sans répétition.
        Set<Categorie> genres = service.obtenirCategoriesUniques(tousLesLivres);
        System.out.println("Genres distincts en magasin : " + genres);
        // Test : Si tu ajoutes 10 fois le même genre dans un Set, sa taille reste à 1.

        
        System.out.println("\n=== 📊 PHASE 3 : LA MAP (HashMap) ===");
        // Pourquoi ? Pour la performance. On veut trouver un livre par son titre SANS boucler.
        Map<String, Livre> index = service.indexerLivresParTitre(tousLesLivres);
        
        String recherche = "Le Guide du Java";
        if (index.containsKey(recherche)) {
            Livre l = index.get(recherche); // Accès direct !
            System.out.println("Résultat de recherche directe : " + l);
        } else {
            System.out.println("Livre non trouvé.");
        }
                
        
        System.out.println("\n=== 📊 PHASE 4 : LE TRI (Comparator) ===");
        System.out.println("Avant le tri :");
        for(Livre l : tousLesLivres) System.out.println(" - " + l.getTitre());

        service.trierLivresParTitre(tousLesLivres);

        System.out.println("\nAprès le tri alphabétique par Titre :");
        for(Livre l : tousLesLivres) System.out.println(" - " + l.getTitre());
        
        service.trierLivresParId(tousLesLivres);
        
        System.out.println("\nAprès le trie Numerique Par ID du Plus Recent au plus ancien :");
        for(Livre l : tousLesLivres) System.out.println(" - " + l.getTitre());
    }
}
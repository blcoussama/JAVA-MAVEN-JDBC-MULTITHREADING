package com.bibliotech.main;

import com.bibliotech.model.Livre;
import com.bibliotech.model.Categorie;
import com.bibliotech.service.BibliothequeService;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.bibliotech.exception.BibliothequeException;

public class TestAppStreams {
    public static void main(String[] args) throws BibliothequeException {
        
        BibliothequeService service = new BibliothequeService();

        // 1. CHARGEMENT DES DONNÉES
        List<Livre> maBibliothequeOriginale = service.chargerTousLesLivres();
        System.out.println("📚 Total livres chargés : " + maBibliothequeOriginale.size());

        // ---------------------------------------------------------------
        // TEST 1 : LE FILTRE (STREAM)
        // ---------------------------------------------------------------
        System.out.println("\n--- 1. TEST FILTRE STREAM (Informatique) ---");
        List<Livre> livresInfo = service.filtrerParCategorieStream(maBibliothequeOriginale, Categorie.INFORMATIQUE);
        
        livresInfo.forEach(l -> System.out.println("   💻 " + l.getTitre()));
        
        // PREUVE QUE L'ORIGINAL N'A PAS BOUGÉ :
        System.out.println("   (Taille liste originale reste : " + maBibliothequeOriginale.size() + ")");

	     // ---------------------------------------------------------------
	     // TEST 2 : LE SET (STREAM)
	     // ---------------------------------------------------------------
	     System.out.println("\n--- 4. TEST SET STREAM (Catégories Uniques) ---");
	     Set<Categorie> categories = service.obtenirCategoriesUniquesStream(maBibliothequeOriginale);
	
	     // Un Set n'est pas ordonné, donc l'affichage peut varier
	     System.out.println("Genres distincts trouvés : " + categories); 
	     // Si tu as 10 livres INFO et 2 HISTOIRE, tu ne verras que [INFORMATIQUE, HISTOIRE]

	     // ---------------------------------------------------------------
	     // TEST 3.1: L'INDEXATION (MAP)
	     // ---------------------------------------------------------------
	     System.out.println("\n--- 3.1 TEST INDEXATION STREAM (Recherche Rapide) ---");
	     Map<String, Livre> index = service.indexerLivresParTitreStream(maBibliothequeOriginale);
	     String recherche = "Le Guide du Java";
	     if (index.containsKey(recherche)) {
	         System.out.println("   🔍 Trouvé via Stream Map : " + index.get(recherche));
	     }
	     
        // ---------------------------------------------------------------
        // TEST 3.2: LA TRANSFORMATION (MAP)
        // ---------------------------------------------------------------
        System.out.println("\n--- 3.2 TEST MAP (Extraction des titres seuls) ---");
        // Ici, on ne récupère plus des objets "Livre", mais des objets "String" !
        List<String> titresSeuls = service.obtenirSeulementLesTitres(maBibliothequeOriginale);
        
        titresSeuls.forEach(t -> System.out.println("   🏷️ Titre : " + t));

        // ---------------------------------------------------------------
        // TEST 4 : LE TRI (STREAM) vs ANCIENNE MÉTHODE
        // ---------------------------------------------------------------
     
        // TRIE PAR TITRE
        System.out.println("\n--- 2. TEST TRI STREAM (Par Titre) ---");
        List<Livre> livresTries = service.trierLivresParTitreStream(maBibliothequeOriginale);
        
        System.out.println("▶️ Liste issue du Stream (Triée) :");
        livresTries.forEach(l -> System.out.println("   A-Z : " + l.getTitre()));

        System.out.println("▶️ Liste Originale (Est-elle restée dans le désordre ?) :");
        // Si tu regardes bien, celle-ci est restée dans l'ordre des IDs !
        System.out.println("   Premier livre original : " + maBibliothequeOriginale.get(0).getTitre());
        
        // TRIE PAR ID A L'ENVERS
        System.out.println("\n--- 2. TEST TRI STREAM (Par Numero ID) ---");
        List<Livre> livresTriesParId = service.trierLivresParNumeroIDStream(maBibliothequeOriginale);
        
        System.out.println("▶️ Liste issue du Stream (Triée) :");
        livresTriesParId.forEach(l -> System.out.println("   Du Plus Recent au Plus Ancien : " + l.getTitre()));

        System.out.println("▶️ Liste Originale (Est-elle restée dans le désordre ?) :");
        // Si tu regardes bien, celle-ci est restée dans l'ordre des IDs !
        System.out.println("   Premier livre original : " + maBibliothequeOriginale.get(0).getTitre());
        
        // ---------------------------------------------------------------
        // TEST BONUS : LA PUISSANCE DU "CHAINING" (Tout en un)
        // ---------------------------------------------------------------
        System.out.println("\n--- 🚀 BONUS : TOUT EN UNE LIGNE ---");
        System.out.println("Je veux : Titres des livres d'Histoire, triés A-Z");
        
        maBibliothequeOriginale.stream()
            .filter(l -> l.getCategorie() == Categorie.HISTOIRE)  // 1. On filtre
            .sorted((l1, l2) -> l1.getTitre().compareToIgnoreCase(l2.getTitre())) // 2. On trie
            .map(Livre::getTitre)                                 // 3. On transforme en String
            .forEach(titre -> System.out.println("   📜 " + titre)); // 4. On affiche
        
     // VERSION PLUS COURTE ET SIMPLIFIE !
	 // maBibliothequeOriginale.stream()
     //  	.filter(l -> l.getCategorie() == Categorie.HISTOIRE)
     //  	.sorted(Comparator.comparing(Livre::getTitre)) // Plus court !
     //  	.map(Livre::getTitre)
     //  	.forEach(System.out::println); // Encore plus court !
    }
}
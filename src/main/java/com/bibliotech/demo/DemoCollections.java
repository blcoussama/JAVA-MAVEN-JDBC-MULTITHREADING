package com.bibliotech.demo;

import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Collections « classiques » : List, Set, Map et tri avec Comparator. */
public class DemoCollections {

    public static void main(String[] args) {
        BibliothequeService service = DemoSupport.service();
        // List : l'ordre de lecture en base (par identifiant) est conservé.
        List<Livre> tousLesLivres = new ArrayList<>(DemoSupport.chargerLivres(service));

        System.out.println("=== 1. List (ArrayList) : filtrage manuel ===");
        List<Livre> livresInfo = service.filtrerParCategorie(tousLesLivres, Categorie.INFORMATIQUE);
        System.out.println("Livres en INFORMATIQUE : " + livresInfo.size());
        livresInfo.forEach(l -> System.out.println("  - " + l.getTitre()));

        System.out.println("\n=== 2. Set (HashSet) : catégories distinctes ===");
        Set<Categorie> genres = service.obtenirCategoriesUniques(tousLesLivres);
        System.out.println("Catégories présentes (sans doublon, ordre non garanti) : " + genres);

        System.out.println("\n=== 3. Map : recherche directe par titre ===");
        Map<String, Livre> index = service.indexerLivresParTitre(tousLesLivres);
        String recherche = "Le Guide du Java";
        Livre trouve = index.get(recherche);
        System.out.println(trouve != null ? "Trouvé : " + trouve : "Livre '" + recherche + "' non trouvé.");

        System.out.println("\n=== 4. Tri avec Comparator ===");
        System.out.println("Avant le tri :");
        tousLesLivres.forEach(l -> System.out.println("  - " + l.getTitre()));

        service.trierLivresParTitre(tousLesLivres);
        System.out.println("\nAprès le tri alphabétique par titre :");
        tousLesLivres.forEach(l -> System.out.println("  - " + l.getTitre()));

        service.trierLivresParId(tousLesLivres);
        System.out.println("\nAprès le tri par ID décroissant (du plus récent au plus ancien) :");
        tousLesLivres.forEach(l -> System.out.println("  - [" + l.getId() + "] " + l.getTitre()));
    }
}

package com.bibliotech.demo;

import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** API Stream : filter, map, collect (Set, Map), sorted et chaînage ; la liste source reste intacte. */
public class DemoStreams {

    public static void main(String[] args) {
        BibliothequeService service = DemoSupport.service();
        List<Livre> livres = DemoSupport.chargerLivres(service);
        System.out.println("Total livres chargés : " + livres.size());
        if (livres.isEmpty()) {
            System.out.println("La base ne contient aucun livre : chargez sql/seed.sql (voir README).");
            return;
        }

        System.out.println("\n--- 1. filter : livres d'informatique ---");
        service.filtrerParCategorieStream(livres, Categorie.INFORMATIQUE)
                .forEach(l -> System.out.println("  " + l.getTitre()));
        System.out.println("  (la liste d'origine contient toujours " + livres.size() + " livres)");

        System.out.println("\n--- 2. map + toSet : catégories distinctes ---");
        Set<Categorie> categories = service.obtenirCategoriesUniquesStream(livres);
        System.out.println("  " + categories);

        System.out.println("\n--- 3. toMap : index par titre (doublons gérés par une fonction de fusion) ---");
        Map<String, Livre> index = service.indexerLivresParTitreStream(livres);
        String recherche = "Le Guide du Java";
        System.out.println("  " + (index.containsKey(recherche) ? "Trouvé : " + index.get(recherche)
                : "'" + recherche + "' absent"));

        System.out.println("\n--- 4. map : extraction des titres ---");
        List<String> titres = service.obtenirSeulementLesTitres(livres);
        System.out.println("  " + titres);

        System.out.println("\n--- 5. sorted : tri par titre puis par ID décroissant ---");
        service.trierLivresParTitreStream(livres).forEach(l -> System.out.println("  A-Z : " + l.getTitre()));
        service.trierLivresParNumeroIDStream(livres)
                .forEach(l -> System.out.println("  ID décroissant : [" + l.getId() + "] " + l.getTitre()));
        System.out.println("  Premier livre de la liste d'origine (inchangée) : " + livres.get(0).getTitre());

        System.out.println("\n--- 6. Chaînage : titres des livres d'histoire, triés A-Z ---");
        List<String> histoire = service.titresTriesDeCategorie(livres, Categorie.HISTOIRE);
        System.out.println(histoire.isEmpty() ? "  (aucun livre d'histoire)" : "  " + histoire);
    }
}

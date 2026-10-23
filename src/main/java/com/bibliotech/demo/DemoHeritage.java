package com.bibliotech.demo;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.model.LivreNumerique;
import com.bibliotech.service.BibliothequeService;
import java.util.List;

/** Héritage et polymorphisme : un LivreNumerique est manipulé comme un Livre. */
public class DemoHeritage {

    public static void main(String[] args) {
        BibliothequeService service = DemoSupport.service();
        List<Auteur> auteurs;
        try {
            auteurs = service.chargerTousLesAuteurs();
        } catch (BibliothequeException e) {
            DemoSupport.arreter(e.getMessage());
            return;
        }
        if (auteurs.isEmpty()) {
            DemoSupport.arreter("Aucun auteur en base : chargez sql/seed.sql (voir README).");
        }

        // Une variable de type Livre peut référencer un LivreNumerique (sous-classe).
        Livre ebook = new LivreNumerique("Java dans le cloud", auteurs.get(0), Categorie.INFORMATIQUE, 15.5);
        // toString() appelé est celui de LivreNumerique : liaison dynamique.
        System.out.println("Objet : " + ebook);

        try {
            service.sauvegarderLivre(ebook);
            System.out.println("Livre numérique enregistré (ID " + ebook.getId() + ") : la méthode attend un Livre.");
        } catch (BibliothequeException e) {
            System.out.println("Résultat : " + e.getMessage());
        }
    }
}

package com.bibliotech.demo;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;
import java.util.List;

/** Exception métier : validations du service et encapsulation des erreurs techniques. */
public class DemoExceptions {

    public static void main(String[] args) {
        BibliothequeService service = DemoSupport.service();
        System.out.println("=== Exceptions et validations ===");

        essayer("1. Livre sans titre", () ->
                service.sauvegarderLivre(new Livre("", null, Categorie.INFORMATIQUE)));
        essayer("2. Auteur sans nom", () ->
                service.sauvegarderAuteur(new Auteur("", "Jean")));
        essayer("3. Recherche de l'auteur ID 9999", () ->
                service.chercherAuteurParId(9999));
        essayer("4. Livre rattaché à un auteur inexistant", () ->
                service.sauvegarderLivre(new Livre("Livre orphelin", new Auteur(9999, "Inconnu", "Auteur"),
                        Categorie.ROMAN)));

        List<Livre> livres = DemoSupport.chargerLivres(service);
        if (!livres.isEmpty()) {
            Livre existant = livres.get(0);
            essayer("5. Titre déjà présent ('" + existant.getTitre() + "')", () ->
                    service.sauvegarderLivre(new Livre(existant.getTitre(), existant.getAuteur(),
                            existant.getCategorie())));
        }
        System.out.println("\n=== Fin des tests de validation ===");
    }

    private interface Action {
        void executer() throws BibliothequeException;
    }

    private static void essayer(String libelle, Action action) {
        try {
            action.executer();
            System.out.println(libelle + " : accepté");
        } catch (BibliothequeException e) {
            System.out.println(libelle + " : refusé -> " + e.getMessage()
                    + (e.getCause() != null ? "\n     cause technique : " + e.getCause().getMessage() : ""));
        }
    }
}

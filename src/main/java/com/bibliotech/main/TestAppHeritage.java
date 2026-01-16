package com.bibliotech.main;

import com.bibliotech.model.*;
import com.bibliotech.service.BibliothequeService;

public class TestAppHeritage {
    public static void main(String[] args) {
        BibliothequeService service = new BibliothequeService();
        Auteur auteur = new Auteur(1, "Belcadi", "Oussama");

        // 1. Création d'un livre numérique (Héritage)
        // Note : On peut stocker un LivreNumerique dans une variable de type Livre !
        Livre livreEbook = new LivreNumerique("Java Cloud Edition", auteur, Categorie.INFORMATIQUE, 15.5);

        System.out.println("🧐 Test de l'objet : " + livreEbook.toString());

        try {
            System.out.println("🚀 Tentative de sauvegarde d'un livre hérité...");
            service.sauvegarderLivre(livreEbook); 
            // Ça marche car LivreNumerique EST UN Livre !
        } catch (Exception e) {
            System.out.println("ℹ️ Résultat : " + e.getMessage());
        }
    }
}
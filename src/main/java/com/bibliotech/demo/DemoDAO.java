package com.bibliotech.demo;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.DatabaseConnection;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import java.sql.SQLException;
import java.util.List;

/**
 * Utilisation directe des DAO : insertion d'un auteur et de ses livres, puis lecture avec jointure.
 * Les livres déjà présents (titre unique) sont ignorés : la démonstration peut être relancée.
 */
public class DemoDAO {

    public static void main(String[] args) {
        DatabaseConnection connexion = DemoSupport.connexion();
        AuteurDAO auteurDAO = new AuteurDAO(connexion);
        LivreDAO livreDAO = new LivreDAO(connexion);

        try {
            System.out.println("=== DAO : insertion ===");
            Auteur auteur = new Auteur("Bernard", "Sophie");
            int idAuteur = auteurDAO.ajouterAuteur(auteur);
            System.out.println("Auteur ajouté : " + auteur + " (ID " + idAuteur + ")");

            List<Livre> aAjouter = List.of(
                    new Livre("Java pour débuter", auteur, Categorie.INFORMATIQUE),
                    new Livre("Apprendre le SQL", auteur, Categorie.INFORMATIQUE),
                    new Livre("Histoire de l'informatique", auteur, Categorie.HISTOIRE));
            for (Livre livre : aAjouter) {
                if (livreDAO.existeTitre(livre.getTitre())) {
                    System.out.println("Déjà présent, ignoré : " + livre.getTitre());
                } else {
                    livreDAO.ajouterLivre(livre);
                    System.out.println("Livre ajouté : " + livre.getTitre() + " (ID " + livre.getId() + ")");
                }
            }

            System.out.println("\n=== DAO : lecture (jointure livre/auteur) ===");
            for (Livre l : livreDAO.listerTousLesLivres()) {
                System.out.println("Livre ID " + l.getId() + " | " + l.getTitre() + " | " + l.getAuteur());
            }
            System.out.println("\nAuteurs enregistrés : " + auteurDAO.listerTousLesAuteurs().size());
            System.out.println("Recherche de l'auteur ID " + idAuteur + " : "
                    + auteurDAO.trouverAuteurParId(idAuteur).map(Auteur::toString).orElse("introuvable"));
            System.out.println("Recherche de l'auteur ID 9999 : "
                    + auteurDAO.trouverAuteurParId(9999).map(Auteur::toString).orElse("introuvable"));
        } catch (SQLException e) {
            DemoSupport.arreter("Erreur SQL : " + e.getMessage());
        }
    }
}

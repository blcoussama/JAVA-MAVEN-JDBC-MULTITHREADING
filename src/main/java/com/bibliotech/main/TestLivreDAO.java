package com.bibliotech.main;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Livre;
import com.bibliotech.model.Categorie;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class TestLivreDAO {
    public static void main(String[] args) throws SQLException {
        
        LivreDAO livDAO = new LivreDAO();
        AuteurDAO autDAO = new AuteurDAO();

        System.out.println("🚀 Test d'ajout d'un livre avec un auteur spécifique...");

        // ÉTAPE 1 : On choisit l'ID de l'auteur DEPUIS MYSQL
        int idAuteurChoisi = 1; 

        // ÉTAPE 2 : On récupère cet auteur depuis la base
        Auteur auteurSource = autDAO.trouverAuteurParId(idAuteurChoisi);

        // ÉTAPE 3 : On vérifie s'il existe avant de créer le livre
        if (auteurSource != null) {
            System.out.println("✅ Auteur trouvé : " + auteurSource.getNom());

         // --- ÉTAPE 1 : Créer une liste de livres à ajouter ---
            List<Livre> livresAAjouter = new ArrayList<>();
            livresAAjouter.add(new Livre("Java pour les nuls", auteurSource, Categorie.INFORMATIQUE));
            livresAAjouter.add(new Livre("Apprendre le SQL", auteurSource, Categorie.INFORMATIQUE));
            livresAAjouter.add(new Livre("L'Histoire du Code", auteurSource, Categorie.HISTOIRE));
            livresAAjouter.add(new Livre("Design Patterns", auteurSource, Categorie.INFORMATIQUE));
            livresAAjouter.add(new Livre("JUMANJI", auteurSource, Categorie.FANTASY));
            livresAAjouter.add(new Livre("Clean Code", auteurSource, Categorie.INFORMATIQUE));

            // --- ÉTAPE 2 : Boucle pour ajouter chaque livre ---
            System.out.println("🚀 Début de l'ajout massif...");
            for (Livre l : livresAAjouter) {
                livDAO.ajouterLivre(l);
            }
            System.out.println("✨ Tous les livres ont été traités.");

        } else {
            System.err.println("❌ Erreur : L'auteur n'existe pas.");
        }
        
        // ÉTAPE 4 : On affiche tous les livres pour voir si la List fonctionne
        System.out.println("\n--- Liste actuelle des livres ---");
        List<Livre> Bibliotheque = livDAO.listerTousLesLivres();
        
        for (Livre l : Bibliotheque) {
            System.out.println("Livre ID: " + l.getId() + " | Titre: " + l.getTitre() + " | Auteur: " + l.getAuteur().getNom());
        }
    }
}
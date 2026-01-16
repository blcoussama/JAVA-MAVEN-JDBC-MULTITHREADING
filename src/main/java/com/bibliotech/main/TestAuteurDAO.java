package com.bibliotech.main;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.DatabaseConnection;
import com.bibliotech.model.Auteur;
import java.util.List;
import java.sql.*;

public class TestAuteurDAO {
    public static void main(String[] args) throws SQLException {
        
        System.out.println("=== TEST DU MODULE AUTEUR ===");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Connexion impossible. Arrêt.");
            return;
        }

        AuteurDAO auteurDAO = new AuteurDAO();

     // 1. On crée des objets (POO)
        Auteur a1 = new Auteur("Oussama", "Belcadi");
//        Auteur a2 = new Auteur("Maupassant", "Guy");

        // 2. On les envoie en base via le DAO
        auteurDAO.ajouterAuteur(a1);
//        auteurDAO.ajouterAuteur(a2);

        // 3. ON APPREND LES COLLECTIONS ICI :
        List<Auteur> maListe = auteurDAO.listerTousLesAuteurs();

        System.out.println("J'ai récupéré " + maListe.size() + " auteurs.");

        
        for (Auteur auteur : maListe) {
            System.out.println("Auteur en mémoire : " + auteur.getNom());
        }

        System.out.println("\n--- FIN DES TESTS ---");
    }
}
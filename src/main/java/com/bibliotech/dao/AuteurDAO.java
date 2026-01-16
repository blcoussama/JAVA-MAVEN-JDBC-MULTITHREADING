package com.bibliotech.dao;

import com.bibliotech.model.Auteur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;

public class AuteurDAO {

	// AJOUTER UN AUTEUR
	// On ajoute "throws SQLException" : 
	// On prévient que cette méthode peut échouer techniquement
    public void ajouterAuteur(Auteur auteur) throws SQLException {
        String sql = "INSERT INTO auteur (nom, prenom) VALUES (?, ?)";

        // try-with-resources : ouvre et ferme la connexion automatiquement
        // Le try-with-resources reste là pour la FERMETURE automatique
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // On remplace les "?" par les vraies valeurs de l'objet auteur
            pstmt.setString(1, auteur.getNom());
            pstmt.setString(2, auteur.getPrenom());

            // On exécute la requête
            pstmt.executeUpdate();
            System.out.println("✅ Auteur ajouté avec succès : " + auteur.getNom());

        }
        
        // PLUS DE CATCH ICI ! 
        // On laisse l'erreur remonter au Service.
    }
    
    
    // RECUPERER TOUS LES AUTEURS DANS LA BASE DE DONNEES
    // On ajoute "throws SQLException"
    public List<Auteur> listerTousLesAuteurs() throws SQLException {
        List<Auteur> auteurs = new ArrayList<>();
        String sql = "SELECT id, nom, prenom FROM auteur";

        // On utilise Statement car la requête est fixe
        // Le try-with-resources reste pour assurer la fermeture (conn, stmt, rs)
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Le curseur descend tant qu'il y a des lignes
            while (rs.next()) {
                // On utilise le constructeur VERSION 1 (avec l'ID récupéré)
                Auteur a = new Auteur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom")
                );
                auteurs.add(a);
            }
        } 
        
        // PLUS DE CATCH : Si la base est injoignable, on ne renvoie pas une liste vide.
        // L'exception est lancée et interrompt la méthode.
        
        return auteurs;
    }
    
    
    // RECUPERER UN AUTEUR PAR SON ID
    // On propage l'exception technique
    public Auteur trouverAuteurParId(int id) throws SQLException {
        String sql = "SELECT id, nom, prenom FROM auteur WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // On utilise 'if' car un ID est unique
                    return new Auteur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom")
                    );
                }
            }
        }
        
        // PLUS DE CATCH : Si la base crash, on lance l'exception.
        
        // ON GARDE LE NULL : Mais uniquement pour dire "L'ID n'existe pas"
        
        return null; // On retourne null si aucun auteur n'a cet ID
    }
    
}
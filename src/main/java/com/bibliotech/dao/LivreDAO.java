package com.bibliotech.dao;

import com.bibliotech.model.Livre;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    // AJOUTER UN LIVRE
	// On ajoute throws SQLException
    public void ajouterLivre(Livre livre) throws SQLException {
        String sql = "INSERT INTO livre (titre, categorie, id_auteur) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livre.getTitre());
            // .name() transforme l'Enum en String pour MySQL
            pstmt.setString(2, livre.getCategorie().name()); 
            // On récupère l'ID de l'objet Auteur contenu dans le Livre
            pstmt.setInt(3, livre.getAuteur().getId());

            pstmt.executeUpdate();
            System.out.println("✅ Livre ajouté : " + livre.getTitre());

        } 
        
        // PLUS DE CATCH : Si l'ID de l'auteur n'existe pas ou si MySQL crash, 
        // le Service doit le savoir pour annuler l'opération.
    }

    
    // LISTER TOUS LES LIVRES (C'est là qu'on va utiliser les Lists !)
    // On ajoute throws SQLException
    public List<Livre> listerTousLesLivres() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        // On fait une JOINTURE pour récupérer les infos de l'auteur en même temps
        String sql = "SELECT l.id, l.titre, l.categorie, a.id as auteur_id, a.nom, a.prenom " +
                     "FROM livre l " +
                     "INNER JOIN auteur a ON l.id_auteur = a.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // 1. On recrée d'abord l'objet Auteur
                Auteur aut = new Auteur(
                    rs.getInt("auteur_id"),
                    rs.getString("nom"),
                    rs.getString("prenom")
                );

                // 2. On crée le Livre en lui passant l'objet Auteur
                Livre liv = new Livre(
                    rs.getInt("id"),
                    rs.getNString("titre"),
                    aut,
                    Categorie.valueOf(rs.getString("categorie"))
                    
                );
                
                // 3. On ajoute à notre Collection List
                livres.add(liv);
            }
        } 
        
        // PLUS DE CATCH : Si la jointure échoue ou si la table est verrouillée,
        // on ne renvoie pas une liste vide, on lance l'alerte.
        
        return livres;
    }
}
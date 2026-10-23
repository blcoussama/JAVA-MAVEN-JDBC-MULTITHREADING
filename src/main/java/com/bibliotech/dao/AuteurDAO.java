package com.bibliotech.dao;

import com.bibliotech.model.Auteur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Accès à la table {@code auteur}.
 *
 * <p>Les erreurs techniques ({@link SQLException}) sont propagées : c'est la couche service qui
 * décide comment les présenter à l'utilisateur. Toutes les requêtes paramétrées passent par
 * {@link PreparedStatement} (aucune concaténation de valeurs dans le SQL).
 */
public class AuteurDAO {

    private final ConnectionFactory connexions;

    public AuteurDAO(ConnectionFactory connexions) {
        this.connexions = Objects.requireNonNull(connexions, "connexions");
    }

    /**
     * Insère un auteur et renseigne l'identifiant généré par MySQL dans l'objet.
     *
     * @return l'identifiant généré
     */
    public int ajouterAuteur(Auteur auteur) throws SQLException {
        String sql = "INSERT INTO auteur (nom, prenom) VALUES (?, ?)";
        try (Connection conn = connexions.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, auteur.getNom());
            pstmt.setString(2, auteur.getPrenom());
            pstmt.executeUpdate();
            try (ResultSet cles = pstmt.getGeneratedKeys()) {
                if (!cles.next()) {
                    throw new SQLException("MySQL n'a renvoyé aucun identifiant pour l'auteur inséré.");
                }
                int id = cles.getInt(1);
                auteur.setId(id);
                return id;
            }
        }
    }

    /** Liste tous les auteurs, triés par identifiant. */
    public List<Auteur> listerTousLesAuteurs() throws SQLException {
        String sql = "SELECT id, nom, prenom FROM auteur ORDER BY id";
        List<Auteur> auteurs = new ArrayList<>();
        // Requête sans paramètre : un simple Statement suffit.
        try (Connection conn = connexions.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                auteurs.add(new Auteur(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom")));
            }
        }
        return auteurs;
    }

    /** Recherche un auteur par identifiant ; {@link Optional#empty()} s'il n'existe pas. */
    public Optional<Auteur> trouverAuteurParId(int id) throws SQLException {
        String sql = "SELECT id, nom, prenom FROM auteur WHERE id = ?";
        try (Connection conn = connexions.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Auteur(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom")));
                }
            }
        }
        return Optional.empty();
    }
}

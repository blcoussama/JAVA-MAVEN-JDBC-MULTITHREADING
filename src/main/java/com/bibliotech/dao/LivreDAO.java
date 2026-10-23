package com.bibliotech.dao;

import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Accès à la table {@code livre}.
 *
 * <p>Le titre est protégé par une contrainte {@code UNIQUE} (voir {@code sql/schema.sql}) :
 * une insertion en double lève une {@link java.sql.SQLIntegrityConstraintViolationException}
 * (code MySQL 1062), même si plusieurs threads ou plusieurs programmes insèrent en même temps.
 */
public class LivreDAO {

    private final ConnectionFactory connexions;

    public LivreDAO(ConnectionFactory connexions) {
        this.connexions = Objects.requireNonNull(connexions, "connexions");
    }

    /**
     * Insère un livre et renseigne l'identifiant généré dans l'objet.
     *
     * @return l'identifiant généré
     * @throws java.sql.SQLIntegrityConstraintViolationException si le titre existe déjà (1062)
     *         ou si l'auteur n'existe pas (clé étrangère, 1452)
     */
    public int ajouterLivre(Livre livre) throws SQLException {
        String sql = "INSERT INTO livre (titre, categorie, id_auteur) VALUES (?, ?, ?)";
        try (Connection conn = connexions.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, livre.getTitre());
            // name() convertit l'énumération Java en valeur de l'ENUM MySQL.
            pstmt.setString(2, livre.getCategorie().name());
            pstmt.setInt(3, livre.getAuteur().getId());
            pstmt.executeUpdate();
            try (ResultSet cles = pstmt.getGeneratedKeys()) {
                if (!cles.next()) {
                    throw new SQLException("MySQL n'a renvoyé aucun identifiant pour le livre inséré.");
                }
                int id = cles.getInt(1);
                livre.setId(id);
                return id;
            }
        }
    }

    /**
     * Indique si un livre porte déjà ce titre (comparaison selon la collation de la colonne,
     * insensible à la casse et aux accents, comme la contrainte UNIQUE).
     */
    public boolean existeTitre(String titre) throws SQLException {
        String sql = "SELECT 1 FROM livre WHERE titre = ? LIMIT 1";
        try (Connection conn = connexions.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titre);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Liste tous les livres avec leur auteur (jointure), triés par identifiant. */
    public List<Livre> listerTousLesLivres() throws SQLException {
        String sql = "SELECT l.id, l.titre, l.categorie, a.id AS auteur_id, a.nom, a.prenom "
                + "FROM livre l "
                + "INNER JOIN auteur a ON l.id_auteur = a.id "
                + "ORDER BY l.id";
        List<Livre> livres = new ArrayList<>();
        try (Connection conn = connexions.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Auteur auteur = new Auteur(rs.getInt("auteur_id"), rs.getString("nom"), rs.getString("prenom"));
                livres.add(new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        auteur,
                        Categorie.valueOf(rs.getString("categorie"))));
            }
        }
        return livres;
    }

    /** Nombre total de livres. */
    public int compterLivres() throws SQLException {
        try (Connection conn = connexions.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM livre")) {
            rs.next();
            return rs.getInt(1);
        }
    }
}

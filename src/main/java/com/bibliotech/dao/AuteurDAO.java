package com.bibliotech.dao;

import com.bibliotech.models.Auteur;
import com.bibliotech.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object pour la table Auteur
 * 
 * Fournit toutes les opérations CRUD (Create, Read, Update, Delete)
 * pour gérer les auteurs dans la base de données.
 * 
 * Utilise PreparedStatement pour prévenir les injections SQL.
 * 
 * @author Belcadi Oussama
 * @version 1.0
 */
public class AuteurDAO {
    
    // ==================== CREATE ====================
    
    /**
     * Insère un nouvel auteur dans la base de données
     * 
     * @param auteur Auteur à insérer
     * @return ID généré pour l'auteur
     * @throws DatabaseException si erreur lors de l'insertion
     */
    public int insert(Auteur auteur) throws DatabaseException {
        String sql = "INSERT INTO Auteur (nom, prenom, nationalite, annee_naissance) " +
                    "VALUES (?, ?, ?, ?)";
        
        // Try-with-resources : fermeture automatique des ressources
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                                     Statement.RETURN_GENERATED_KEYS)) {
            
            // Définir les paramètres (1-indexed)
            stmt.setString(1, auteur.getNom());
            stmt.setString(2, auteur.getPrenom());
            stmt.setString(3, auteur.getNationalite());
            
            // Gestion de l'année de naissance (peut être null)
            if (auteur.getAnneeNaissance() > 0) {
                stmt.setInt(4, auteur.getAnneeNaissance());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            // Exécuter l'insertion
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    auteur.setIdAuteur(id);
                    return id;
                }
            }
            
            throw new DatabaseException("Échec de l'insertion : aucun ID généré");
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'insertion de l'auteur", e);
        }
    }
    
    // ==================== READ ====================
    
    /**
     * Recherche un auteur par son ID
     * 
     * @param id ID de l'auteur recherché
     * @return Optional contenant l'auteur si trouvé, vide sinon
     * @throws DatabaseException si erreur lors de la recherche
     */
    public Optional<Auteur> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM Auteur WHERE id_auteur = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Auteur auteur = mapResultSetToAuteur(rs);
                return Optional.of(auteur);
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche de l'auteur #" + id, e);
        }
    }
    
    /**
     * Récupère tous les auteurs de la base de données
     * 
     * @return Liste de tous les auteurs (peut être vide)
     * @throws DatabaseException si erreur lors de la récupération
     */
    public List<Auteur> findAll() throws DatabaseException {
        String sql = "SELECT * FROM Auteur ORDER BY nom, prenom";
        List<Auteur> auteurs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                auteurs.add(mapResultSetToAuteur(rs));
            }
            
            return auteurs;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des auteurs", e);
        }
    }
    
    /**
     * Recherche un auteur par son nom et prénom
     * 
     * @param nom Nom de l'auteur
     * @param prenom Prénom de l'auteur
     * @return Optional contenant l'auteur si trouvé, vide sinon
     * @throws DatabaseException si erreur lors de la recherche
     */
    public Optional<Auteur> findByNom(String nom, String prenom) throws DatabaseException {
        String sql = "SELECT * FROM Auteur WHERE nom = ? AND prenom = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Auteur auteur = mapResultSetToAuteur(rs);
                return Optional.of(auteur);
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche de l'auteur " + nom + " " + prenom, e);
        }
    }
    
    /**
     * Recherche des auteurs par nom (recherche partielle)
     * 
     * @param nom Nom (ou partie du nom) à rechercher
     * @return Liste des auteurs correspondants
     * @throws DatabaseException si erreur lors de la recherche
     */
    public List<Auteur> searchByNom(String nom) throws DatabaseException {
        String sql = "SELECT * FROM Auteur WHERE nom LIKE ? ORDER BY nom, prenom";
        List<Auteur> auteurs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Recherche partielle avec %
            stmt.setString(1, "%" + nom + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                auteurs.add(mapResultSetToAuteur(rs));
            }
            
            return auteurs;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche des auteurs", e);
        }
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Met à jour un auteur existant dans la base de données
     * 
     * @param auteur Auteur avec les nouvelles valeurs
     * @return true si la mise à jour a réussi, false sinon
     * @throws DatabaseException si erreur lors de la mise à jour
     */
    public boolean update(Auteur auteur) throws DatabaseException {
        String sql = "UPDATE Auteur SET nom = ?, prenom = ?, " +
                    "nationalite = ?, annee_naissance = ? " +
                    "WHERE id_auteur = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, auteur.getNom());
            stmt.setString(2, auteur.getPrenom());
            stmt.setString(3, auteur.getNationalite());
            
            if (auteur.getAnneeNaissance() > 0) {
                stmt.setInt(4, auteur.getAnneeNaissance());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(5, auteur.getIdAuteur());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la mise à jour de l'auteur #" + auteur.getIdAuteur(), e);
        }
    }
    
    // ==================== DELETE ====================
    
    /**
     * Supprime un auteur de la base de données
     * 
     * ⚠️ ATTENTION : Ne fonctionne que si l'auteur n'a pas de livres associés
     * (contrainte de clé étrangère)
     * 
     * @param id ID de l'auteur à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws DatabaseException si erreur lors de la suppression
     */
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM Auteur WHERE id_auteur = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Erreur 1451 = Cannot delete (referenced by foreign key)
            if (e.getErrorCode() == 1451) {
                throw new DatabaseException(
                    "Impossible de supprimer l'auteur #" + id + 
                    " : il a des livres associés", e);
            }
            throw new DatabaseException(
                "Erreur lors de la suppression de l'auteur #" + id, e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Compte le nombre total d'auteurs dans la base de données
     * 
     * @return Nombre total d'auteurs
     * @throws DatabaseException si erreur lors du comptage
     */
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) as total FROM Auteur";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des auteurs", e);
        }
    }
    
    /**
     * Vérifie si un auteur existe dans la base de données
     * 
     * @param id ID de l'auteur
     * @return true si l'auteur existe, false sinon
     * @throws DatabaseException si erreur lors de la vérification
     */
    public boolean exists(int id) throws DatabaseException {
        return findById(id).isPresent();
    }
    
    // ==================== MAPPING ====================
    
    /**
     * Convertit une ligne de ResultSet en objet Auteur
     * 
     * Méthode privée utilisée par toutes les méthodes READ
     * 
     * @param rs ResultSet positionné sur une ligne valide
     * @return Objet Auteur créé depuis les données SQL
     * @throws SQLException si erreur lors de la lecture des colonnes
     */
    private Auteur mapResultSetToAuteur(ResultSet rs) throws SQLException {
        Auteur auteur = new Auteur();
        
        // Mapper chaque colonne SQL vers un attribut Java
        auteur.setIdAuteur(rs.getInt("id_auteur"));
        auteur.setNom(rs.getString("nom"));
        auteur.setPrenom(rs.getString("prenom"));
        auteur.setNationalite(rs.getString("nationalite"));
        
        // Gérer la possibilité de NULL pour annee_naissance
        int annee = rs.getInt("annee_naissance");
        if (!rs.wasNull()) {
            auteur.setAnneeNaissance(annee);
        }
        
        return auteur;
    }
}
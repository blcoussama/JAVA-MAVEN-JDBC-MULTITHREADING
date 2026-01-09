package com.bibliotech.dao;

import com.bibliotech.models.Livre;
import com.bibliotech.models.Auteur;
import com.bibliotech.models.Categorie;
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
 * Data Access Object pour la table Livre
 * 
 * Gère toutes les opérations CRUD sur les livres avec :
 * - Relation avec Auteur (JOIN SQL)
 * - Gestion de l'enum Categorie
 * - Contraintes sur disponibilité
 * 
 * @author Belcadi Oussama
 * @version 1.0
 */
public class LivreDAO {
    
    // ==================== CREATE ====================
    
    /**
     * Insère un nouveau livre dans la base de données
     * 
     * IMPORTANT : Le livre doit avoir un Auteur avec un idAuteur valide
     * 
     * @param livre Livre à insérer (avec auteur.idAuteur défini)
     * @return ID généré pour le livre
     * @throws DatabaseException si erreur lors de l'insertion
     */
    public int insert(Livre livre) throws DatabaseException {
        String sql = "INSERT INTO Livre (isbn, titre, id_auteur, categorie, " +
                    "annee_publication, nombre_exemplaires, disponibles) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                                     Statement.RETURN_GENERATED_KEYS)) {
            
            // Paramètres
            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setInt(3, livre.getAuteur().getIdAuteur());
            stmt.setString(4, livre.getCategorie().name());
            
            // Année de publication (peut être null)
            if (livre.getAnneePublication() > 0) {
                stmt.setInt(5, livre.getAnneePublication());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(6, livre.getNombreExemplaires());
            stmt.setInt(7, livre.getDisponibles());
            
            // Exécuter
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    livre.setIdLivre(id);
                    return id;
                }
            }
            
            throw new DatabaseException("Échec de l'insertion : aucun ID généré");
            
        } catch (SQLException e) {
            // Erreur 1062 = Duplicate entry (ISBN déjà existant)
            if (e.getErrorCode() == 1062) {
                throw new DatabaseException(
                    "Un livre avec cet ISBN existe déjà : " + livre.getIsbn(), e);
            }
            throw new DatabaseException("Erreur lors de l'insertion du livre", e);
        }
    }
    
    // ==================== READ ====================
    
    /**
     * Recherche un livre par son ID
     * 
     * Effectue un JOIN avec la table Auteur pour récupérer les infos complètes
     * 
     * @param id ID du livre recherché
     * @return Optional contenant le livre si trouvé, vide sinon
     * @throws DatabaseException si erreur lors de la recherche
     */
    public Optional<Livre> findById(int id) throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE l.id_livre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Livre livre = mapResultSetToLivre(rs);
                return Optional.of(livre);
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du livre #" + id, e);
        }
    }
    
    /**
     * Recherche un livre par son ISBN (unique)
     * 
     * @param isbn ISBN du livre recherché
     * @return Optional contenant le livre si trouvé, vide sinon
     * @throws DatabaseException si erreur lors de la recherche
     */
    public Optional<Livre> findByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE l.isbn = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Livre livre = mapResultSetToLivre(rs);
                return Optional.of(livre);
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche du livre ISBN " + isbn, e);
        }
    }
    
    /**
     * Récupère tous les livres de la base de données
     * 
     * @return Liste de tous les livres (peut être vide)
     * @throws DatabaseException si erreur lors de la récupération
     */
    public List<Livre> findAll() throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "ORDER BY l.titre";
        
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            
            return livres;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des livres", e);
        }
    }
    
    /**
     * Recherche des livres par titre (recherche partielle)
     * 
     * @param titre Titre (ou partie du titre) à rechercher
     * @return Liste des livres correspondants
     * @throws DatabaseException si erreur lors de la recherche
     */
    public List<Livre> rechercherParTitre(String titre) throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE l.titre LIKE ? " +
                    "ORDER BY l.titre";
        
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titre + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            
            return livres;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par titre", e);
        }
    }
    
    /**
     * Recherche des livres par nom d'auteur (recherche partielle)
     * 
     * @param nomAuteur Nom de l'auteur (ou partie du nom)
     * @return Liste des livres correspondants
     * @throws DatabaseException si erreur lors de la recherche
     */
    public List<Livre> rechercherParAuteur(String nomAuteur) throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE a.nom LIKE ? OR a.prenom LIKE ? " +
                    "ORDER BY a.nom, l.titre";
        
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + nomAuteur + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            
            return livres;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche par auteur", e);
        }
    }
    
    /**
     * Recherche des livres par catégorie
     * 
     * @param categorie Catégorie recherchée
     * @return Liste des livres de cette catégorie
     * @throws DatabaseException si erreur lors de la recherche
     */
    public List<Livre> findByCategorie(Categorie categorie) throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE l.categorie = ? " +
                    "ORDER BY l.titre";
        
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            
            return livres;
            
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche par catégorie", e);
        }
    }
    
    /**
     * Récupère tous les livres d'un auteur spécifique
     * 
     * @param idAuteur ID de l'auteur
     * @return Liste des livres de cet auteur
     * @throws DatabaseException si erreur lors de la recherche
     */
    public List<Livre> findByAuteur(int idAuteur) throws DatabaseException {
        String sql = "SELECT l.*, " +
                    "a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite AS auteur_nationalite, " +
                    "a.annee_naissance AS auteur_annee_naissance " +
                    "FROM Livre l " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE l.id_auteur = ? " +
                    "ORDER BY l.titre";
        
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAuteur);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
            
            return livres;
            
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche des livres de l'auteur #" + idAuteur, e);
        }
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Met à jour un livre existant dans la base de données
     * 
     * @param livre Livre avec les nouvelles valeurs
     * @return true si la mise à jour a réussi, false sinon
     * @throws DatabaseException si erreur lors de la mise à jour
     */
    public boolean update(Livre livre) throws DatabaseException {
        String sql = "UPDATE Livre SET " +
                    "isbn = ?, titre = ?, id_auteur = ?, categorie = ?, " +
                    "annee_publication = ?, nombre_exemplaires = ?, disponibles = ? " +
                    "WHERE id_livre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setInt(3, livre.getAuteur().getIdAuteur());
            stmt.setString(4, livre.getCategorie().name());
            
            if (livre.getAnneePublication() > 0) {
                stmt.setInt(5, livre.getAnneePublication());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(6, livre.getNombreExemplaires());
            stmt.setInt(7, livre.getDisponibles());
            stmt.setInt(8, livre.getIdLivre());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Erreur 1062 = Duplicate entry (ISBN déjà existant)
            if (e.getErrorCode() == 1062) {
                throw new DatabaseException(
                    "Un autre livre possède déjà cet ISBN : " + livre.getIsbn(), e);
            }
            throw new DatabaseException(
                "Erreur lors de la mise à jour du livre #" + livre.getIdLivre(), e);
        }
    }
    
    /**
     * Met à jour uniquement la disponibilité d'un livre
     * 
     * Méthode utilitaire pour les emprunts/retours
     * 
     * @param idLivre ID du livre
     * @param nouveauDisponibles Nouvelle valeur de disponibilité
     * @return true si succès
     * @throws DatabaseException si erreur
     */
    public boolean updateDisponibilite(int idLivre, int nouveauDisponibles) 
            throws DatabaseException {
        String sql = "UPDATE Livre SET disponibles = ? WHERE id_livre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nouveauDisponibles);
            stmt.setInt(2, idLivre);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la mise à jour de la disponibilité", e);
        }
    }
    
    // ==================== DELETE ====================
    
    /**
     * Supprime un livre de la base de données
     * 
     * ⚠️ ATTENTION : Ne fonctionne que si le livre n'a pas d'emprunts associés
     * (contrainte de clé étrangère)
     * 
     * @param id ID du livre à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws DatabaseException si erreur lors de la suppression
     */
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM Livre WHERE id_livre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Erreur 1451 = Cannot delete (referenced by foreign key)
            if (e.getErrorCode() == 1451) {
                throw new DatabaseException(
                    "Impossible de supprimer le livre #" + id + 
                    " : il a des emprunts associés", e);
            }
            throw new DatabaseException(
                "Erreur lors de la suppression du livre #" + id, e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Compte le nombre total de livres dans la base de données
     * 
     * @return Nombre total de livres
     * @throws DatabaseException si erreur lors du comptage
     */
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) as total FROM Livre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des livres", e);
        }
    }
    
    /**
     * Compte le nombre de livres par catégorie
     * 
     * @param categorie Catégorie à compter
     * @return Nombre de livres dans cette catégorie
     * @throws DatabaseException si erreur
     */
    public long countByCategorie(Categorie categorie) throws DatabaseException {
        String sql = "SELECT COUNT(*) as total FROM Livre WHERE categorie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie.name());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage par catégorie", e);
        }
    }
    
    /**
     * Vérifie si un livre existe dans la base de données
     * 
     * @param id ID du livre
     * @return true si le livre existe, false sinon
     * @throws DatabaseException si erreur lors de la vérification
     */
    public boolean exists(int id) throws DatabaseException {
        return findById(id).isPresent();
    }
    
    /**
     * Vérifie si un ISBN existe déjà
     * 
     * @param isbn ISBN à vérifier
     * @return true si l'ISBN existe, false sinon
     * @throws DatabaseException si erreur
     */
    public boolean isbnExists(String isbn) throws DatabaseException {
        return findByIsbn(isbn).isPresent();
    }
    
    // ==================== MAPPING ====================
    
    /**
     * Convertit une ligne de ResultSet en objet Livre
     * 
     * Méthode privée utilisée par toutes les méthodes READ
     * Gère le mapping de l'Auteur et de l'Enum Categorie
     * 
     * @param rs ResultSet positionné sur une ligne valide
     * @return Objet Livre créé depuis les données SQL
     * @throws SQLException si erreur lors de la lecture des colonnes
     */
    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        // 1. Créer l'Auteur
        Auteur auteur = new Auteur();
        auteur.setIdAuteur(rs.getInt("id_auteur"));
        auteur.setNom(rs.getString("auteur_nom"));
        auteur.setPrenom(rs.getString("auteur_prenom"));
        auteur.setNationalite(rs.getString("auteur_nationalite"));
        
        int anneeNaissance = rs.getInt("auteur_annee_naissance");
        if (!rs.wasNull()) {
            auteur.setAnneeNaissance(anneeNaissance);
        }
        
        // 2. Créer le Livre
        Livre livre = new Livre();
        livre.setIdLivre(rs.getInt("id_livre"));
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(auteur);
        
        // 3. Mapper l'Enum Categorie
        String categorieStr = rs.getString("categorie");
        livre.setCategorie(Categorie.valueOf(categorieStr));
        
        // 4. Année de publication (peut être NULL)
        int anneePublication = rs.getInt("annee_publication");
        if (!rs.wasNull()) {
            livre.setAnneePublication(anneePublication);
        }
        
        // 5. Nombres d'exemplaires
        livre.setNombreExemplaires(rs.getInt("nombre_exemplaires"));
        livre.setDisponibles(rs.getInt("disponibles"));
        
        return livre;
    }
}
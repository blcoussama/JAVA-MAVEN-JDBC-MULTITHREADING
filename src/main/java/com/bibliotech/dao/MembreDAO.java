package com.bibliotech.dao;

import com.bibliotech.models.Membre;
import com.bibliotech.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object pour la table Membre
 *
 * Gère toutes les opérations CRUD sur les membres de la bibliothèque.
 * Utilise PreparedStatement pour prévenir les injections SQL.
 *
 * Contraintes de la table Membre :
 * - cin (UNIQUE) : Identifiant unique du membre
 * - email (UNIQUE) : Email unique du membre
 * - nombre_emprunts_actifs : Entre 0 et 5 (CHECK constraint)
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class MembreDAO {

    // ==================== CREATE ====================

    /**
     * Insère un nouveau membre dans la base de données
     *
     * @param membre Membre à insérer
     * @return ID généré du membre inséré
     * @throws DatabaseException si erreur BD ou CIN/email déjà existant
     */
    public int insert(Membre membre) throws DatabaseException {
        String sql = "INSERT INTO Membre (cin, nom, prenom, email, telephone, " +
                    "date_inscription, nombre_emprunts_actifs) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                                     Statement.RETURN_GENERATED_KEYS)) {

            // Définir les paramètres
            stmt.setString(1, membre.getCin());
            stmt.setString(2, membre.getNom());
            stmt.setString(3, membre.getPrenom());
            stmt.setString(4, membre.getEmail());
            stmt.setString(5, membre.getTelephone());
            stmt.setDate(6, Date.valueOf(membre.getDateInscription()));
            stmt.setInt(7, membre.getNombreEmpruntsActifs());

            // Exécuter l'insertion
            int rowsAffected = stmt.executeUpdate();

            // Récupérer l'ID généré
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    membre.setIdMembre(id);
                    return id;
                }
            }

            throw new DatabaseException("Échec de l'insertion du membre", null);

        } catch (SQLException e) {
            // Erreur 1062 = Duplicate entry (CIN ou email déjà existant)
            if (e.getErrorCode() == 1062) {
                if (e.getMessage().contains("cin")) {
                    throw new DatabaseException(
                        "Un membre avec ce CIN existe déjà : " + membre.getCin(), e);
                } else if (e.getMessage().contains("email")) {
                    throw new DatabaseException(
                        "Un membre avec cet email existe déjà : " + membre.getEmail(), e);
                }
            }
            throw new DatabaseException("Erreur lors de l'insertion du membre", e);
        }
    }

    // ==================== READ ====================

    /**
     * Recherche un membre par son ID
     *
     * @param id ID du membre
     * @return Optional contenant le membre ou vide si introuvable
     * @throws DatabaseException si erreur BD
     */
    public Optional<Membre> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM Membre WHERE id_membre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMembre(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche du membre #" + id, e);
        }
    }

    /**
     * Recherche un membre par son CIN (unique)
     *
     * @param cin CIN du membre
     * @return Optional contenant le membre ou vide si introuvable
     * @throws DatabaseException si erreur BD
     */
    public Optional<Membre> findByCin(String cin) throws DatabaseException {
        String sql = "SELECT * FROM Membre WHERE cin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMembre(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche du membre avec CIN : " + cin, e);
        }
    }

    /**
     * Recherche un membre par son email (unique)
     *
     * @param email Email du membre
     * @return Optional contenant le membre ou vide si introuvable
     * @throws DatabaseException si erreur BD
     */
    public Optional<Membre> findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM Membre WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMembre(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche du membre avec email : " + email, e);
        }
    }

    /**
     * Récupère tous les membres
     *
     * @return Liste de tous les membres, triés par nom puis prénom
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> findAll() throws DatabaseException {
        String sql = "SELECT * FROM Membre ORDER BY nom, prenom";
        List<Membre> membres = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }

            return membres;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération de tous les membres", e);
        }
    }

    /**
     * Recherche des membres par nom (partiel, LIKE)
     *
     * @param nom Nom à rechercher (partiel accepté)
     * @return Liste des membres dont le nom contient la chaîne recherchée
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> rechercherParNom(String nom) throws DatabaseException {
        String sql = "SELECT * FROM Membre WHERE nom LIKE ? ORDER BY nom, prenom";
        List<Membre> membres = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nom + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }

            return membres;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche par nom : " + nom, e);
        }
    }

    /**
     * Récupère les membres ayant des emprunts actifs
     *
     * @return Liste des membres avec nombre_emprunts_actifs > 0
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> getMembresActifs() throws DatabaseException {
        String sql = "SELECT * FROM Membre " +
                    "WHERE nombre_emprunts_actifs > 0 " +
                    "ORDER BY nombre_emprunts_actifs DESC, nom";
        List<Membre> membres = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }

            return membres;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des membres actifs", e);
        }
    }

    /**
     * Récupère les membres qui ont atteint le maximum d'emprunts (5)
     *
     * @return Liste des membres avec 5 emprunts actifs
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> getMembresMaxEmprunts() throws DatabaseException {
        String sql = "SELECT * FROM Membre " +
                    "WHERE nombre_emprunts_actifs >= 5 " +
                    "ORDER BY nom, prenom";
        List<Membre> membres = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }

            return membres;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des membres à capacité maximale", e);
        }
    }

    // ==================== UPDATE ====================

    /**
     * Met à jour les informations d'un membre
     *
     * @param membre Membre avec les nouvelles données
     * @return true si la mise à jour a réussi, false sinon
     * @throws DatabaseException si erreur BD
     */
    public boolean update(Membre membre) throws DatabaseException {
        String sql = "UPDATE Membre SET " +
                    "cin = ?, nom = ?, prenom = ?, email = ?, " +
                    "telephone = ?, date_inscription = ?, " +
                    "nombre_emprunts_actifs = ? " +
                    "WHERE id_membre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, membre.getCin());
            stmt.setString(2, membre.getNom());
            stmt.setString(3, membre.getPrenom());
            stmt.setString(4, membre.getEmail());
            stmt.setString(5, membre.getTelephone());
            stmt.setDate(6, Date.valueOf(membre.getDateInscription()));
            stmt.setInt(7, membre.getNombreEmpruntsActifs());
            stmt.setInt(8, membre.getIdMembre());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Erreur 1062 = Duplicate entry
            if (e.getErrorCode() == 1062) {
                if (e.getMessage().contains("cin")) {
                    throw new DatabaseException(
                        "Le CIN " + membre.getCin() + " est déjà utilisé", e);
                } else if (e.getMessage().contains("email")) {
                    throw new DatabaseException(
                        "L'email " + membre.getEmail() + " est déjà utilisé", e);
                }
            }
            throw new DatabaseException(
                "Erreur lors de la mise à jour du membre #" + membre.getIdMembre(), e);
        }
    }

    /**
     * Incrémente le nombre d'emprunts actifs d'un membre
     * Utilisé lors d'un nouvel emprunt
     *
     * @param idMembre ID du membre
     * @return true si la mise à jour a réussi
     * @throws DatabaseException si erreur BD ou limite atteinte
     */
    public boolean incrementerEmprunts(int idMembre) throws DatabaseException {
        String sql = "UPDATE Membre SET nombre_emprunts_actifs = nombre_emprunts_actifs + 1 " +
                    "WHERE id_membre = ? AND nombre_emprunts_actifs < 5";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMembre);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                // Vérifier si c'est parce que le membre a déjà 5 emprunts
                Optional<Membre> membre = findById(idMembre);
                if (membre.isPresent() && membre.get().getNombreEmpruntsActifs() >= 5) {
                    throw new DatabaseException(
                        "Le membre a atteint le maximum d'emprunts (5)", null);
                }
                return false;
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de l'incrémentation des emprunts pour membre #" + idMembre, e);
        }
    }

    /**
     * Décrémente le nombre d'emprunts actifs d'un membre
     * Utilisé lors d'un retour de livre
     *
     * @param idMembre ID du membre
     * @return true si la mise à jour a réussi
     * @throws DatabaseException si erreur BD
     */
    public boolean decrementerEmprunts(int idMembre) throws DatabaseException {
        String sql = "UPDATE Membre SET nombre_emprunts_actifs = nombre_emprunts_actifs - 1 " +
                    "WHERE id_membre = ? AND nombre_emprunts_actifs > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMembre);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la décrémentation des emprunts pour membre #" + idMembre, e);
        }
    }

    // ==================== DELETE ====================

    /**
     * Supprime un membre de la base de données
     *
     * Attention : Ne peut pas supprimer un membre qui a des emprunts associés
     * (contrainte de clé étrangère)
     *
     * @param id ID du membre à supprimer
     * @return true si la suppression a réussi
     * @throws DatabaseException si erreur BD ou contrainte FK
     */
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM Membre WHERE id_membre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Erreur 1451 = Contrainte de clé étrangère
            if (e.getErrorCode() == 1451) {
                throw new DatabaseException(
                    "Impossible de supprimer le membre #" + id +
                    " : il a des emprunts associés", e);
            }
            throw new DatabaseException(
                "Erreur lors de la suppression du membre #" + id, e);
        }
    }

    // ==================== UTILITY ====================

    /**
     * Compte le nombre total de membres
     *
     * @return Nombre de membres dans la base
     * @throws DatabaseException si erreur BD
     */
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM Membre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors du comptage des membres", e);
        }
    }

    /**
     * Vérifie si un membre existe par son ID
     *
     * @param id ID du membre
     * @return true si le membre existe
     * @throws DatabaseException si erreur BD
     */
    public boolean exists(int id) throws DatabaseException {
        String sql = "SELECT 1 FROM Membre WHERE id_membre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la vérification de l'existence du membre #" + id, e);
        }
    }

    /**
     * Vérifie si un CIN existe déjà
     *
     * @param cin CIN à vérifier
     * @return true si le CIN existe déjà
     * @throws DatabaseException si erreur BD
     */
    public boolean cinExists(String cin) throws DatabaseException {
        String sql = "SELECT 1 FROM Membre WHERE cin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cin);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la vérification du CIN : " + cin, e);
        }
    }

    /**
     * Vérifie si un email existe déjà
     *
     * @param email Email à vérifier
     * @return true si l'email existe déjà
     * @throws DatabaseException si erreur BD
     */
    public boolean emailExists(String email) throws DatabaseException {
        String sql = "SELECT 1 FROM Membre WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la vérification de l'email : " + email, e);
        }
    }

    /**
     * Vérifie si un membre peut emprunter (< 5 emprunts actifs)
     *
     * @param idMembre ID du membre
     * @return true si le membre peut emprunter
     * @throws DatabaseException si erreur BD
     */
    public boolean peutEmprunter(int idMembre) throws DatabaseException {
        Optional<Membre> membre = findById(idMembre);

        if (membre.isEmpty()) {
            return false;
        }

        return membre.get().getNombreEmpruntsActifs() < 5;
    }

    // ==================== MAPPING ====================

    /**
     * Convertit une ligne ResultSet en objet Membre
     *
     * @param rs ResultSet positionné sur la ligne à mapper
     * @return Objet Membre
     * @throws SQLException si erreur d'accès aux données
     */
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();

        membre.setIdMembre(rs.getInt("id_membre"));
        membre.setCin(rs.getString("cin"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setEmail(rs.getString("email"));
        membre.setTelephone(rs.getString("telephone"));
        membre.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        membre.setNombreEmpruntsActifs(rs.getInt("nombre_emprunts_actifs"));

        return membre;
    }
}

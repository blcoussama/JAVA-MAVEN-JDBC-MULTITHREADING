package com.bibliotech.dao;

import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Auteur;
import com.bibliotech.models.Categorie;
import com.bibliotech.models.StatutEmprunt;
import com.bibliotech.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object pour la table Emprunt
 *
 * COMPLEXITÉ ÉLEVÉE :
 * - Transactions atomiques (4 opérations minimum)
 * - JOIN avec 3 tables (Emprunt, Membre, Livre, Auteur)
 * - Méthodes synchronized pour accès concurrent
 * - Calcul automatique de retard
 * - Gestion des statuts (EN_COURS, RETOURNE, EN_RETARD)
 *
 * RELATIONS :
 * - Emprunt → Membre (FK id_membre)
 * - Emprunt → Livre (FK id_livre)
 * - Livre → Auteur (FK id_auteur via JOIN)
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class EmpruntDAO {

    // ==================== CREATE (TRANSACTION COMPLEXE) ====================

    /**
     * Effectue un emprunt avec transaction atomique
     *
     * TRANSACTION EN 4 ÉTAPES :
     * 1. Vérifier disponibilité livre (SELECT FOR UPDATE)
     * 2. Décrémenter disponibilité livre (UPDATE)
     * 3. Créer enregistrement emprunt (INSERT)
     * 4. Incrémenter compteur membre (UPDATE)
     *
     * IMPORTANT : Méthode synchronized pour éviter les race conditions
     *
     * @param idMembre ID du membre emprunteur
     * @param idLivre ID du livre à emprunter
     * @return Emprunt créé avec toutes les données
     * @throws DatabaseException si erreur BD ou livre indisponible
     */
    public synchronized Emprunt effectuerEmpruntTransaction(int idMembre, int idLivre)
            throws DatabaseException {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // ⚠️ Début transaction

            // ===== ÉTAPE 1 : Vérifier disponibilité avec verrou =====
            String sqlVerif = "SELECT disponibles FROM Livre " +
                            "WHERE id_livre = ? FOR UPDATE";
            PreparedStatement stmtVerif = conn.prepareStatement(sqlVerif);
            stmtVerif.setInt(1, idLivre);
            ResultSet rs = stmtVerif.executeQuery();

            if (!rs.next() || rs.getInt("disponibles") <= 0) {
                throw new DatabaseException(
                    "Livre #" + idLivre + " non disponible pour emprunt", null);
            }

            // ===== ÉTAPE 2 : Décrémenter disponibilité =====
            String sqlUpdate = "UPDATE Livre " +
                             "SET disponibles = disponibles - 1 " +
                             "WHERE id_livre = ?";
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, idLivre);
            int rowsUpdated = stmtUpdate.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DatabaseException(
                    "Impossible de mettre à jour la disponibilité du livre", null);
            }

            // ===== ÉTAPE 3 : Créer l'emprunt =====
            LocalDate dateEmprunt = LocalDate.now();
            LocalDate dateRetourPrevue = dateEmprunt.plusDays(14);  // 14 jours

            String sqlInsert = "INSERT INTO Emprunt " +
                             "(id_membre, id_livre, date_emprunt, date_retour_prevue, statut) " +
                             "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert,
                                         Statement.RETURN_GENERATED_KEYS);
            stmtInsert.setInt(1, idMembre);
            stmtInsert.setInt(2, idLivre);
            stmtInsert.setDate(3, Date.valueOf(dateEmprunt));
            stmtInsert.setDate(4, Date.valueOf(dateRetourPrevue));
            stmtInsert.setString(5, StatutEmprunt.EN_COURS.name());

            int rowsInserted = stmtInsert.executeUpdate();

            if (rowsInserted == 0) {
                throw new DatabaseException("Impossible de créer l'emprunt", null);
            }

            ResultSet rsKey = stmtInsert.getGeneratedKeys();
            int idEmprunt = 0;
            if (rsKey.next()) {
                idEmprunt = rsKey.getInt(1);
            }

            // ===== ÉTAPE 4 : Incrémenter compteur membre =====
            String sqlMembre = "UPDATE Membre " +
                             "SET nombre_emprunts_actifs = nombre_emprunts_actifs + 1 " +
                             "WHERE id_membre = ? AND nombre_emprunts_actifs < 5";
            PreparedStatement stmtMembre = conn.prepareStatement(sqlMembre);
            stmtMembre.setInt(1, idMembre);
            int rowsMembre = stmtMembre.executeUpdate();

            if (rowsMembre == 0) {
                throw new DatabaseException(
                    "Membre a atteint le maximum d'emprunts (5)", null);
            }

            // ✅ COMMIT : Tout s'est bien passé
            conn.commit();

            // Recharger l'emprunt complet avec toutes les données
            Optional<Emprunt> empruntOpt = findById(idEmprunt);

            if (empruntOpt.isEmpty()) {
                throw new DatabaseException(
                    "Emprunt créé mais impossible de le recharger", null);
            }

            return empruntOpt.get();

        } catch (SQLException e) {
            // ❌ ROLLBACK : Annuler toutes les modifications
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("⚠️  Transaction annulée suite à erreur");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new DatabaseException(
                "Échec de la transaction d'emprunt : " + e.getMessage(), e);

        } catch (DatabaseException e) {
            // ❌ ROLLBACK pour exceptions métier aussi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;

        } finally {
            // Réactiver auto-commit et fermer connexion
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Effectue le retour d'un livre avec transaction atomique
     *
     * TRANSACTION EN 4 ÉTAPES :
     * 1. Récupérer les infos de l'emprunt
     * 2. Mettre à jour l'emprunt (date retour + statut)
     * 3. Incrémenter disponibilité livre
     * 4. Décrémenter compteur membre
     *
     * @param idEmprunt ID de l'emprunt à retourner
     * @return Emprunt mis à jour avec date de retour
     * @throws DatabaseException si erreur BD ou emprunt introuvable
     */
    public synchronized Emprunt effectuerRetourTransaction(int idEmprunt)
            throws DatabaseException {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Début transaction

            // ===== ÉTAPE 1 : Récupérer infos emprunt =====
            String sqlSelect = "SELECT id_livre, id_membre, date_retour_prevue, statut " +
                             "FROM Emprunt WHERE id_emprunt = ?";
            PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect);
            stmtSelect.setInt(1, idEmprunt);
            ResultSet rs = stmtSelect.executeQuery();

            if (!rs.next()) {
                throw new DatabaseException("Emprunt #" + idEmprunt + " introuvable", null);
            }

            int idLivre = rs.getInt("id_livre");
            int idMembre = rs.getInt("id_membre");
            LocalDate dateRetourPrevue = rs.getDate("date_retour_prevue").toLocalDate();
            String statutActuel = rs.getString("statut");

            // Vérifier que l'emprunt est en cours
            if (!statutActuel.equals(StatutEmprunt.EN_COURS.name())) {
                throw new DatabaseException(
                    "L'emprunt #" + idEmprunt + " n'est pas en cours (statut: " +
                    statutActuel + ")", null);
            }

            // ===== ÉTAPE 2 : Mettre à jour l'emprunt =====
            LocalDate dateRetourEffective = LocalDate.now();
            boolean enRetard = dateRetourEffective.isAfter(dateRetourPrevue);
            StatutEmprunt nouveauStatut = enRetard ? StatutEmprunt.EN_RETARD
                                                    : StatutEmprunt.RETOURNE;

            String sqlUpdate = "UPDATE Emprunt " +
                             "SET date_retour_effective = ?, statut = ? " +
                             "WHERE id_emprunt = ?";
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setDate(1, Date.valueOf(dateRetourEffective));
            stmtUpdate.setString(2, nouveauStatut.name());
            stmtUpdate.setInt(3, idEmprunt);

            int rowsUpdated = stmtUpdate.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("Impossible de mettre à jour l'emprunt", null);
            }

            // ===== ÉTAPE 3 : Incrémenter disponibilité livre =====
            String sqlLivre = "UPDATE Livre " +
                            "SET disponibles = disponibles + 1 " +
                            "WHERE id_livre = ?";
            PreparedStatement stmtLivre = conn.prepareStatement(sqlLivre);
            stmtLivre.setInt(1, idLivre);
            stmtLivre.executeUpdate();

            // ===== ÉTAPE 4 : Décrémenter compteur membre =====
            String sqlMembre = "UPDATE Membre " +
                             "SET nombre_emprunts_actifs = nombre_emprunts_actifs - 1 " +
                             "WHERE id_membre = ? AND nombre_emprunts_actifs > 0";
            PreparedStatement stmtMembre = conn.prepareStatement(sqlMembre);
            stmtMembre.setInt(1, idMembre);
            stmtMembre.executeUpdate();

            // ✅ COMMIT
            conn.commit();

            // Recharger l'emprunt complet
            Optional<Emprunt> empruntOpt = findById(idEmprunt);

            if (empruntOpt.isEmpty()) {
                throw new DatabaseException(
                    "Retour effectué mais impossible de recharger l'emprunt", null);
            }

            return empruntOpt.get();

        } catch (SQLException e) {
            // ❌ ROLLBACK
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("⚠️  Transaction de retour annulée");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new DatabaseException(
                "Échec de la transaction de retour : " + e.getMessage(), e);

        } catch (DatabaseException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ==================== READ (AVEC JOIN COMPLEXE) ====================

    /**
     * Recherche un emprunt par ID avec toutes ses relations
     *
     * JOIN TRIPLE :
     * Emprunt → Membre
     * Emprunt → Livre → Auteur
     *
     * @param id ID de l'emprunt
     * @return Optional contenant l'emprunt complet ou vide
     * @throws DatabaseException si erreur BD
     */
    public Optional<Emprunt> findById(int id) throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE e.id_emprunt = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmprunt(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la recherche de l'emprunt #" + id, e);
        }
    }

    /**
     * Récupère tous les emprunts avec leurs relations
     *
     * @return Liste de tous les emprunts
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> findAll() throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "ORDER BY e.date_emprunt DESC";

        List<Emprunt> emprunts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }

            return emprunts;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération de tous les emprunts", e);
        }
    }

    /**
     * Récupère l'historique des emprunts d'un membre
     *
     * @param idMembre ID du membre
     * @return Liste des emprunts du membre (tous statuts)
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> findByMembre(int idMembre) throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE e.id_membre = ? " +
                    "ORDER BY e.date_emprunt DESC";

        List<Emprunt> emprunts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }

            return emprunts;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des emprunts du membre #" + idMembre, e);
        }
    }

    /**
     * Récupère l'historique des emprunts d'un livre
     *
     * @param idLivre ID du livre
     * @return Liste des emprunts du livre (tous statuts)
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> findByLivre(int idLivre) throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE e.id_livre = ? " +
                    "ORDER BY e.date_emprunt DESC";

        List<Emprunt> emprunts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLivre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }

            return emprunts;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des emprunts du livre #" + idLivre, e);
        }
    }

    /**
     * Récupère les emprunts par statut
     *
     * @param statut Statut recherché (EN_COURS, RETOURNE, EN_RETARD)
     * @return Liste des emprunts avec ce statut
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> findByStatut(StatutEmprunt statut) throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE e.statut = ? " +
                    "ORDER BY e.date_emprunt DESC";

        List<Emprunt> emprunts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }

            return emprunts;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des emprunts avec statut " + statut, e);
        }
    }

    /**
     * Récupère les emprunts en retard
     * Calcul automatique : EN_COURS + date_retour_prevue < aujourd'hui
     *
     * @return Liste des emprunts en retard
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> findEmpruntsEnRetard() throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE e.statut = 'EN_COURS' " +
                    "AND e.date_retour_prevue < CURDATE() " +
                    "ORDER BY e.date_retour_prevue ASC";

        List<Emprunt> emprunts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }

            return emprunts;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des emprunts en retard", e);
        }
    }

    /**
     * Récupère les emprunts en cours d'un membre
     *
     * @param idMembre ID du membre
     * @return Liste des emprunts EN_COURS du membre
     * @throws DatabaseException si erreur BD
     */
    public List<Emprunt> findEmpruntsEnCoursByMembre(int idMembre)
            throws DatabaseException {
        String sql = "SELECT " +
                    // Emprunt
                    "e.id_emprunt, e.date_emprunt, e.date_retour_prevue, " +
                    "e.date_retour_effective, e.statut, " +
                    // Membre
                    "m.id_membre, m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom, " +
                    "m.email, m.telephone, m.date_inscription, m.nombre_emprunts_actifs, " +
                    // Livre
                    "l.id_livre, l.isbn, l.titre, l.categorie, l.annee_publication, " +
                    "l.nombre_exemplaires, l.disponibles, " +
                    // Auteur
                    "a.id_auteur, a.nom AS auteur_nom, a.prenom AS auteur_prenom, " +
                    "a.nationalite, a.annee_naissance " +
                    "FROM Emprunt e " +
                    "JOIN Membre m ON e.id_membre = m.id_membre " +
                    "JOIN Livre l ON e.id_livre = l.id_livre " +
                    "JOIN Auteur a ON l.id_auteur = a.id_auteur " +
                    "WHERE e.id_membre = ? AND e.statut = 'EN_COURS' " +
                    "ORDER BY e.date_emprunt DESC";

        List<Emprunt> emprunts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMembre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }

            return emprunts;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la récupération des emprunts en cours du membre #" +
                idMembre, e);
        }
    }

    // ==================== UPDATE ====================

    /**
     * Met à jour un emprunt
     *
     * @param emprunt Emprunt avec les nouvelles données
     * @return true si la mise à jour a réussi
     * @throws DatabaseException si erreur BD
     */
    public boolean update(Emprunt emprunt) throws DatabaseException {
        String sql = "UPDATE Emprunt SET " +
                    "id_membre = ?, id_livre = ?, date_emprunt = ?, " +
                    "date_retour_prevue = ?, date_retour_effective = ?, statut = ? " +
                    "WHERE id_emprunt = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprunt.getMembre().getIdMembre());
            stmt.setInt(2, emprunt.getLivre().getIdLivre());
            stmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            stmt.setDate(4, Date.valueOf(emprunt.getDateRetourPrevue()));

            if (emprunt.getDateRetourEffective() != null) {
                stmt.setDate(5, Date.valueOf(emprunt.getDateRetourEffective()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setString(6, emprunt.getStatut().name());
            stmt.setInt(7, emprunt.getIdEmprunt());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la mise à jour de l'emprunt #" +
                emprunt.getIdEmprunt(), e);
        }
    }

    // ==================== DELETE ====================

    /**
     * Supprime un emprunt
     *
     * @param id ID de l'emprunt à supprimer
     * @return true si la suppression a réussi
     * @throws DatabaseException si erreur BD
     */
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM Emprunt WHERE id_emprunt = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la suppression de l'emprunt #" + id, e);
        }
    }

    // ==================== UTILITY ====================

    /**
     * Compte le nombre total d'emprunts
     *
     * @return Nombre d'emprunts dans la base
     * @throws DatabaseException si erreur BD
     */
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM Emprunt";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des emprunts", e);
        }
    }

    /**
     * Compte les emprunts par statut
     *
     * @param statut Statut à compter
     * @return Nombre d'emprunts avec ce statut
     * @throws DatabaseException si erreur BD
     */
    public long countByStatut(StatutEmprunt statut) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM Emprunt WHERE statut = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors du comptage des emprunts par statut", e);
        }
    }

    /**
     * Vérifie si un emprunt existe
     *
     * @param id ID de l'emprunt
     * @return true si l'emprunt existe
     * @throws DatabaseException si erreur BD
     */
    public boolean exists(int id) throws DatabaseException {
        String sql = "SELECT 1 FROM Emprunt WHERE id_emprunt = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseException(
                "Erreur lors de la vérification de l'existence de l'emprunt #" + id, e);
        }
    }

    // ==================== MAPPING COMPLEXE ====================

    /**
     * Convertit ResultSet en objet Emprunt avec toutes ses relations
     *
     * MAPPING HIÉRARCHIQUE :
     * 1. Créer Auteur (depuis colonnes avec alias)
     * 2. Créer Livre (avec Auteur)
     * 3. Créer Membre (depuis colonnes avec alias)
     * 4. Créer Emprunt (avec Membre et Livre)
     *
     * @param rs ResultSet positionné sur la ligne
     * @return Objet Emprunt complet
     * @throws SQLException si erreur d'accès aux données
     */
    private Emprunt mapResultSetToEmprunt(ResultSet rs) throws SQLException {
        // ===== 1. Créer Auteur =====
        Auteur auteur = new Auteur();
        auteur.setIdAuteur(rs.getInt("id_auteur"));
        auteur.setNom(rs.getString("auteur_nom"));
        auteur.setPrenom(rs.getString("auteur_prenom"));
        auteur.setNationalite(rs.getString("nationalite"));

        int anneeNaissance = rs.getInt("annee_naissance");
        if (!rs.wasNull()) {
            auteur.setAnneeNaissance(anneeNaissance);
        }

        // ===== 2. Créer Livre (avec Auteur) =====
        Livre livre = new Livre();
        livre.setIdLivre(rs.getInt("id_livre"));
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(auteur);  // Composition
        livre.setCategorie(Categorie.valueOf(rs.getString("categorie")));

        int anneePublication = rs.getInt("annee_publication");
        if (!rs.wasNull()) {
            livre.setAnneePublication(anneePublication);
        }

        livre.setNombreExemplaires(rs.getInt("nombre_exemplaires"));
        livre.setDisponibles(rs.getInt("disponibles"));

        // ===== 3. Créer Membre =====
        Membre membre = new Membre();
        membre.setIdMembre(rs.getInt("id_membre"));
        membre.setCin(rs.getString("cin"));
        membre.setNom(rs.getString("membre_nom"));
        membre.setPrenom(rs.getString("membre_prenom"));
        membre.setEmail(rs.getString("email"));
        membre.setTelephone(rs.getString("telephone"));
        membre.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        membre.setNombreEmpruntsActifs(rs.getInt("nombre_emprunts_actifs"));

        // ===== 4. Créer Emprunt (avec Membre et Livre) =====
        Emprunt emprunt = new Emprunt();
        emprunt.setIdEmprunt(rs.getInt("id_emprunt"));
        emprunt.setMembre(membre);  // Composition
        emprunt.setLivre(livre);    // Composition
        emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        emprunt.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());

        Date dateRetourEffective = rs.getDate("date_retour_effective");
        if (dateRetourEffective != null) {
            emprunt.setDateRetourEffective(dateRetourEffective.toLocalDate());
        }

        emprunt.setStatut(StatutEmprunt.valueOf(rs.getString("statut")));

        return emprunt;
    }
}
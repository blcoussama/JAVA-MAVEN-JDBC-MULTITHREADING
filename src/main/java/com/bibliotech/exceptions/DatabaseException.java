package com.bibliotech.exceptions;

import java.sql.SQLException;

/**
 * Exception levée lors d'erreurs liées à la base de données
 *
 * Cette exception encapsule les SQLException pour fournir
 * des messages d'erreur plus clairs au niveau métier.
 *
 * Cas d'usage :
 * - Erreur de connexion à la base de données
 * - Erreur d'exécution de requête SQL
 * - Violation de contraintes (clé primaire, clé étrangère, etc.)
 * - Timeout de transaction
 * - Table ou colonne inexistante
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class DatabaseException extends BiblioTechException {

    private static final long serialVersionUID = 1L;

    /**
     * SQLException originale encapsulée
     * Permet de récupérer les détails techniques de l'erreur SQL
     */
    private SQLException sqlException;

    /**
     * Constructeur avec message et SQLException
     *
     * @param message Message d'erreur métier
     * @param sqlException SQLException originale
     */
    public DatabaseException(String message, SQLException sqlException) {
        super("Erreur base de données: " + message, "ERR_DB_001");
        this.sqlException = sqlException;
    }

    /**
     * Constructeur avec message seulement
     * Utilisé quand l'erreur BD n'est pas une SQLException
     *
     * @param message Message d'erreur
     */
    public DatabaseException(String message) {
        super("Erreur base de données: " + message, "ERR_DB_001");
        this.sqlException = null;
    }

    /**
     * Récupère la SQLException originale
     *
     * @return SQLException encapsulée (peut être null)
     */
    public SQLException getSqlException() {
        return sqlException;
    }

    /**
     * Récupère le code d'erreur SQL (SQLState)
     *
     * @return Code SQLState ou null si pas de SQLException
     */
    public String getSqlState() {
        return sqlException != null ? sqlException.getSQLState() : null;
    }

    /**
     * Récupère le code d'erreur vendor-specific
     *
     * @return Code erreur MySQL ou 0 si pas de SQLException
     */
    public int getErrorCode() {
        return sqlException != null ? sqlException.getErrorCode() : 0;
    }

    /**
     * Retourne une représentation détaillée de l'erreur
     * Inclut le message métier + détails SQL
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());

        if (sqlException != null) {
            sb.append("\n  SQLState: ").append(getSqlState());
            sb.append("\n  Error Code: ").append(getErrorCode());
            sb.append("\n  SQL Message: ").append(sqlException.getMessage());
        }

        return sb.toString();
    }
}

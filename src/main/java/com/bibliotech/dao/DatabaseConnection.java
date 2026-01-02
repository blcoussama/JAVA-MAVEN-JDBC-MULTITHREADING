package com.bibliotech.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de connexion à la base de données MySQL
 * BiblioTech - Projet Java Avancé
 */
public class DatabaseConnection {

    // ⚠️ CONFIGURATION - Remplace le mot de passe par le tien !
    private static final String URL = "jdbc:mysql://localhost:3306/bibliotech_db";
    private static final String USER = "root";
    private static final String PASSWORD = "***REDACTED***"; // ⚠️ METS TON MOT DE PASSE ICI !

    /**
     * Établit une connexion à la base de données MySQL
     * @return Connection - Objet de connexion
     * @throws SQLException si erreur de connexion
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            return conn;

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable : " + e.getMessage(), e);
        }
    }

    /**
     * Teste la connexion à la base de données
     * @return true si connexion réussie, false sinon
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion MySQL réussie !");
                System.out.println("📊 Base de données : " + conn.getCatalog());
                System.out.println("🔗 URL : " + URL);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL :");
            System.err.println("   Message : " + e.getMessage());
            System.err.println("   Code : " + e.getErrorCode());
            return false;
        }
        return false;
    }
}

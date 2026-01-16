package com.bibliotech.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/bibliotech_db";
    private static final String USER = "root";
    private static final String PASSWORD = "***REDACTED***"; 

    // On laisse la méthode lancer l'exception : c'est à celui qui l'appelle de gérer l'erreur
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Connexion réussie à : " + conn.getCatalog());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        return false;
    }
}
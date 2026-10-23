package com.bibliotech.demo;

import com.bibliotech.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Vérifie la connexion MySQL et affiche la base active, la version du serveur et le contenu. */
public class DemoConnexion {

    public static void main(String[] args) {
        DatabaseConnection connexion = DemoSupport.connexion();
        System.out.println("=== Test de connexion MySQL - BiblioTech ===");
        System.out.println("Configuration : " + connexion.getConfig());

        try (Connection conn = connexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT DATABASE() AS db, VERSION() AS version, "
                             + "(SELECT COUNT(*) FROM auteur) AS auteurs, (SELECT COUNT(*) FROM livre) AS livres")) {
            rs.next();
            System.out.println("[OK] Connexion réussie");
            System.out.println("  Base active     : " + rs.getString("db"));
            System.out.println("  Version MySQL   : " + rs.getString("version"));
            System.out.println("  Auteurs / livres: " + rs.getInt("auteurs") + " / " + rs.getInt("livres"));
        } catch (SQLException e) {
            DemoSupport.arreter("Connexion impossible : " + e.getMessage()
                    + "\nVérifiez que MySQL est démarré, que la base existe et que BIBLIOTECH_DB_* sont corrects.");
        }
    }
}

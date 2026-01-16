package com.bibliotech.main;

import com.bibliotech.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;

/**
 * Classe de test pour vérifier la connexion MySQL
 * BiblioTech - Projet Java Avancé
 */
public class TestConnection {

    public static void main(String[] args) throws SQLException {

        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║  TEST CONNEXION MySQL - BiblioTech        ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();

        // Test 1 : Connexion simple
        System.out.println("📌 Test 1 : Connexion à MySQL...");
        boolean connected = DatabaseConnection.testConnection();
        System.out.println();

        if (!connected) {
            System.err.println("❌ Échec du test de connexion !");
            System.err.println("⚠️  Vérifier :");
            System.err.println("   - MySQL est démarré (service MySQL80)");
            System.err.println("   - Le mot de passe dans DatabaseConnection.java");
            System.err.println("   - La base de données bibliotech_db existe");
            return;
        }

        // Test 2 : Requête SQL simple
        System.out.println("📌 Test 2 : Exécution d'une requête SQL...");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DATABASE() as db, VERSION() as version")) {

            if (rs.next()) {
                String database = rs.getString("db");
                String version = rs.getString("version");

                System.out.println("✅ Requête SQL réussie !");
                System.out.println("   Base de données active : " + database);
                System.out.println("   Version MySQL : " + version);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la requête SQL :");
            e.printStackTrace();
            return;
        }

        System.out.println();
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  ✅ TOUS LES TESTS SONT RÉUSSIS ! ✅       ║ ");
        System.out.println("║  MySQL est prêt pour BiblioTech !          ║");
        System.out.println("╚════════════════════════════════════════════╝");
    }
}

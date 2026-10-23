package com.bibliotech.dao;

import com.bibliotech.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Ouvre des connexions MySQL via {@link DriverManager} à partir d'une {@link DatabaseConfig}.
 *
 * <p>Chaque appel ouvre une nouvelle connexion : les DAO la ferment aussitôt grâce au
 * try-with-resources. Une connexion JDBC n'étant pas thread-safe, chaque thread travaille
 * ainsi avec sa propre connexion.
 */
public final class DatabaseConnection implements ConnectionFactory {

    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    /** Connexion configurée par les variables d'environnement BIBLIOTECH_DB_*. */
    public static DatabaseConnection depuisEnvironnement() {
        return new DatabaseConnection(DatabaseConfig.depuisEnvironnement());
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.url(), config.utilisateur(), config.motDePasse());
    }

    public DatabaseConfig getConfig() {
        return config;
    }
}

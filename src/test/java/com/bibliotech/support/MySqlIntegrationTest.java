package com.bibliotech.support;

import com.bibliotech.config.DatabaseConfig;
import com.bibliotech.dao.ConnectionFactory;
import com.bibliotech.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Base des tests d'intégration : un MySQL 8.4 jetable (Docker, via Testcontainers) initialisé
 * avec le vrai {@code sql/schema.sql}. Le conteneur est partagé par toutes les classes de test
 * et supprimé automatiquement à la fin ; les tables sont vidées avant chaque test.
 */
public abstract class MySqlIntegrationTest {

    protected static final MySQLContainer MYSQL = new MySQLContainer(DockerImageName.parse("mysql:8.4"))
            .withDatabaseName("bibliotech_db")
            .withCopyFileToContainer(MountableFile.forHostPath("sql/schema.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");

    static {
        MYSQL.start();
    }

    protected static final ConnectionFactory CONNEXIONS = new DatabaseConnection(
            new DatabaseConfig(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword()));

    @BeforeEach
    void viderLesTables() throws SQLException {
        try (Connection conn = CONNEXIONS.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("TRUNCATE TABLE livre");
            stmt.execute("TRUNCATE TABLE auteur");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
}

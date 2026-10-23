package com.bibliotech.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

    @Test
    void utiliseLesValeursParDefautSaufPourLeMotDePasse() {
        DatabaseConfig config = DatabaseConfig.depuis(Map.of(DatabaseConfig.VAR_MOT_DE_PASSE, "secret-de-test"));

        assertEquals(DatabaseConfig.URL_PAR_DEFAUT, config.url());
        assertEquals(DatabaseConfig.UTILISATEUR_PAR_DEFAUT, config.utilisateur());
        assertEquals("secret-de-test", config.motDePasse());
    }

    @Test
    void lesVariablesDEnvironnementRemplacentLesValeursParDefaut() {
        DatabaseConfig config = DatabaseConfig.depuis(Map.of(
                DatabaseConfig.VAR_URL, "jdbc:mysql://db.exemple:3306/autre_base",
                DatabaseConfig.VAR_UTILISATEUR, "lecteur",
                DatabaseConfig.VAR_MOT_DE_PASSE, "x"));

        assertEquals("jdbc:mysql://db.exemple:3306/autre_base", config.url());
        assertEquals("lecteur", config.utilisateur());
    }

    @Test
    void refuseDeDemarrerSansMotDePasse() {
        IllegalStateException absent = assertThrows(IllegalStateException.class, () -> DatabaseConfig.depuis(Map.of()));
        assertTrue(absent.getMessage().contains(DatabaseConfig.VAR_MOT_DE_PASSE));

        assertThrows(IllegalStateException.class,
                () -> DatabaseConfig.depuis(Map.of(DatabaseConfig.VAR_MOT_DE_PASSE, "   ")));
    }

    @Test
    void refuseUneUrlQuiNEstPasJdbc() {
        assertThrows(IllegalArgumentException.class, () -> DatabaseConfig.depuis(Map.of(
                DatabaseConfig.VAR_URL, "mysql://localhost/bibliotech_db",
                DatabaseConfig.VAR_MOT_DE_PASSE, "x")));
    }

    @Test
    void neDevoileJamaisLeMotDePasse() {
        DatabaseConfig config = new DatabaseConfig("jdbc:mysql://localhost:3307/bibliotech_db", "bibliotech", "ne-pas-afficher");

        assertFalse(config.toString().contains("ne-pas-afficher"));
        assertTrue(config.toString().contains("****"));
    }
}

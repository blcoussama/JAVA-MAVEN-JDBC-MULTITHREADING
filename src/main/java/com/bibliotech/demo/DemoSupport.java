package com.bibliotech.demo;

import com.bibliotech.config.DatabaseConfig;
import com.bibliotech.dao.DatabaseConnection;
import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;
import java.util.List;

/** Outils communs aux démonstrations : connexion configurée par l'environnement et arrêt propre. */
final class DemoSupport {

    private DemoSupport() {
    }

    /** Connexion lue dans les variables BIBLIOTECH_DB_* ; arrête le programme avec un message clair sinon. */
    static DatabaseConnection connexion() {
        try {
            return DatabaseConnection.depuisEnvironnement();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return arreter("Configuration invalide : " + e.getMessage());
        }
    }

    static BibliothequeService service() {
        return new BibliothequeService(connexion());
    }

    static List<Livre> chargerLivres(BibliothequeService service) {
        try {
            return service.chargerTousLesLivres();
        } catch (BibliothequeException e) {
            String cause = e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : "";
            return arreter(e.getMessage() + cause
                    + "\nLa base MySQL est-elle démarrée (docker compose up -d) et "
                    + DatabaseConfig.VAR_URL + " correcte ?");
        }
    }

    static <T> T arreter(String message) {
        System.err.println("[ERREUR] " + message);
        System.exit(1);
        throw new AssertionError("inaccessible");
    }
}

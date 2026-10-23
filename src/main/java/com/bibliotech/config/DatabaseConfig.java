package com.bibliotech.config;

import java.util.Map;
import java.util.Objects;

/**
 * Paramètres de connexion à MySQL, lus dans les variables d'environnement.
 *
 * <p>Aucun identifiant n'est écrit dans le code source :
 * <ul>
 *   <li>{@value #VAR_URL} : URL JDBC (défaut : {@value #URL_PAR_DEFAUT}) ;</li>
 *   <li>{@value #VAR_UTILISATEUR} : utilisateur MySQL (défaut : {@value #UTILISATEUR_PAR_DEFAUT}) ;</li>
 *   <li>{@value #VAR_MOT_DE_PASSE} : mot de passe, <strong>obligatoire</strong>, sans valeur par défaut.</li>
 * </ul>
 */
public record DatabaseConfig(String url, String utilisateur, String motDePasse) {

    public static final String VAR_URL = "BIBLIOTECH_DB_URL";
    public static final String VAR_UTILISATEUR = "BIBLIOTECH_DB_USER";
    public static final String VAR_MOT_DE_PASSE = "BIBLIOTECH_DB_PASSWORD";

    public static final String URL_PAR_DEFAUT = "jdbc:mysql://localhost:3307/bibliotech_db";
    public static final String UTILISATEUR_PAR_DEFAUT = "bibliotech";

    public DatabaseConfig {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(utilisateur, "utilisateur");
        Objects.requireNonNull(motDePasse, "motDePasse");
        if (!url.startsWith("jdbc:")) {
            throw new IllegalArgumentException("L'URL de connexion doit être une URL JDBC (jdbc:...).");
        }
        if (utilisateur.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur MySQL ne peut pas être vide.");
        }
    }

    /** Construit la configuration à partir des variables d'environnement du processus. */
    public static DatabaseConfig depuisEnvironnement() {
        return depuis(System.getenv());
    }

    /**
     * Construit la configuration à partir d'une table de variables (testable sans modifier l'environnement).
     *
     * @throws IllegalStateException si le mot de passe n'est pas fourni
     */
    public static DatabaseConfig depuis(Map<String, String> variables) {
        String url = valeurOuDefaut(variables, VAR_URL, URL_PAR_DEFAUT);
        String utilisateur = valeurOuDefaut(variables, VAR_UTILISATEUR, UTILISATEUR_PAR_DEFAUT);
        String motDePasse = variables.get(VAR_MOT_DE_PASSE);
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalStateException("La variable d'environnement " + VAR_MOT_DE_PASSE
                    + " n'est pas définie : exportez-la avant de lancer l'application (voir README, section Configuration).");
        }
        return new DatabaseConfig(url, utilisateur, motDePasse);
    }

    private static String valeurOuDefaut(Map<String, String> variables, String nom, String defaut) {
        String valeur = variables.get(nom);
        return (valeur == null || valeur.isBlank()) ? defaut : valeur.trim();
    }

    /** Le mot de passe n'apparaît jamais dans les journaux ni dans les messages d'erreur. */
    @Override
    public String toString() {
        return "DatabaseConfig[url=" + url + ", utilisateur=" + utilisateur + ", motDePasse=****]";
    }
}

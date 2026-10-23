package com.bibliotech.exception;

/**
 * Exception métier de l'application (exception vérifiée : l'appelant doit la traiter).
 *
 * <p>Elle sert à la fois aux erreurs de validation et à l'encapsulation des erreurs
 * techniques (par exemple une {@link java.sql.SQLException}) conservées comme cause.
 */
public class BibliothequeException extends Exception {

    private static final long serialVersionUID = 1L;

    public BibliothequeException(String message) {
        super(message);
    }

    public BibliothequeException(String message, Throwable cause) {
        super(message, cause);
    }
}

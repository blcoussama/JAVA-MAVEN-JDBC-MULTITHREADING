package com.bibliotech.exception;

/** Levée quand on tente d'enregistrer un livre dont le titre existe déjà. */
public class LivreDejaExistantException extends BibliothequeException {

    private static final long serialVersionUID = 1L;

    public LivreDejaExistantException(String message) {
        super(message);
    }

    public LivreDejaExistantException(String message, Throwable cause) {
        super(message, cause);
    }
}

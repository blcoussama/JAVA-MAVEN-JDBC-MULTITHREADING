package com.bibliotech.exceptions;

/**
 * Exception levée quand un livre existe mais n'a plus d'exemplaires disponibles
 * 
 * Cas d'usage :
 * - Tentative d'emprunt alors que disponibles = 0
 * - Tous les exemplaires sont déjà empruntés
 * 
 * @author Belcadi Oussama
 * @version 1.0
 */
public class LivreIndisponibleException extends BiblioTechException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructeur avec ISBN
     * Génère un message par défaut
     * 
     * @param isbn ISBN du livre indisponible
     */
    public LivreIndisponibleException(String isbn) {
        super("Le livre avec ISBN " + isbn + " est indisponible", "ERR_LIVRE_001");
    }
    
    /**
     * Constructeur avec ISBN et message personnalisé
     * 
     * @param isbn ISBN du livre indisponible
     * @param message Message d'erreur personnalisé
     */
    public LivreIndisponibleException(String isbn, String message) {
        super(message + " (ISBN: " + isbn + ")", "ERR_LIVRE_001");
    }
}
package com.bibliotech.exceptions;

public class BiblioTechException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
    private String code;

    /**
     * Constructeur avec message et code d'erreur
     * @param message Description de l'erreur
     * @param code Code d'erreur (ex: "ERR_LIVRE_001")
     */
    public BiblioTechException(String message, String code) {
        super(message);
        this.code = code;
    }

    /**
     * Constructeur avec message seulement
     * @param message Description de l'erreur
     */
    public BiblioTechException(String message) {
        super(message);
        this.code = "ERR_UNKNOWN";
    }

    /**
     * Récupère le code d'erreur
     * @return Code d'erreur
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + getMessage();
    }
}

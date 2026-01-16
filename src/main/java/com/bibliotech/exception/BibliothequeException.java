package com.bibliotech.exception;


// On hérite de Exception : c'est une "Checked Exception"
// Java obligera le développeur à la gérer (sécurité)
public class BibliothequeException extends Exception {
	
	// Ce numéro supprime le warning. C'est la carte d'identité de ta classe.
    private static final long serialVersionUID = 1L;

    // Constructeur pour envoyer un message d'erreur
    public BibliothequeException(String message) {
        super(message);
    }

    // Constructeur pour emballer une erreur technique (ex: SQLException) 
    // tout en ajoutant un message métier
    public BibliothequeException(String message, Throwable cause) {
        super(message, cause);
    }
}
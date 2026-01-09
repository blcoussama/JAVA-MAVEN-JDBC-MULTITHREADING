package com.bibliotech.exceptions;

/**
 * Exception levée quand un livre n'est pas trouvé dans le catalogue
 *
 * Cas d'usage :
 * - Recherche par ISBN sans résultat
 * - Recherche par ID sans résultat
 * - Tentative d'accès à un livre supprimé
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class LivreNonTrouveException extends BiblioTechException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructeur avec ISBN
     * Génère un message par défaut
     *
     * @param isbn ISBN du livre recherché
     */
    public LivreNonTrouveException(String isbn) {
        super("Aucun livre trouvé avec ISBN " + isbn, "ERR_LIVRE_002");
    }

    /**
     * Constructeur avec ISBN et message personnalisé
     *
     * @param isbn ISBN du livre recherché
     * @param message Message d'erreur personnalisé
     */
    public LivreNonTrouveException(String isbn, String message) {
        super(message + " (ISBN: " + isbn + ")", "ERR_LIVRE_002");
    }
}

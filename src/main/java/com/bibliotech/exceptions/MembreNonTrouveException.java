package com.bibliotech.exceptions;

/**
 * Exception levée quand un membre n'est pas trouvé dans le système
 *
 * Cas d'usage :
 * - Recherche par CIN sans résultat
 * - Recherche par ID sans résultat
 * - Tentative d'emprunt par membre inexistant
 * - Tentative d'accès à un membre supprimé
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class MembreNonTrouveException extends BiblioTechException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructeur avec CIN
     * Génère un message par défaut
     *
     * @param cin CIN du membre recherché
     */
    public MembreNonTrouveException(String cin) {
        super("Aucun membre trouvé avec CIN " + cin, "ERR_MEMBRE_001");
    }

    /**
     * Constructeur avec CIN et message personnalisé
     *
     * @param cin CIN du membre recherché
     * @param message Message d'erreur personnalisé
     */
    public MembreNonTrouveException(String cin, String message) {
        super(message + " (CIN: " + cin + ")", "ERR_MEMBRE_001");
    }
}

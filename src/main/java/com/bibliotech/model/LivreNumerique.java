package com.bibliotech.model;

/**
 * Livre numérique : illustre l'héritage. Un {@code LivreNumerique} EST UN {@link Livre}
 * et peut donc être passé partout où un {@code Livre} est attendu.
 *
 * <p>La taille du fichier n'est pas persistée (la table {@code livre} ne possède pas de colonne
 * dédiée) : seule la partie « Livre » est enregistrée en base.
 */
public class LivreNumerique extends Livre {

    private double tailleMo;

    public LivreNumerique(String titre, Auteur auteur, Categorie categorie, double tailleMo) {
        super(titre, auteur, categorie);
        this.tailleMo = tailleMo;
    }

    public double getTailleMo() { return tailleMo; }
    public void setTailleMo(double tailleMo) { this.tailleMo = tailleMo; }

    /** Redéfinition de toString (polymorphisme). */
    @Override
    public String toString() {
        return super.toString() + " [Format numérique : " + tailleMo + " Mo]";
    }
}

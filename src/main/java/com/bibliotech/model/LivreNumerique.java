package com.bibliotech.model;

public class LivreNumerique extends Livre {
    private double tailleMo; // Propriété spécifique

    // Constructeur : il utilise 'super' pour remplir la partie "Livre" de l'objet
    public LivreNumerique(String titre, Auteur auteur, Categorie categorie, double tailleMo) {
        super(titre, auteur, categorie); 
        this.tailleMo = tailleMo;
    }

    // Getter et Setter
    public double getTailleMo() { return tailleMo; }
    public void setTailleMo(double tailleMo) { this.tailleMo = tailleMo; }

    // On surcharge toString (Polymorphisme)
    @Override
    public String toString() {
        return super.toString() + " [💾 Format Numérique: " + tailleMo + " Mo]";
    }
}
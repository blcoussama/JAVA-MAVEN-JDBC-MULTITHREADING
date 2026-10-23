package com.bibliotech.model;

/** Auteur d'un ou plusieurs livres. */
public class Auteur {

    private int id;
    private String nom;
    private String prenom;

    /** Auteur lu depuis la base : il possède déjà un identifiant. */
    public Auteur(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    /** Nouvel auteur, pas encore enregistré : l'identifiant sera attribué par MySQL. */
    public Auteur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    @Override
    public String toString() {
        return (prenom == null || prenom.isBlank()) ? nom : prenom + " " + nom;
    }
}

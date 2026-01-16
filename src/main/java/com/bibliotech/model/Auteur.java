package com.bibliotech.model;

public class Auteur {
    private int id;
    private String nom;
    private String prenom;

    // Constructeurs
    
    // Version 1 : Utile quand on récupère un auteur DEPUIS la base (il a déjà un ID)
    public Auteur(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }
    
    // Version 2 : Utile pour CRÉER un nouvel auteur (pas encore d'ID)
    public Auteur(String nom, String prenom) {
    	this.nom = nom;
    	this.prenom = prenom;
    }

    // Getters et Setters (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
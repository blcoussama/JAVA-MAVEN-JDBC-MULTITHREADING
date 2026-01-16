package com.bibliotech.model;

public class Livre {
    private int id;
    private String titre;
    private Auteur auteur; // Association : Le livre "connaît" son auteur
    private Categorie categorie;
    private StatutEmprunt statut;

    // Constructeur pour récupérer depuis la base (avec ID)
    public Livre(int id, String titre, Auteur auteur, Categorie categorie) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.statut = StatutEmprunt.DISPONIBLE;
    }

    // Constructeur pour créer un NOUVEAU livre (sans ID)
    public Livre(String titre, Auteur auteur, Categorie categorie) {
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.statut = StatutEmprunt.DISPONIBLE;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public Auteur getAuteur() { return auteur; }
    public Categorie getCategorie() { return categorie; }
    public StatutEmprunt getStatut() { return statut; }

    @Override
    public String toString() {
        return "Livre: " + titre + " | Auteur: " + auteur + " | Catégorie: " + categorie + " [" + statut + "]";
    }
}
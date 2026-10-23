package com.bibliotech.model;

/** Livre du catalogue ; il connaît son auteur (association). */
public class Livre {

    private int id;
    private final String titre;
    private final Auteur auteur;
    private final Categorie categorie;
    private final StatutEmprunt statut;

    /** Livre lu depuis la base (avec identifiant). */
    public Livre(int id, String titre, Auteur auteur, Categorie categorie) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.statut = StatutEmprunt.DISPONIBLE;
    }

    /** Nouveau livre, pas encore enregistré. */
    public Livre(String titre, Auteur auteur, Categorie categorie) {
        this(0, titre, auteur, categorie);
    }

    public int getId() { return id; }
    /** Renseigné par le DAO après l'insertion (identifiant généré par MySQL). */
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public Auteur getAuteur() { return auteur; }
    public Categorie getCategorie() { return categorie; }
    public StatutEmprunt getStatut() { return statut; }

    @Override
    public String toString() {
        return "Livre: " + titre + " | Auteur: " + auteur + " | Catégorie: " + categorie + " [" + statut + "]";
    }
}

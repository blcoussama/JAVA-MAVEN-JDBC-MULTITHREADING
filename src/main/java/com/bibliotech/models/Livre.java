package com.bibliotech.models;

import java.util.Objects;

public class Livre {
    // ==================== ATTRIBUTS ====================
    private int idLivre;
    private String isbn;
    private String titre;
    private Auteur auteur;                    // COMPOSITION
    private Categorie categorie;              // ENUM
    private int anneePublication;
    private int nombreExemplaires;
    private int disponibles;

    // ==================== CONSTRUCTEURS ====================
    
    /**
     * Constructeur par défaut
     */
    public Livre() {
        this.disponibles = 0;
        this.nombreExemplaires = 0;
    }

    /**
     * Constructeur avec paramètres essentiels
     */
    public Livre(String isbn, String titre, Auteur auteur, Categorie categorie) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombreExemplaires = 1;
        this.disponibles = 1;
    }

    /**
     * Constructeur complet
     */
    public Livre(String isbn, String titre, Auteur auteur, 
                 Categorie categorie, int nombreExemplaires) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombreExemplaires = nombreExemplaires;
        this.disponibles = nombreExemplaires;
    }

    // ==================== GETTERS/SETTERS ====================
    
    public int getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(int idLivre) {
        this.idLivre = idLivre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public int getNombreExemplaires() {
        return nombreExemplaires;
    }

    public void setNombreExemplaires(int nombreExemplaires) {
        if (nombreExemplaires < 0) {
            throw new IllegalArgumentException("Nombre d'exemplaires ne peut pas être négatif");
        }
        this.nombreExemplaires = nombreExemplaires;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(int disponibles) {
        if (disponibles < 0 || disponibles > nombreExemplaires) {
            throw new IllegalArgumentException("Disponibles invalide");
        }
        this.disponibles = disponibles;
    }

    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Vérifie si le livre est disponible
     * @return true si au moins un exemplaire disponible
     */
    public boolean estDisponible() {
        return disponibles > 0;
    }

    // ==================== equals/hashCode ====================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livre livre = (Livre) o;
        return Objects.equals(isbn, livre.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    // ==================== toString ====================
    
    @Override
    public String toString() {
        return String.format("Livre{isbn='%s', titre='%s', auteur=%s, categorie=%s, disponibles=%d/%d}",
                           isbn, titre, 
                           (auteur != null ? auteur.getNom() : "Inconnu"), 
                           categorie, disponibles, nombreExemplaires);
    }
}
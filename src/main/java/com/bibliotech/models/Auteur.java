package com.bibliotech.models;

import java.util.Objects;

public class Auteur {
    // ==================== ATTRIBUTS ====================
    private int idAuteur;
    private String nom;
    private String prenom;
    private String nationalite;
    private int anneeNaissance;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur par défaut
     */
    public Auteur() {
    }

    /**
     * Constructeur avec paramètres principaux
     */
    public Auteur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    /**
     * Constructeur complet
     */
    public Auteur(String nom, String prenom, String nationalite, int anneeNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
        this.anneeNaissance = anneeNaissance;
    }

    // ==================== GETTERS/SETTERS ====================

    public int getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(int idAuteur) {
        this.idAuteur = idAuteur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public int getAnneeNaissance() {
        return anneeNaissance;
    }

    public void setAnneeNaissance(int anneeNaissance) {
        this.anneeNaissance = anneeNaissance;
    }

    // ==================== equals/hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auteur auteur = (Auteur) o;
        return Objects.equals(nom, auteur.nom) &&
               Objects.equals(prenom, auteur.prenom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, prenom);
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return prenom + " " + nom +
               (nationalite != null ? " (" + nationalite + ")" : "");
    }
}

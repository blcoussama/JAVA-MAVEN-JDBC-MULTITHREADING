package com.bibliotech.models;

import java.time.LocalDate;
import java.util.Objects;

public class Membre {
    // ==================== ATTRIBUTS ====================
    private int idMembre;
    private String cin;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateInscription;
    private int nombreEmpruntsActifs;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur par défaut
     */
    public Membre() {
        this.nombreEmpruntsActifs = 0;
    }

    /**
     * Constructeur avec paramètres essentiels
     */
    public Membre(String cin, String nom, String prenom, String email) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = LocalDate.now();
        this.nombreEmpruntsActifs = 0;
    }

    /**
     * Constructeur complet
     */
    public Membre(String cin, String nom, String prenom, String email, String telephone) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateInscription = LocalDate.now();
        this.nombreEmpruntsActifs = 0;
    }

    // ==================== GETTERS/SETTERS ====================

    public int getIdMembre() {
        return idMembre;
    }

    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public int getNombreEmpruntsActifs() {
        return nombreEmpruntsActifs;
    }

    public void setNombreEmpruntsActifs(int nombreEmpruntsActifs) {
        if (nombreEmpruntsActifs < 0 || nombreEmpruntsActifs > 5) {
            throw new IllegalArgumentException("Nombre d'emprunts doit être entre 0 et 5");
        }
        this.nombreEmpruntsActifs = nombreEmpruntsActifs;
    }

    // ==================== MÉTHODES MÉTIER ====================

    /**
     * Vérifie si le membre peut emprunter
     * @return true si moins de 5 emprunts actifs
     */
    public boolean peutEmprunter() {
        return nombreEmpruntsActifs < 5;
    }

    // ==================== equals/hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Membre membre = (Membre) o;
        return Objects.equals(cin, membre.cin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cin);
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("Membre{cin='%s', nom='%s', prenom='%s', email='%s', empruntsActifs=%d}",
                           cin, nom, prenom, email, nombreEmpruntsActifs);
    }
}

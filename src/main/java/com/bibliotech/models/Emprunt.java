package com.bibliotech.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Emprunt {
    // ==================== CONSTANTES ====================
    private static final int DUREE_EMPRUNT_JOURS = 14;

    // ==================== ATTRIBUTS ====================
    private int idEmprunt;
    private Membre membre;                    // COMPOSITION
    private Livre livre;                      // COMPOSITION
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private StatutEmprunt statut;             // ENUM

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur par défaut
     */
    public Emprunt() {
    }

    /**
     * Constructeur pour nouvel emprunt
     * @param membre Membre qui emprunte
     * @param livre Livre emprunté
     */
    public Emprunt(Membre membre, Livre livre) {
        this.membre = membre;
        this.livre = livre;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = dateEmprunt.plusDays(DUREE_EMPRUNT_JOURS);
        this.statut = StatutEmprunt.EN_COURS;
    }

    /**
     * Constructeur complet (pour chargement depuis BD)
     */
    public Emprunt(int idEmprunt, Membre membre, Livre livre,
                   LocalDate dateEmprunt, LocalDate dateRetourPrevue,
                   LocalDate dateRetourEffective, StatutEmprunt statut) {
        this.idEmprunt = idEmprunt;
        this.membre = membre;
        this.livre = livre;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.statut = statut;
    }

    // ==================== GETTERS/SETTERS ====================

    public int getIdEmprunt() {
        return idEmprunt;
    }

    public void setIdEmprunt(int idEmprunt) {
        this.idEmprunt = idEmprunt;
    }

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public StatutEmprunt getStatut() {
        return statut;
    }

    public void setStatut(StatutEmprunt statut) {
        this.statut = statut;
    }

    // ==================== MÉTHODES MÉTIER ====================

    /**
     * Vérifie si l'emprunt est en retard
     * @return true si en retard
     */
    public boolean estEnRetard() {
        if (statut == StatutEmprunt.RETOURNE) {
            return dateRetourEffective.isAfter(dateRetourPrevue);
        }
        return LocalDate.now().isAfter(dateRetourPrevue);
    }

    /**
     * Calcule la durée de l'emprunt en jours
     * @return durée en jours
     */
    public long calculerDuree() {
        LocalDate dateFin = (dateRetourEffective != null)
                           ? dateRetourEffective
                           : LocalDate.now();
        return ChronoUnit.DAYS.between(dateEmprunt, dateFin);
    }

    /**
     * Calcule le nombre de jours de retard
     * @return jours de retard (0 si pas de retard)
     */
    public long calculerJoursRetard() {
        if (!estEnRetard()) {
            return 0;
        }

        LocalDate dateFin = (dateRetourEffective != null)
                           ? dateRetourEffective
                           : LocalDate.now();
        return ChronoUnit.DAYS.between(dateRetourPrevue, dateFin);
    }

    /**
     * Effectue le retour du livre
     */
    public void effectuerRetour() {
        this.dateRetourEffective = LocalDate.now();
        this.statut = estEnRetard() ? StatutEmprunt.EN_RETARD
                                    : StatutEmprunt.RETOURNE;
    }

    // ==================== equals/hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emprunt emprunt = (Emprunt) o;
        return idEmprunt == emprunt.idEmprunt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEmprunt);
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("Emprunt{id=%d, membre=%s, livre=%s, dateEmprunt=%s, statut=%s}",
                           idEmprunt,
                           (membre != null ? membre.getNom() : "Inconnu"),
                           (livre != null ? livre.getTitre() : "Inconnu"),
                           dateEmprunt, statut);
    }
}

package com.bibliotech.models;

public enum StatutEmprunt {
    EN_COURS("En cours"),
    RETOURNE("Retourné"),
    EN_RETARD("En retard");

    private final String libelle;

    StatutEmprunt(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}

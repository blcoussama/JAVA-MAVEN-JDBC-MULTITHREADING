package com.bibliotech.models;

public enum Categorie {
    ROMAN("Roman"),
    SCIENCE("Science"),
    HISTOIRE("Histoire"),
    TECHNOLOGIE("Technologie"),
    ART("Art");

    private final String libelle;

    Categorie(String libelle) {
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
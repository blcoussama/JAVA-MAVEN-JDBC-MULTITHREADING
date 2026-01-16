package com.bibliotech.service;

import com.bibliotech.model.Livre;
import com.bibliotech.model.Auteur;
import com.bibliotech.exception.BibliothequeException;
import java.util.List;

public interface ActionBibliotheque {
    // Les méthodes essentielles que ton service DOIT posséder
    void sauvegarderLivre(Livre livre) throws BibliothequeException;
    List<Livre> chargerTousLesLivres() throws BibliothequeException;
    void sauvegarderAuteur(Auteur auteur) throws BibliothequeException;
}
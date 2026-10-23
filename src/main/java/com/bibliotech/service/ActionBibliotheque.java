package com.bibliotech.service;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Livre;
import java.util.List;

/** Opérations de persistance offertes par le service de bibliothèque. */
public interface ActionBibliotheque {

    void sauvegarderLivre(Livre livre) throws BibliothequeException;

    List<Livre> chargerTousLesLivres() throws BibliothequeException;

    void sauvegarderAuteur(Auteur auteur) throws BibliothequeException;
}

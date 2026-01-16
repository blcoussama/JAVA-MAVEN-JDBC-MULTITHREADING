package com.bibliotech.main;

import com.bibliotech.model.*;
import com.bibliotech.service.BibliothequeService;

import com.bibliotech.exception.BibliothequeException;

public class TestAppThreadsSynchronization {
    public static void main(String[] args) {
        BibliothequeService service = new BibliothequeService();
        
        // On utilise l'auteur ID 1 (Oussama) présent dans ta base
        Auteur auteur = new Auteur(1, "Belcadi", "Oussama");
        Livre nouveauLivre = new Livre("L'Avare", auteur, Categorie.ROMAN);

        // Thread 1
        Thread t1 = new Thread(() -> {
            try { 
                service.sauvegarderLivre(nouveauLivre); 
            } catch (BibliothequeException e) {
                System.out.println("❌ Thread 1 : " + e.getMessage());
            }
        }, "Utilisateur-A");

        // Thread 2
        Thread t2 = new Thread(() -> {
            try { 
                service.sauvegarderLivre(nouveauLivre); 
            } catch (BibliothequeException e) {
                System.out.println("❌ Thread 2 : " + e.getMessage());
            }
        }, "Utilisateur-B");

        System.out.println("⚔️ Lancement de deux ajouts simultanés...\n");
        t1.start();
        t2.start();
    }
}
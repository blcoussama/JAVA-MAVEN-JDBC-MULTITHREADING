package com.bibliotech.main;

import com.bibliotech.service.BibliothequeService;

public class TestAppThreadsSemaphore {
    public static void main(String[] args) {
        BibliothequeService service = new BibliothequeService();

        System.out.println("=== 🚥 DÉMONSTRATION MULTITHREADING & SÉMAPHORE ===");
        System.out.println("Scénario : 5 utilisateurs veulent lire les livres.");
        System.out.println("Contrainte : Serveur limité à 2 connexions simultanées.\n");

        // On crée 5 "Utilisateurs" (Threads)
        String[] utilisateurs = {"[thread1]", "[thread2]", "[thread3]", "[thread4]", "[thread5]"};

        for (String nom : utilisateurs) {
            // On crée un nouveau fil d'exécution (Thread) pour chaque utilisateur
            Thread t = new Thread(() -> {
                service.consulterLivresSimultane(nom);
            });

            // On lance le thread (il devient autonome)
            t.start();
        }
        
        // Le Main continue sa vie pendant que les threads travaillent !
        System.out.println("🚀 [THREAD MAIN] : Les 5 threads sont lancés. Je ne suis pas bloqué !\n");
    }
}
package com.bibliotech.main;
import com.bibliotech.service.BibliothequeService;

public class TestAppThreadsSimpleParallelisme {
    public static void main(String[] args) throws InterruptedException {
        BibliothequeService service = new BibliothequeService();
        
        System.out.println("--- TEST 1 : SANS MULTITHREADING (Séquentiel) ---");
        long debut1 = System.currentTimeMillis();
        
        service.recupererTotalLivresLentement("Action_1");
        service.recupererTotalLivresLentement("Action_2");
        
        long fin1 = System.currentTimeMillis();
        System.out.println("⏱️ Temps total Séquentiel : " + (fin1 - debut1) + " ms");

        System.out.println("\n--- TEST 2 : AVEC MULTITHREADING (Parallèle) ---");
        long debut2 = System.currentTimeMillis();

        // On lance les deux en même temps dans deux fils séparés
        Thread t1 = new Thread(() -> service.recupererTotalLivresLentement("Thread_A"));
        Thread t2 = new Thread(() -> service.recupererTotalLivresLentement("Thread_B"));

        t1.start(); // Lance Thread_A
        t2.start(); // Lance Thread_B

        // On attend que les deux aient fini pour calculer le temps
        t1.join(); 
        t2.join();

        long fin2 = System.currentTimeMillis();
        System.out.println("⏱️ Temps total Multithread : " + (fin2 - debut2) + " ms");
    }
}
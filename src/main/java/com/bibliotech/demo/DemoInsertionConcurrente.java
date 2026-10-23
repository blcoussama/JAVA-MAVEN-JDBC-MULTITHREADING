package com.bibliotech.demo;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.exception.LivreDejaExistantException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Insertion concurrente : plusieurs threads tentent d'ajouter le MÊME livre au même instant.
 *
 * <ol>
 *   <li>avec {@code sauvegarderLivreSiAbsent} (vérification + insertion dans une méthode
 *       {@code synchronized}) : le doublon est détecté côté Java ;</li>
 *   <li>avec {@code sauvegarderLivre} (aucun verrou Java) : le doublon est refusé par la
 *       contrainte UNIQUE de MySQL.</li>
 * </ol>
 * Dans les deux cas, exactement une insertion réussit.
 */
public class DemoInsertionConcurrente {

    private static final int NB_THREADS = 5;

    private interface Insertion {
        void inserer(Livre livre) throws BibliothequeException;
    }

    public static void main(String[] args) throws InterruptedException {
        BibliothequeService service = DemoSupport.service();
        Auteur auteur;
        try {
            List<Auteur> auteurs = service.chargerTousLesAuteurs();
            if (auteurs.isEmpty()) {
                DemoSupport.arreter("Aucun auteur en base : chargez sql/seed.sql (voir README).");
            }
            auteur = auteurs.get(0);
        } catch (BibliothequeException e) {
            DemoSupport.arreter(e.getMessage());
            return;
        }
        // Titre propre à cette exécution : la démonstration donne le même résultat à chaque lancement.
        long suffixe = System.currentTimeMillis();

        System.out.println("=== 1. Méthode synchronized (vérifier puis insérer) ===");
        lancer(NB_THREADS, new Livre("L'Avare (synchronized) #" + suffixe, auteur, Categorie.ROMAN),
                service::sauvegarderLivreSiAbsent);

        System.out.println("\n=== 2. Sans verrou Java : contrainte UNIQUE de la base ===");
        lancer(NB_THREADS, new Livre("L'Avare (UNIQUE) #" + suffixe, auteur, Categorie.ROMAN),
                service::sauvegarderLivre);
    }

    private static void lancer(int nbThreads, Livre modele, Insertion insertion) throws InterruptedException {
        CountDownLatch depart = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(nbThreads);
        List<Future<String>> resultats = new ArrayList<>();
        for (int i = 1; i <= nbThreads; i++) {
            String nom = "Utilisateur-" + i;
            resultats.add(pool.submit(() -> {
                // Chaque thread a son propre objet Livre ; tous attendent le signal de départ.
                Livre livre = new Livre(modele.getTitre(), modele.getAuteur(), modele.getCategorie());
                depart.await();
                try {
                    insertion.inserer(livre);
                    return nom + " : insertion réussie (ID " + livre.getId() + ")";
                } catch (LivreDejaExistantException e) {
                    return nom + " : refusé -> " + e.getMessage();
                }
            }));
        }
        depart.countDown(); // tous les threads démarrent en même temps
        int succes = 0;
        for (Future<String> resultat : resultats) {
            try {
                String ligne = resultat.get();
                if (ligne.contains("réussie")) {
                    succes++;
                }
                System.out.println("  " + ligne);
            } catch (ExecutionException e) {
                System.out.println("  erreur inattendue : " + e.getCause().getMessage());
            }
        }
        pool.shutdown();
        System.out.println("  -> " + succes + " insertion réussie sur " + nbThreads + " tentatives simultanées.");
    }
}

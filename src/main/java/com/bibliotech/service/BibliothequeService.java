package com.bibliotech.service;

import com.bibliotech.model.Livre;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import java.util.*;
import java.util.stream.Collectors;

import com.bibliotech.dao.LivreDAO;
import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.exception.BibliothequeException;
import java.sql.SQLException;

import java.util.concurrent.Semaphore;

public class BibliothequeService implements ActionBibliotheque {
	
	// On instancie les DAO une seule fois pour tout le service
	private LivreDAO livreDAO = new LivreDAO();
    private AuteurDAO auteurDAO = new AuteurDAO();
    
    // On crée un Sémaphore : seulement 2 threads peuvent "passer" en même temps
    private final Semaphore semaphore = new Semaphore(3);
    
    
//============================================================================//
			 // GESTION DE LA PERSISTENCE (Base de données) //                 
//============================================================================//
    
 // --- PARTIE AUTEUR ---

    /**
     * Sauvegarde un auteur.
     * Validation métier : Le nom est obligatoire.
     */
    public void sauvegarderAuteur(Auteur auteur) throws BibliothequeException {
        if (auteur.getNom() == null || auteur.getNom().trim().isEmpty()) {
            throw new BibliothequeException("⚠️ Erreur métier : Le nom de l'auteur est obligatoire !");
        }
        try {
            auteurDAO.ajouterAuteur(auteur);
        } catch (SQLException e) {
            throw new BibliothequeException("❌ Erreur technique lors de l'ajout de l'auteur.", e);
        }
    }

    /**
     * Récupère tous les auteurs.
     */
    public List<Auteur> chargerTousLesAuteurs() throws BibliothequeException {
        try {
            return auteurDAO.listerTousLesAuteurs();
        } catch (SQLException e) {
            throw new BibliothequeException("❌ Impossible de charger la liste des auteurs.", e);
        }
    }

    /**
     * Trouve un auteur précis. 
     * Si l'auteur n'existe pas, on lance une exception plutôt que de renvoyer null 
     * (C'est plus propre en architecture Service).
     */
    public Auteur chercherAuteurParId(int id) throws BibliothequeException {
        try {
            Auteur a = auteurDAO.trouverAuteurParId(id);
            if (a == null) {
                throw new BibliothequeException("⚠️ Aucun auteur trouvé avec l'ID : " + id);
            }
            return a;
        } catch (SQLException e) {
            throw new BibliothequeException("❌ Erreur lors de la recherche de l'auteur ID: " + id, e);
        }
    }
    
  
 // --- PARTIE LIVRE ---
    
    // Cette méthode montre l'utilisation de BibliothequeException 
    // pour encapsuler une erreur SQL et ajouter une validation métier.
    public void sauvegarderLivre(Livre livre) throws BibliothequeException {
        // Validation métier : on ne veut pas de titre vide
        if (livre.getTitre() == null || livre.getTitre().isEmpty()) {
            throw new BibliothequeException("Le titre ne peut pas être vide !");
        }
        
        try {
            livreDAO.ajouterLivre(livre);
        } catch (SQLException e) {
            // On transforme l'erreur SQL technique en erreur métier
            throw new BibliothequeException("Erreur lors de la sauvegarde en base", e);
        }
    }
    
    
    // VERSION POUR TESTER LE MULTI THREADING AVEC SYNCHRONIZATION
// ============================================================================//
//	  				  MULTI-THREADING AVEC SYNCHRONIZE                         //
//=============================================================================//
//    public synchronized void sauvegarderLivre(Livre livre) throws BibliothequeException {
//        // 1. Validation : Titre vide (Ce que tu avais déjà)
//        if (livre.getTitre() == null || livre.getTitre().trim().isEmpty()) {
//            throw new BibliothequeException("Le titre ne peut pas être vide !");
//        }
//
//        try {
//            // 2. Validation : Existence (Utilisation des Streams)
//            // On récupère la liste pour vérifier si le titre existe déjà
//            List<Livre> existants = livreDAO.listerTousLesLivres();
//            boolean dejaPresent = existants.stream()
//                    .anyMatch(l -> l.getTitre().equalsIgnoreCase(livre.getTitre()));
//
//            if (dejaPresent) {
//                // On lance l'exception si le livre existe déjà
//                throw new BibliothequeException("Erreur : Le livre '" + livre.getTitre() + "' existe déjà !");
//            }
//
//            // 3. Si tout est OK, on procède à l'insertion
//            System.out.println("💾 [" + Thread.currentThread().getName() + "] Autorisation d'ajout pour Le livre : " + livre.getTitre());
//            livreDAO.ajouterLivre(livre);
//            System.out.println("✅ [" + Thread.currentThread().getName() + "] Livre sauvegardé avec succès.");
//
//        } catch (SQLException e) {
//            throw new BibliothequeException("Erreur lors de la sauvegarde en base", e);
//        }
//    }

    
    // Récupère les livres depuis la base.
    public List<Livre> chargerTousLesLivres() throws BibliothequeException {
        try {
            return livreDAO.listerTousLesLivres();
        } catch (SQLException e) {
            throw new BibliothequeException("Impossible de charger la bibliothèque", e);
        }
    }

	
//============================================================================//
					// METHODES VERSIONS COLLECTIONS //
//============================================================================//

    // 1. CONCEPT : LIST (ArrayList)
    // On l'utilise déjà, mais ici on montre comment filtrer manuellement
    public List<Livre> filtrerParCategorie(List<Livre> tousLesLivres, Categorie cible) {
        List<Livre> resultat = new ArrayList<>();
        for (Livre l : tousLesLivres) {
            if (l.getCategorie() == cible) {
                resultat.add(l);
            }
        }
        return resultat;
    }

    // 2. CONCEPT : SET (HashSet)
    // Objectif : Extraire les catégories uniques présentes dans la liste
    public Set<Categorie> obtenirCategoriesUniques(List<Livre> tousLesLivres) {
        // Le HashSet refuse les doublons automatiquement !
        Set<Categorie> categories = new HashSet<>();
        for (Livre l : tousLesLivres) {
            categories.add(l.getCategorie());
        }
        return categories;
    }

    // 3 CONCEPT : MAP (HashMap / Indexation)
    // Objectif : Créer un dictionnaire "Titre -> Objet Livre" pour une recherche éclair
    public Map<String, Livre> indexerLivresParTitre(List<Livre> tousLesLivres) {
        Map<String, Livre> index = new HashMap<>();
        for (Livre l : tousLesLivres) {
            // Clé : le titre (String), Valeur : l'objet Livre complet
            index.put(l.getTitre(), l);
        }
        return index;
    }
    
    // 4. CONCEPT : TRIE (Collection avec Comparator)
    // tri alphabétique par Titre
    public void trierLivresParTitre(List<Livre> tousLesLivres) {
        // Collections.sort prend la liste et un "comparateur"
        Collections.sort(tousLesLivres, new Comparator<Livre>() {
            @Override
            public int compare(Livre l1, Livre l2) {
                // On utilise le compareTo de la classe String
                // Renvoie : 
                //   négatif si l1 < l2
                //   zéro si l1 == l2
                //   positif si l1 > l2
                return l1.getTitre().compareToIgnoreCase(l2.getTitre());
            }
        });
    }
    
    // tri Numerique Par ID du Plus Recent au plus ancien
    public void trierLivresParId(List<Livre> tousLesLivres) {
        Collections.sort(tousLesLivres, new Comparator<Livre>() {
            @Override
            public int compare(Livre l1, Livre l2) {
                // On compare deux entiers (id)
                return Integer.compare(l2.getId(), l1.getId());
            }
        });
    }
    
//============================================================================//
    				   // METHODES VERSIONS STREAMS //
//============================================================================//
    
    
    // VERSION MODERNE 1 : LE STREAM (Filtre)
    // 1. On ouvre le flux (.stream)
    // 2. On garde seulement si la condition est vraie (.filter)
    // 3. On remet tout dans une liste (.toList)
    public List<Livre> filtrerParCategorieStream(List<Livre> tousLesLivres, Categorie cible) {
        return tousLesLivres.stream()
                .filter(l -> l.getCategorie() == cible)
                .toList(); // Si tu as une erreur ici (Java < 16), dis-le moi !
    }
    
	// VERSION MODERNE 2 : LE STREAM (Set / Unicité)
    public Set<Categorie> obtenirCategoriesUniquesStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .map(Livre::getCategorie) // 1. On extrait toutes les catégories (avec doublons)
                .collect(Collectors.toSet()); // 2. On les collecte dans un Set (les doublons disparaissent !)
    }
    
    // VERSION MODERNE 3.1 : LE STREAM (Map / Indexation)
    // Trouve un Livre Par Son Titre
    public Map<String, Livre> indexerLivresParTitreStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .collect(Collectors.toMap(
                    Livre::getTitre, 
                    l -> l
                ));
    }
    
    // VERSION MODERNE 3.2 : LE STREAM (Map / Transformation)
    // Transforme une List<Livre> en List<String>
    public List<String> obtenirSeulementLesTitres(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .map(l -> l.getTitre()) // Pour chaque livre l, on extrait son titre
                .toList();
    }
    
    // VERSION MODERNE : LE STREAM (Tri)
    // Trie par titre (A-Z) et renvoie une NOUVELLE liste (ne touche pas l'originale)
    public List<Livre> trierLivresParTitreStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .sorted(Comparator.comparing(Livre::getTitre))
                .toList();
    }
    
    // Trie par ID (Du Plus Recent au Plus Ancien) et renvoie une NOUVELLE liste (ne touche pas l'originale)
    public List<Livre> trierLivresParNumeroIDStream(List<Livre> tousLesLivres) {
        return tousLesLivres.stream()
                .sorted(Comparator.comparing(Livre::getId).reversed())
                .toList();
    }
    
// ============================================================================//
//  					  LAMBDA SEULE (SANS STREAM)                           //
//=============================================================================//

	/**
	* Supprime les livres d'une liste selon un mot-clé présent dans le titre.
	* Utilise la méthode removeIf() qui accepte une Lambda (Predicate).
	*/
	public void supprimerLivresParMotCle(List<Livre> livres, String motCle) {
	// La lambda 'l -> ...' définit la condition de suppression
	livres.removeIf(l -> l.getTitre().toLowerCase().contains(motCle.toLowerCase()));
	}
	
// ============================================================================//
//  					MULTI-THREADING AVEC SEMAPHORE                         //
//=============================================================================//

    // Nouvelle méthode pour simuler une consultation "lourde"
	public void consulterLivresSimultane(String nomUtilisateur) {
	    try {
	        System.out.println("👤 [" + nomUtilisateur + "] attend un accès à la base...");
	        
	        // --- DÉBUT DE LA ZONE PROTÉGÉE ---
	        semaphore.acquire(); 
	        
	        System.out.println("✅ [" + nomUtilisateur + "] ACCÈS ACCORDÉ !");
	        
	        // ACTION RÉELLE SUR LA BASE : On compte les livres
	        int nbLivres = livreDAO.listerTousLesLivres().size();
	        System.out.println("📊 [" + nomUtilisateur + "] Lecture DB en cours... Nombre de livres trouvés : " + nbLivres);

	        // On garde le sleep de 2s pour que TU puisses voir le blocage à l'écran
	        // sinon MySQL répond trop vite (0.001s) et on ne voit pas le Sémaphore agir.
	        Thread.sleep(10000); 
	        
	        System.out.println("📖 [" + nomUtilisateur + "] a fini sa lecture.");
	        
	    } catch (Exception e) {
	        System.err.println("❌ Erreur pour " + nomUtilisateur + " : " + e.getMessage());
	    } finally {
	        // --- FIN DE LA ZONE PROTÉGÉE ---
	        System.out.println("🔓 [" + nomUtilisateur + "] libère la connexion.");
	        semaphore.release();
	    }
	}
 
// ============================================================================//
//							MULTI-THREADING SIMPLE                             //
//=============================================================================//
    public void recupererTotalLivresLentement(String nomThread) {
        try {
            System.out.println("⏳ [" + nomThread + "] Connexion à la base de données...");
            
            // On simule une connexion qui prend 2 secondes
            Thread.sleep(2000); 
            
            // On fait une vraie action SQL
            int total = livreDAO.listerTousLesLivres().size();
            
            System.out.println("✅ [" + nomThread + "] Terminé ! Total trouvé : " + total + " livres.");
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }    
}
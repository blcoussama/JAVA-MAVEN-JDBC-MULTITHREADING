package com.bibliotech.services;

import com.bibliotech.dao.EmpruntDAO;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.dao.MembreDAO;
import com.bibliotech.models.Emprunt;
import com.bibliotech.models.Livre;
import com.bibliotech.models.Membre;
import com.bibliotech.models.Categorie;
import com.bibliotech.exceptions.DatabaseException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.IntSummaryStatistics;

/**
 * Service de calcul des statistiques de la bibliothèque
 *
 * UTILISE MASSIVEMENT L'API STREAM (TD4-5) :
 * - filter() : Filtrer emprunts/livres selon critères
 * - map() : Transformer objets en données
 * - sorted() : Trier par popularité, nombre d'emprunts
 * - limit() : Top 10 des livres
 * - collect() : Collecter résultats
 * - groupingBy() : Grouper par catégorie
 * - counting() : Compter emprunts par livre
 * - summarizingInt() : Statistiques sur disponibilité
 * - average() : Moyennes diverses
 *
 * CONCEPTS DÉMONTRÉS (TD4-5) :
 * ✅ Streams intermédiaires (filter, map, sorted, distinct, limit)
 * ✅ Streams terminales (collect, count, reduce, findFirst, anyMatch)
 * ✅ Collectors avancés (groupingBy, counting, summarizingInt)
 * ✅ Opérations numériques (mapToInt, sum, average, min, max)
 * ✅ Chaînage complexe de streams
 * ✅ Map.Entry pour manipulation de Maps
 * ✅ Comparator.comparing() avec lambda
 * ✅ Optional avec orElse()
 *
 * @author Belcadi Oussama
 * @version 1.0
 */
public class StatistiquesService {

    // ==================== ATTRIBUTS - DAO ====================

    private final EmpruntDAO empruntDAO;
    private final LivreDAO livreDAO;
    private final MembreDAO membreDAO;

    // ==================== CONSTRUCTEUR ====================

    /**
     * Constructeur avec injection des DAO
     */
    public StatistiquesService() {
        this.empruntDAO = new EmpruntDAO();
        this.livreDAO = new LivreDAO();
        this.membreDAO = new MembreDAO();
    }

    /**
     * Constructeur avec DAO personnalisés (pour tests)
     *
     * @param empruntDAO DAO des emprunts
     * @param livreDAO DAO des livres
     * @param membreDAO DAO des membres
     */
    public StatistiquesService(EmpruntDAO empruntDAO, LivreDAO livreDAO, MembreDAO membreDAO) {
        this.empruntDAO = empruntDAO;
        this.livreDAO = livreDAO;
        this.membreDAO = membreDAO;
    }

    // ==================== STATISTIQUES SUR LIVRES ====================

    /**
     * Récupère le Top 10 des livres les plus empruntés
     *
     * STREAM COMPLEXE :
     * 1. Récupérer tous les emprunts
     * 2. Grouper par livre et compter (groupingBy + counting)
     * 3. Trier par nombre d'emprunts décroissant (sorted + reversed)
     * 4. Limiter à 10 (limit)
     * 5. Collecter en liste (collect)
     *
     * CONCEPTS STREAMS :
     * ✅ groupingBy(Livre, counting)
     * ✅ Map.Entry.comparingByValue()
     * ✅ sorted() + reversed()
     * ✅ limit()
     *
     * @return Liste des 10 livres les plus empruntés avec leur nombre d'emprunts
     * @throws DatabaseException si erreur BD
     */
    public List<Map.Entry<Livre, Long>> top10LivresPlusEmpruntes() 
            throws DatabaseException {
        
        List<Emprunt> tousEmprunts = empruntDAO.findAll();

        // Grouper par livre et compter, puis trier et limiter
        return tousEmprunts.stream()
            // Grouper par livre (clé) et compter les emprunts (valeur)
            .collect(Collectors.groupingBy(
                Emprunt::getLivre,           // Clé : le livre
                Collectors.counting()         // Valeur : nombre d'emprunts
            ))
            .entrySet().stream()              // Transformer Map en Stream d'entries
            // Trier par valeur (nombre d'emprunts) décroissant
            .sorted(Map.Entry.<Livre, Long>comparingByValue().reversed())
            // Limiter aux 10 premiers
            .limit(10)
            // Collecter en liste
            .collect(Collectors.toList());
    }

    /**
     * Groupe les livres par catégorie
     *
     * STREAM SIMPLE :
     * - Récupérer tous les livres
     * - Grouper par catégorie
     *
     * CONCEPTS STREAMS :
     * ✅ groupingBy(Categorie)
     * ✅ Collectors.toList() implicite
     *
     * @return Map<Categorie, List<Livre>> - Livres groupés par catégorie
     * @throws DatabaseException si erreur BD
     */
    public Map<Categorie, List<Livre>> livresParCategorie() 
            throws DatabaseException {
        
        List<Livre> tousLivres = livreDAO.findAll();

        return tousLivres.stream()
            .collect(Collectors.groupingBy(Livre::getCategorie));
    }

    /**
     * Compte le nombre de livres par catégorie
     *
     * STREAM SIMPLE :
     * - Récupérer tous les livres
     * - Grouper par catégorie et compter
     *
     * CONCEPTS STREAMS :
     * ✅ groupingBy(Categorie, counting)
     *
     * @return Map<Categorie, Long> - Nombre de livres par catégorie
     * @throws DatabaseException si erreur BD
     */
    public Map<Categorie, Long> nombreLivresParCategorie() 
            throws DatabaseException {
        
        List<Livre> tousLivres = livreDAO.findAll();

        return tousLivres.stream()
            .collect(Collectors.groupingBy(
                Livre::getCategorie,
                Collectors.counting()
            ));
    }

    /**
     * Calcule des statistiques sur la disponibilité des livres
     *
     * STREAM NUMÉRIQUE :
     * - mapToInt() pour transformer en IntStream
     * - summarizingInt() pour obtenir statistiques complètes
     *
     * CONCEPTS STREAMS :
     * ✅ mapToInt()
     * ✅ summarizingInt()
     * ✅ IntSummaryStatistics (count, sum, min, max, average)
     *
     * Résultat contient :
     * - count : nombre total de livres
     * - sum : total d'exemplaires disponibles
     * - min : minimum d'exemplaires disponibles
     * - max : maximum d'exemplaires disponibles
     * - average : moyenne d'exemplaires disponibles
     *
     * @return IntSummaryStatistics avec toutes les statistiques
     * @throws DatabaseException si erreur BD
     */
    public IntSummaryStatistics statsDisponibilite() 
            throws DatabaseException {
        
        List<Livre> tousLivres = livreDAO.findAll();

        return tousLivres.stream()
            .collect(Collectors.summarizingInt(Livre::getDisponibles));
    }

    // ==================== STATISTIQUES SUR EMPRUNTS ====================

    /**
     * Calcule la moyenne d'emprunts actifs par membre
     *
     * STREAM NUMÉRIQUE :
     * - Récupérer tous les membres
     * - mapToInt() pour extraire nombre d'emprunts actifs
     * - average() pour calculer la moyenne
     *
     * CONCEPTS STREAMS :
     * ✅ mapToInt()
     * ✅ average()
     * ✅ OptionalDouble avec orElse()
     *
     * @return Moyenne d'emprunts actifs par membre (0.0 si aucun membre)
     * @throws DatabaseException si erreur BD
     */
    public double moyenneEmpruntsParMembre() 
            throws DatabaseException {
        
        List<Membre> tousMembres = membreDAO.findAll();

        // Si aucun membre, retourner 0
        if (tousMembres.isEmpty()) {
            return 0.0;
        }

        return tousMembres.stream()
            .mapToInt(Membre::getNombreEmpruntsActifs)
            .average()
            .orElse(0.0);  // Si OptionalDouble vide
    }

    /**
     * Calcule la durée moyenne des emprunts (en jours)
     *
     * STREAM NUMÉRIQUE :
     * - Récupérer tous les emprunts
     * - filter() pour ne garder que ceux avec date de retour effective
     * - mapToLong() pour calculer la durée de chaque emprunt
     * - average() pour la moyenne
     *
     * CONCEPTS STREAMS :
     * ✅ filter()
     * ✅ mapToLong()
     * ✅ average()
     * ✅ OptionalDouble avec orElse()
     * ✅ Méthode métier calculerDuree() de Emprunt
     *
     * @return Durée moyenne en jours (0.0 si aucun emprunt retourné)
     * @throws DatabaseException si erreur BD
     */
    public double dureeMoyenneEmprunts() 
            throws DatabaseException {
        
        List<Emprunt> tousEmprunts = empruntDAO.findAll();

        return tousEmprunts.stream()
            // Filtrer uniquement les emprunts retournés (avec date effective)
            .filter(e -> e.getDateRetourEffective() != null)
            // Calculer la durée de chaque emprunt
            .mapToLong(Emprunt::calculerDuree)
            // Calculer la moyenne
            .average()
            .orElse(0.0);
    }

    // ==================== STATISTIQUES SUR MEMBRES ====================

    /**
     * Récupère les membres ayant au moins un emprunt en retard
     *
     * STREAM COMPLEXE :
     * 1. Récupérer tous les membres
     * 2. filter() pour garder uniquement ceux avec emprunts en retard
     * 3. collect() en liste
     *
     * CONCEPTS STREAMS :
     * ✅ filter() avec condition complexe
     * ✅ anyMatch() sur sous-stream
     * ✅ Nested stream (stream dans stream)
     *
     * LOGIQUE :
     * - Pour chaque membre
     * - Vérifier s'il a AU MOINS UN emprunt en retard
     * - Utilise anyMatch() sur ses emprunts
     *
     * @return Liste des membres avec au moins un retard
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> getMembresEnRetard() 
            throws DatabaseException {
        
        List<Membre> tousMembres = membreDAO.findAll();

        return tousMembres.stream()
            .filter(membre -> {
                try {
                    // Récupérer les emprunts de ce membre
                    List<Emprunt> empruntsMembre = 
                        empruntDAO.findByMembre(membre.getIdMembre());
                    
                    // Vérifier si au moins un emprunt est en retard
                    return empruntsMembre.stream()
                        .anyMatch(Emprunt::estEnRetard);  // Méthode métier
                    
                } catch (DatabaseException e) {
                    // En cas d'erreur, on considère qu'il n'a pas de retard
                    return false;
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * Calcule le taux de retard (pourcentage d'emprunts en retard)
     *
     * STREAM NUMÉRIQUE :
     * - Récupérer tous les emprunts
     * - Compter le total
     * - filter() pour compter ceux en retard
     * - Calculer le pourcentage
     *
     * CONCEPTS STREAMS :
     * ✅ count()
     * ✅ filter()
     * ✅ Calcul mathématique sur résultats
     *
     * @return Taux de retard en pourcentage (0.0 si aucun emprunt)
     * @throws DatabaseException si erreur BD
     */
    public double tauxRetard() 
            throws DatabaseException {
        
        List<Emprunt> tousEmprunts = empruntDAO.findAll();

        // Si aucun emprunt, retourner 0%
        if (tousEmprunts.isEmpty()) {
            return 0.0;
        }

        long total = tousEmprunts.size();
        
        long enRetard = tousEmprunts.stream()
            .filter(Emprunt::estEnRetard)  // Méthode métier
            .count();

        // Calculer le pourcentage
        return (enRetard * 100.0) / total;
    }

    // ==================== STATISTIQUES COMBINÉES ====================

    /**
     * Récupère le top 5 des membres les plus actifs
     *
     * STREAM SIMPLE :
     * - Récupérer tous les membres
     * - sorted() par nombre d'emprunts actifs décroissant
     * - limit(5)
     *
     * CONCEPTS STREAMS :
     * ✅ Comparator.comparing()
     * ✅ reversed()
     * ✅ limit()
     *
     * @return Liste des 5 membres avec le plus d'emprunts actifs
     * @throws DatabaseException si erreur BD
     */
    public List<Membre> top5MembresActifs() 
            throws DatabaseException {
        
        List<Membre> tousMembres = membreDAO.findAll();

        return tousMembres.stream()
            .sorted(Comparator.comparing(Membre::getNombreEmpruntsActifs).reversed())
            .limit(5)
            .collect(Collectors.toList());
    }

    /**
     * Groupe les emprunts par catégorie de livre
     *
     * STREAM COMPLEXE :
     * - Récupérer tous les emprunts
     * - Grouper par catégorie du livre emprunté
     * - Compter les emprunts par catégorie
     *
     * CONCEPTS STREAMS :
     * ✅ groupingBy() avec navigation d'objets
     * ✅ counting()
     * ✅ Livre::getCategorie via getLivre()
     *
     * @return Map<Categorie, Long> - Nombre d'emprunts par catégorie
     * @throws DatabaseException si erreur BD
     */
    public Map<Categorie, Long> empruntsParCategorie() 
            throws DatabaseException {
        
        List<Emprunt> tousEmprunts = empruntDAO.findAll();

        return tousEmprunts.stream()
            .collect(Collectors.groupingBy(
                emprunt -> emprunt.getLivre().getCategorie(),  // Catégorie du livre
                Collectors.counting()                           // Nombre d'emprunts
            ));
    }

    /**
     * Calcule le total d'exemplaires disponibles dans la bibliothèque
     *
     * STREAM NUMÉRIQUE :
     * - mapToInt() pour extraire disponibles
     * - sum() pour additionner
     *
     * CONCEPTS STREAMS :
     * ✅ mapToInt()
     * ✅ sum()
     *
     * @return Total d'exemplaires disponibles
     * @throws DatabaseException si erreur BD
     */
    public int totalExemplairesDisponibles() 
            throws DatabaseException {
        
        List<Livre> tousLivres = livreDAO.findAll();

        return tousLivres.stream()
            .mapToInt(Livre::getDisponibles)
            .sum();
    }

    // ==================== GETTERS (pour tests) ====================

    public EmpruntDAO getEmpruntDAO() {
        return empruntDAO;
    }

    public LivreDAO getLivreDAO() {
        return livreDAO;
    }

    public MembreDAO getMembreDAO() {
        return membreDAO;
    }
}
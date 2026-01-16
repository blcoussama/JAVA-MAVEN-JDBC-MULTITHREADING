package com.bibliotech.main;
import java.util.ArrayList;
import java.util.List;
import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;

import com.bibliotech.exception.BibliothequeException;

public class TestAppLambda {
	public static void main(String args[]) throws BibliothequeException  {
		
		BibliothequeService service = new BibliothequeService();
		
		// --- SOURCE DE DONNÉES ---
        // On récupère une LIST car c'est l'ordre de la base de données qui compte.
        List<Livre> tousLesLivres = service.chargerTousLesLivres();
		
		// ---------------------------------------------------------------
        // TEST LAMBDA SEULE : removeIf
        // ---------------------------------------------------------------
        System.out.println("\n=== 🧪 TEST LAMBDA SEULE (removeIf) ===");
        
        // On crée une copie temporaire pour ne pas vider notre base de données locale
        List<Livre> listeAnettoyer = new ArrayList<>(tousLesLivres);
        System.out.println("Taille avant nettoyage : " + listeAnettoyer.size());

        // On donne l'ordre de supprimer tout ce qui contient "Java"
        String motASupprimer = "Java";
        service.supprimerLivresParMotCle(listeAnettoyer, motASupprimer);

        System.out.println("Après suppression des titres contenant '" + motASupprimer + "' :");
        System.out.println("Taille finale : " + listeAnettoyer.size());
        
        // On affiche les survivants pour vérifier
        listeAnettoyer.forEach(l -> System.out.println(" ✅ Rescapé : " + l.getTitre()));
	}
	
}

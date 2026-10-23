package com.bibliotech.demo;

import com.bibliotech.model.Livre;
import com.bibliotech.service.BibliothequeService;
import java.util.ArrayList;
import java.util.List;

/** Lambda sans Stream : List.removeIf reçoit un Predicate écrit sous forme de lambda. */
public class DemoLambda {

    public static void main(String[] args) {
        BibliothequeService service = DemoSupport.service();
        // Copie : on ne modifie que la liste en mémoire, jamais la base.
        List<Livre> copie = new ArrayList<>(DemoSupport.chargerLivres(service));

        System.out.println("=== Lambda seule : removeIf ===");
        System.out.println("Taille avant : " + copie.size());
        String motCle = "Java";
        boolean modifiee = service.supprimerLivresParMotCle(copie, motCle);
        System.out.println("Titres contenant '" + motCle + "' retirés : " + (modifiee ? "oui" : "aucun"));
        System.out.println("Taille après : " + copie.size());
        copie.forEach(l -> System.out.println("  conservé : " + l.getTitre()));
    }
}

package com.bergerie.util;

import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;

import java.util.Comparator;
import java.util.List;

/**
 * Plafond d'affichage : l'interface présente au plus {@value #MAX} alertes et {@value #MAX} animaux.
 * On garde les alertes les plus récentes et les animaux ajoutés le plus récemment, afin qu'un nouvel animal
 * apparaisse tout de suite dans la liste.
 */
public final class Affichage {

    public static final int MAX = 5;

    private Affichage() {
    }

    public static List<Animal> animaux(List<Animal> animaux) {
        return animaux.stream()
                .sorted(Comparator.comparing(Animal::getId).reversed())
                .limit(MAX)
                .sorted(Comparator.comparing(Animal::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    public static List<Alerte> alertes(List<Alerte> alertes) {
        return alertes.stream()
                .sorted(Comparator.comparing(Alerte::getDateHeure, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(MAX)
                .toList();
    }
}

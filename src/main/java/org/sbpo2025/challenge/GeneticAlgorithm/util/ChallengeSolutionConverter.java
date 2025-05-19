package org.sbpo2025.challenge.GeneticAlgorithm.util;

import org.sbpo2025.challenge.ChallengeSolution;
import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import java.util.Set;
import java.util.HashSet;

public class ChallengeSolutionConverter {
    public static ChallengeSolution convert(Individual individual) {
        if (individual == null) {
            System.err.println("Tentativa de converter um Individual nulo. Retornando solução vazia.");
            return new ChallengeSolution(new HashSet<>(), new HashSet<>());
        }

        Set<Integer> selectedOrders = new HashSet<>();
        boolean[] orderGenes = individual.getOrderGenes();
        for (int i = 0; i < orderGenes.length; i++) {
            if (orderGenes[i]) {
                selectedOrders.add(i);
            }
        }

        Set<Integer> visitedAisles = new HashSet<>();
        boolean[] aisleGenes = individual.getAisleGenes();
        for (int i = 0; i < aisleGenes.length; i++) {
            if (aisleGenes[i]) {
                visitedAisles.add(i);
            }
        }

        // Opcional: Adicionar log se a solução for vazia, como estava no GeneticAlgorithmExecutor
        if (selectedOrders.isEmpty() && visitedAisles.isEmpty()) {
             System.err.println("Atenção: Conversão de Individual para ChallengeSolution resultou em pedidos e corredores vazios.");
        }
        return new ChallengeSolution(selectedOrders, visitedAisles);
    }
}

package org.sbpo2025.challenge.geneticAlgorithm.mutation.binary;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.real.MutationOperator;

import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Operador de mutação Scramble Mutation.
 * Seleciona um segmento contínuo de genes e embaralha sua ordem interna.
 */
public class ScrambleMutationOperator implements MutationOperator {
    private final Random random;

    public ScrambleMutationOperator(Random random) {
        this.random = random;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        int n = genes.length;
        if (n < 2) return;
        if (random.nextDouble() < mutationRate) {
            int start = random.nextInt(n - 1);
            int end = start + 1 + random.nextInt(n - start);
            // Copia o segmento
            List<Boolean> segment = new ArrayList<>();
            for (int i = start; i < end; i++) {
                segment.add(genes[i]);
            }
            // Embaralha
            Collections.shuffle(segment, random);
            // Reinsere o segmento embaralhado
            for (int i = start; i < end; i++) {
                genes[i] = segment.get(i - start);
                individual.setGene(i, genes[i]);
            }
        }
    }
}

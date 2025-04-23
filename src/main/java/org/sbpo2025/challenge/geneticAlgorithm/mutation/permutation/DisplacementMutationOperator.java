package org.sbpo2025.challenge.geneticAlgorithm.mutation.permutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.real.MutationOperator;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * Operador de mutação Displacement Mutation.
 * Extrai um bloco de genes e desloca para outra posição, preservando ordem interna.
 */
public class DisplacementMutationOperator implements MutationOperator {
    private final Random random;

    public DisplacementMutationOperator(Random random) {
        this.random = random;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        int n = genes.length;
        if (n < 2) return;
        if (random.nextDouble() < mutationRate) {
            int start = random.nextInt(n);
            int end = start + 1 + random.nextInt(n - start);
            // Copia lista de genes
            List<Boolean> list = new ArrayList<>(n);
            for (boolean g : genes) list.add(g);
            // Extrai bloco
            List<Boolean> block = new ArrayList<>(list.subList(start, end));
            list.subList(start, end).clear();
            // Insere bloco em nova posição
            int insertPos = random.nextInt(list.size() + 1);
            list.addAll(insertPos, block);
            // Atualiza genes no indivíduo
            for (int i = 0; i < n; i++) {
                boolean val = list.get(i);
                individual.setGene(i, val);
                genes[i] = val;
            }
        }
    }
}

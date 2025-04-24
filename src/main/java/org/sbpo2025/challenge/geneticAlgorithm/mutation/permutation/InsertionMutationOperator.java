package org.sbpo2025.challenge.geneticAlgorithm.mutation.permutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.MutationOperator;

import java.util.Random;

/**
 * Operador de mutação Insertion Mutation.
 * Remove um gene de uma posição e o insere em outra, deslocando o segmento.
 */
public class InsertionMutationOperator implements MutationOperator {
    private final Random random;

    public InsertionMutationOperator(Random random) {
        this.random = random;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        int n = genes.length;
        if (n < 2) return;
        if (random.nextDouble() < mutationRate) {
            int i = random.nextInt(n);
            int j = random.nextInt(n);
            if (i != j) {
                boolean val = genes[i];
                if (i < j) {
                    // desloca à esquerda o segmento (i+1 .. j)
                    System.arraycopy(genes, i + 1, genes, i, j - i);
                } else {
                    // desloca à direita o segmento (j .. i-1)
                    System.arraycopy(genes, j, genes, j + 1, i - j);
                }
                genes[j] = val;
                // Atualiza seleção no indivíduo para índices afetados
                int start = Math.min(i, j);
                int end = Math.max(i, j);
                for (int k = start; k <= end; k++) {
                    individual.setGene(k, genes[k]);
                }
            }
        }
    }
}

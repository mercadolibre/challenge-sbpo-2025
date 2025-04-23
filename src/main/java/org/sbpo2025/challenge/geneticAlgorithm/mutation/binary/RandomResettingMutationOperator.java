package org.sbpo2025.challenge.geneticAlgorithm.mutation.binary;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.real.MutationOperator;

import java.util.Random;

/**
 * Operador de mutação Random Resetting.
 * Para genes booleanos, faz bit-flip. Para inteiros, sorteia novo valor diferente do atual.
 */
public class RandomResettingMutationOperator implements MutationOperator {
    private final Random random;
    private final int[] lowerBounds;
    private final int[] upperBounds;

    /**
     * Construtor para genes binários (bit-flip)
     * @param random gerador de números aleatórios
     */
    public RandomResettingMutationOperator(Random random) {
        this.random = random;
        this.lowerBounds = null;
        this.upperBounds = null;
    }

    /**
     * Construtor para genes inteiros
     * @param random gerador de números aleatórios
     * @param lowerBounds limites inferiores de cada gene
     * @param upperBounds limites superiores de cada gene
     */
    public RandomResettingMutationOperator(Random random, int[] lowerBounds, int[] upperBounds) {
        this.random = random;
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        // Binário: bit-flip
        if (lowerBounds == null || upperBounds == null) {
            for (int i = 0; i < genes.length; i++) {
                if (random.nextDouble() < mutationRate) {
                    genes[i] = !genes[i];
                    individual.setGene(i, genes[i]);
                }
            }
        } else {
            // Inteiro: sorteia novo valor diferente do atual
            int[] intGenes = individual.getIntGenes(); // Supondo método para genes inteiros
            for (int i = 0; i < intGenes.length; i++) {
                if (random.nextDouble() < mutationRate) {
                    int current = intGenes[i];
                    int lb = lowerBounds[i];
                    int ub = upperBounds[i];
                    int newValue = current;
                    if (ub > lb) {
                        do {
                            newValue = lb + random.nextInt(ub - lb + 1);
                        } while (newValue == current && ub - lb > 0);
                        intGenes[i] = newValue;
                        individual.setIntGene(i, newValue); // Supondo método para setar gene inteiro
                    }
                }
            }
        }
    }
}

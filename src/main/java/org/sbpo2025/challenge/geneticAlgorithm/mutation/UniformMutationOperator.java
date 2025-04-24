package org.sbpo2025.challenge.geneticAlgorithm.mutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.MutationOperator;

import java.util.Random;

/**
 * Operador de mutação Uniform Mutation.
 * Substitui o valor de um gene por um novo valor amostrado uniformemente no intervalo permitido.
 */
public class UniformMutationOperator implements MutationOperator {
    private final Random random;
    private final double[] lowerBounds;
    private final double[] upperBounds;

    /**
     * Construtor para genes contínuos
     * @param random gerador de números aleatórios
     * @param lowerBounds limites inferiores de cada gene
     * @param upperBounds limites superiores de cada gene
     */
    public UniformMutationOperator(Random random, double[] lowerBounds, double[] upperBounds) {
        this.random = random;
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        double[] genes = individual.getDoubleGenes(); // Supondo método para genes contínuos
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationRate) {
                double lb = lowerBounds[i];
                double ub = upperBounds[i];
                double newValue = lb + random.nextDouble() * (ub - lb);
                genes[i] = newValue;
                individual.setDoubleGene(i, newValue); // Supondo método para setar gene contínuo
            }
        }
    }
}

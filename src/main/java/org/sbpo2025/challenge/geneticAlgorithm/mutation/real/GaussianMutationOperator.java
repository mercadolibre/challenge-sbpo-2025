package org.sbpo2025.challenge.geneticAlgorithm.mutation.real;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.MutationOperator;

import java.util.Random;

/**
 * Operador de mutação Gaussiana para genes contínuos.
 * Adiciona ruído gaussiano (média zero, variância sigma^2) a cada gene.
 */
public class GaussianMutationOperator implements MutationOperator {
    private final Random random;
    private final double sigma;

    public GaussianMutationOperator(Random random, double sigma) {
        this.random = random;
        this.sigma = sigma;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        double[] genes = individual.getDoubleGenes(); // Supondo método para genes contínuos
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationRate) {
                double noise = random.nextGaussian() * sigma;
                genes[i] += noise;
                individual.setDoubleGene(i, genes[i]); // Supondo método para setar gene contínuo
            }
        }
    }
}

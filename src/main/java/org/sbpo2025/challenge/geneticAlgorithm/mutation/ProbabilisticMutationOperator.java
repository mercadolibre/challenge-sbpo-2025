package org.sbpo2025.challenge.geneticAlgorithm.mutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.AliasSampler;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Operador probabilístico de mutação: escolhe um operador de mutação com base em probabilidades.
 */
public class ProbabilisticMutationOperator implements MutationOperator {
    private final MutationOperator[] operators;
    private final AliasSampler aliasSampler;
    private final double[] probabilities;
    private final int[] successCounts;
    private final int[] attemptCounts;
    private static final double MIN_PROB = 0.05;

    public ProbabilisticMutationOperator(MutationOperator[] operators, double[] probabilities, java.util.Random ignoredRandom) {
        if (operators == null || probabilities == null || operators.length != probabilities.length) {
            throw new IllegalArgumentException("Operators and probabilities must be non-null and of same length");
        }
        // Defensive copy para imutabilidade
        this.operators = Arrays.copyOf(removeDuplicates(operators), operators.length);
        this.probabilities = Arrays.copyOf(probabilities, probabilities.length);
        this.successCounts = new int[operators.length];
        this.attemptCounts = new int[operators.length];
        this.aliasSampler = new AliasSampler(Arrays.copyOf(probabilities, probabilities.length), ThreadLocalRandom.current());
    }

    // Remove duplicatas de operadores pelo equals/hashCode
    private static MutationOperator[] removeDuplicates(MutationOperator[] ops) {
        LinkedHashSet<MutationOperator> set = new LinkedHashSet<>(Arrays.asList(ops));
        return set.toArray(new MutationOperator[0]);
    }

    /**
     * Realiza mutação e retorna o índice do operador utilizado.
     */
    public int mutateWithOperatorIndex(Individual individual, double mutationRate) {
        int idx = aliasSampler.sample();
        attemptCounts[idx]++;
        operators[idx].mutate(individual, mutationRate);
        return idx;
    }

    /**
     * Registra sucesso para o operador idx.
     */
    public void registerSuccess(int idx) {
        if (idx >= 0 && idx < successCounts.length) {
            successCounts[idx]++;
        }
    }

    /**
     * Atualiza as probabilidades proporcionalmente ao sucesso recente.
     */
    public void updateProbabilities() {
        double[] newProbs = new double[probabilities.length];
        double total = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            newProbs[i] = successCounts[i] + 1e-3;
            total += newProbs[i];
        }
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = Math.max(newProbs[i] / total, MIN_PROB);
        }
        double sum = Arrays.stream(probabilities).sum();
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= sum;
        }
        // Atualiza sampler
        this.aliasSampler.updateProbabilities(probabilities);
        Arrays.fill(successCounts, 0);
        Arrays.fill(attemptCounts, 0);
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        mutateWithOperatorIndex(individual, mutationRate);
    }
}

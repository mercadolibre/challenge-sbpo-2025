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

    public ProbabilisticMutationOperator(MutationOperator[] operators, double[] probabilities, java.util.Random ignoredRandom) {
        if (operators == null || probabilities == null || operators.length != probabilities.length) {
            throw new IllegalArgumentException("Operators and probabilities must be non-null and of same length");
        }
        // Defensive copy para imutabilidade
        this.operators = Arrays.copyOf(removeDuplicates(operators), operators.length);
        this.aliasSampler = new AliasSampler(Arrays.copyOf(probabilities, probabilities.length), ThreadLocalRandom.current());
    }

    // Remove duplicatas de operadores pelo equals/hashCode
    private static MutationOperator[] removeDuplicates(MutationOperator[] ops) {
        LinkedHashSet<MutationOperator> set = new LinkedHashSet<>(Arrays.asList(ops));
        return set.toArray(new MutationOperator[0]);
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        int idx = aliasSampler.sample();
        operators[idx].mutate(individual, mutationRate);
    }
}

package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import org.sbpo2025.challenge.GeneticAlgorithm.config.GAConfiguration;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class TournamentSelection implements SelectionOperator {
    private final GAConfiguration gaConfiguration;
    private final Random random;

    public TournamentSelection(GAConfiguration gaConfiguration, Random random) {
        this.gaConfiguration = gaConfiguration;
        this.random = random;
    }

    @Override
    public Individual select(List<Individual> population, double[] fitnesses) {
        if (population.isEmpty() || fitnesses == null || fitnesses.length != population.size()) return null;
        int k = gaConfiguration.getTournamentSize();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            indices.add(random.nextInt(population.size()));
        }
        int bestIdx = indices.get(0);
        for (int i = 1; i < indices.size(); i++) {
            int idx = indices.get(i);
            if (fitnesses[idx] > fitnesses[bestIdx]) {
                bestIdx = idx;
            }
        }
        return population.get(bestIdx).clone();
    }
}

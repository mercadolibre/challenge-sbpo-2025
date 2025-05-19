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
    public Individual select(List<Individual> population) {
        // TODO: Implementar Tournament Selection (k é gaConfiguration.getTournamentSize())
        // Exemplo simples: selecionar o primeiro (placeholder)
        if (population.isEmpty()) return null;

        List<Individual> tournamentContestants = new ArrayList<>();
        for (int i = 0; i < gaConfiguration.getTournamentSize(); i++) {
            tournamentContestants.add(population.get(random.nextInt(population.size())));
        }

        Individual bestInTournament = tournamentContestants.get(0);
        for (int i = 1; i < tournamentContestants.size(); i++) {
            if (tournamentContestants.get(i).getFitness() > bestInTournament.getFitness()) {
                bestInTournament = tournamentContestants.get(i);
            }
        }
        return bestInTournament.clone(); // Retornar clone
    }
}

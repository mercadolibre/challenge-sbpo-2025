package org.sbpo2025.challenge.GeneticAlgorithm.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import org.sbpo2025.challenge.ChallengeSolution;
import org.sbpo2025.challenge.GeneticAlgorithm.config.GAConfiguration;
import org.sbpo2025.challenge.GeneticAlgorithm.evaluator.FitnessEvaluator;
import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;

public class GeneticAlgorithmExecutor {

    // Dados do problema
    private final List<Map<Integer, Integer>> orders;
    private final List<Map<Integer, Integer>> aisles;
    private final int nItems;
    private final int waveSizeLB;
    private final int waveSizeUB;
    private final int numOrders;
    private final int numAisles;

    // Configuração do GA
    private final GAConfiguration gaConfiguration;

    // Avaliador de Fitness
    private final FitnessEvaluator fitnessEvaluator;

    // População
    private List<Individual> population;

    // Utilitários
    private final Random random;

    public GeneticAlgorithmExecutor(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB,
            GAConfiguration gaConfiguration) {
        this.orders = orders;
        this.aisles = aisles;
        this.nItems = nItems;
        this.waveSizeLB = waveSizeLB;
        this.waveSizeUB = waveSizeUB;
        this.numOrders = orders.size();
        this.numAisles = aisles.size();

        this.gaConfiguration = gaConfiguration;
        this.fitnessEvaluator = new FitnessEvaluator(orders, aisles, nItems, waveSizeLB, waveSizeUB, gaConfiguration);

        this.population = new ArrayList<>(gaConfiguration.getPopulationSize());
        this.random = new Random();
    }

    public ChallengeSolution run() {
        evolve();
        Individual bestIndividual = findBestIndividual();
        return convertToChallengeSolution(bestIndividual);
    }

    private void evolve() {
        initializePopulation();
        evaluatePopulation();

        for (int generation = 0; generation < gaConfiguration.getNumberOfGenerations(); generation++) {
            List<Individual> nextGeneration = new ArrayList<>(gaConfiguration.getPopulationSize());

            while (nextGeneration.size() < gaConfiguration.getPopulationSize()) {
                Individual parent1 = selectParent();
                Individual parent2 = selectParent();

                Individual offspring1, offspring2;

                if (random.nextDouble() < gaConfiguration.getCrossoverRate()) {
                    Individual[] children = crossover(parent1, parent2);
                    offspring1 = children[0];
                    offspring2 = children[1];
                } else {
                    offspring1 = parent1.clone();
                    offspring2 = parent2.clone();
                }

                mutate(offspring1);
                mutate(offspring2);

                repair(offspring1);
                repair(offspring2);

                this.fitnessEvaluator.calculateFitness(offspring1);
                this.fitnessEvaluator.calculateFitness(offspring2);

                nextGeneration.add(offspring1);
                if (nextGeneration.size() < gaConfiguration.getPopulationSize()) {
                    nextGeneration.add(offspring2);
                }
            }
            population = nextGeneration;
            // logGenerationProgress(generation);
        }
    }

    private void initializePopulation() {
        for (int i = 0; i < gaConfiguration.getPopulationSize(); i++) {
            Individual individual = new Individual(numOrders, numAisles);
            // TODO: Implementar lógica da Seção 3: População Inicial
            this.fitnessEvaluator.calculateFitness(individual);
            population.add(individual);
        }
    }

    private void evaluatePopulation() {
        for (Individual individual : population) {
            this.fitnessEvaluator.calculateFitness(individual);
        }
    }

    private Individual selectParent() {
        // TODO: Implementar seleção por torneio (usando gaConfiguration.getTournamentSize())
        if (population.isEmpty()) {
            throw new IllegalStateException("A população está vazia, não é possível selecionar pais.");
        }
        return population.get(random.nextInt(gaConfiguration.getPopulationSize())).clone();
    }

    private Individual[] crossover(Individual parent1, Individual parent2) {
        Individual offspring1 = parent1.clone();
        Individual offspring2 = parent2.clone();

        for (int i = 0; i < numOrders; i++) {
            if (random.nextDouble() < 0.5) {
                offspring1.getOrderGenes()[i] = parent2.getOrderGenes()[i];
                offspring2.getOrderGenes()[i] = parent1.getOrderGenes()[i];
            }
        }
        for (int i = 0; i < numAisles; i++) {
            if (random.nextDouble() < 0.5) {
                offspring1.getAisleGenes()[i] = parent2.getAisleGenes()[i];
                offspring2.getAisleGenes()[i] = parent1.getAisleGenes()[i];
            }
        }
        return new Individual[]{offspring1, offspring2};
    }

    private void mutate(Individual individual) {
        for (int i = 0; i < numOrders; i++) {
            if (random.nextDouble() < gaConfiguration.getMutationRate()) {
                boolean[] genes = individual.getOrderGenes();
                genes[i] = !genes[i];
                individual.setOrderGenes(genes);
            }
        }
        for (int i = 0; i < numAisles; i++) {
            if (random.nextDouble() < gaConfiguration.getMutationRate()) {
                boolean[] genes = individual.getAisleGenes();
                genes[i] = !genes[i];
                individual.setAisleGenes(genes);
            }
        }
    }

    private void repair(Individual individual) {
        // TODO: Implementar lógica da Seção 5: Repair
    }

    private Individual findBestIndividual() {
        if (population.isEmpty()) return null;
        Individual best = population.get(0);
        for (int i = 1; i < population.size(); i++) {
            if (population.get(i).getFitness() > best.getFitness()) {
                best = population.get(i);
            }
        }
        return best != null ? best.clone() : null;
    }

    private ChallengeSolution convertToChallengeSolution(Individual individual) {
        if (individual == null) {
            System.err.println("Tentativa de converter um Individual nulo para ChallengeSolution. Retornando solução vazia.");
            return new ChallengeSolution(new HashSet<>(), new HashSet<>());
        }
        Set<Integer> selectedOrders = new HashSet<>();
        for(int i=0; i<individual.getOrderGenes().length; i++) {
            if(individual.getOrderGenes()[i]) selectedOrders.add(i);
        }
        Set<Integer> visitedAisles = new HashSet<>();
        for(int i=0; i<individual.getAisleGenes().length; i++) {
            if(individual.getAisleGenes()[i]) visitedAisles.add(i);
        }
        if (selectedOrders.isEmpty() && visitedAisles.isEmpty()) {
             System.err.println("Atenção: Convertendo Individual para ChallengeSolution resultou em pedidos e corredores vazios.");
        }
        return new ChallengeSolution(selectedOrders, visitedAisles);
    }
}

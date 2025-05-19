package org.sbpo2025.challenge.GeneticAlgorithm.runner;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.sbpo2025.challenge.ChallengeSolution;
import org.sbpo2025.challenge.GeneticAlgorithm.config.GAConfiguration;
import org.sbpo2025.challenge.GeneticAlgorithm.evaluator.FitnessEvaluator;
import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import org.sbpo2025.challenge.GeneticAlgorithm.operators.SelectionOperator;
import org.sbpo2025.challenge.GeneticAlgorithm.operators.CrossoverOperator;
import org.sbpo2025.challenge.GeneticAlgorithm.operators.MutationOperator;
import org.sbpo2025.challenge.GeneticAlgorithm.operators.RepairOperator;
import org.sbpo2025.challenge.GeneticAlgorithm.population.PopulationManager;
import org.sbpo2025.challenge.GeneticAlgorithm.util.ChallengeSolutionConverter;

public class GeneticAlgorithmExecutor {
    private final PopulationManager populationManager;
    private final SelectionOperator selectionOperator;
    private final CrossoverOperator crossoverOperator;
    private final MutationOperator mutationOperator;
    private final RepairOperator repairOperator;
    private List<Individual> population;
    private final Random random;
    private final int numOrders;
    private final int numAisles;
    private final GAConfiguration gaConfiguration;
    private final FitnessEvaluator fitnessEvaluator;

    public GeneticAlgorithmExecutor(
            PopulationManager populationManager,
            SelectionOperator selectionOperator,
            CrossoverOperator crossoverOperator,
            MutationOperator mutationOperator,
            RepairOperator repairOperator,
            GAConfiguration gaConfiguration,
            FitnessEvaluator fitnessEvaluator,
            Random random) {
        this.populationManager = populationManager;
        this.selectionOperator = selectionOperator;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.repairOperator = repairOperator;
        this.gaConfiguration = gaConfiguration;
        this.fitnessEvaluator = fitnessEvaluator;
        this.random = random;
        this.numOrders = populationManager == null ? 0 : populationManager.initializePopulation().get(0).getOrderGenes().length;
        this.numAisles = populationManager == null ? 0 : populationManager.initializePopulation().get(0).getAisleGenes().length;
    }

    public ChallengeSolution run() {
        evolve();
        Individual bestIndividual = populationManager.findBest(population);
        return ChallengeSolutionConverter.convert(bestIndividual);
    }

    private void evolve() {
        population = populationManager.initializePopulation();
        int popSize = gaConfiguration.getPopulationSize();
        int generations = gaConfiguration.getNumberOfGenerations();
        for (int generation = 0; generation < generations; generation++) {
            List<Individual> offspring = new java.util.ArrayList<>(popSize);
            double[] fitnesses = new double[population.size()];
            for (int i = 0; i < population.size(); i++) {
                fitnesses[i] = population.get(i).getFitness();
            }
            while (offspring.size() < popSize) {
                Individual parent1 = selectionOperator.select(population, fitnesses);
                Individual parent2 = selectionOperator.select(population, fitnesses);
                Individual[] children;
                if (random.nextDouble() < gaConfiguration.getCrossoverRate()) {
                    children = crossoverOperator.crossover(parent1, parent2);
                } else {
                    children = new Individual[]{parent1.clone(), parent2.clone()};
                }
                for (Individual child : children) {
                    mutationOperator.mutate(child);
                    repairOperator.repair(child, populationManager.ordersData, populationManager.aislesData, populationManager.waveSizeLB, populationManager.waveSizeUB, populationManager.nItems);
                    fitnessEvaluator.evaluate(child, populationManager.ordersData, populationManager.aislesData, populationManager.waveSizeLB, populationManager.waveSizeUB);
                    offspring.add(child);
                    if (offspring.size() >= popSize) break;
                }
            }
            population = populationManager.selectNextGeneration(population, offspring);
        }
    }
}

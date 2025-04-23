package org.sbpo2025.challenge.geneticAlgorithm;

import org.apache.commons.lang3.time.StopWatch;
import org.sbpo2025.challenge.ChallengeSolution;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.binary.*;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.real.*;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.permutation.*;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.UniformMutationOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.CrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.OnePointCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.ProbabilisticCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.TwoPointCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.UniformCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.HUXCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.SegmentCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.ShuffleExchangeCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.SetBasedCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.crossover.GreedyHeuristicCrossoverOperator;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.permutation.InsertionMutationOperator;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.permutation.DisplacementMutationOperator;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.real.GaussianMutationOperator;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Algoritmo Genético para resolver o problema de wave-selection do SBPO 2025.
 */
public class GeneticAlgorithm {
    // Configurações do algoritmo genético
    private final int populationSize;
    private final int maxGenerations;
    private final int noImprovementLimit;
    private final double crossoverRate;
    private final double mutationRate;
    private final int elitismCount;
    private final long maxRuntimeMillis;

    private final double mutationRateMin = 0.001; // Valor mínimo da taxa de mutação para controle determinístico
    private final double mutationRateInicial; // Guarda o valor inicial
    private double mutationRateAdaptativa = 0.05; // Valor inicial adaptativo
    private final double mutationRateMax = 0.15; // Limite superior adaptativo
    private final double improvementThresholdUp = 0.01; // Threshold para diminuir mutação
    private final double improvementThresholdDown = 0.001; // Threshold para aumentar mutação
    private final double mutationStep = 0.01; // Passo de ajuste

    // Dados do problema
    private final List<Map<Integer, Integer>> orders;
    private final List<Map<Integer, Integer>> aisles;
    private final int numItems;
    private final int waveSizeLB;
    private final int waveSizeUB;

    // Estado do algoritmo
    private List<Individual> population;
    private Random random;
    private Individual bestIndividual;
    private int generationsWithoutImprovement;
    private double bestFitness;

    private MutationOperator mutationOperator;
    private CrossoverOperator crossoverOperator;

    /**
     * Construtor do algoritmo genético
     * @param orders Lista de pedidos (mapa de item para quantidade)
     * @param aisles Lista de corredores (mapa de item para capacidade)
     * @param numItems Número total de itens diferentes
     * @param waveSizeLB Limite inferior da wave
     * @param waveSizeUB Limite superior da wave
     * @param populationSize Tamanho da população
     * @param maxGenerations Número máximo de gerações
     * @param noImprovementLimit Limite de gerações sem melhoria para parar
     * @param crossoverRate Taxa de crossover
     * @param mutationRate Taxa de mutação
     * @param elitismCount Número de indivíduos elite preservados entre gerações
     * @param maxRuntimeMillis Tempo máximo de execução em milissegundos
     */
    public GeneticAlgorithm(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int numItems,
            int waveSizeLB,
            int waveSizeUB,
            int populationSize,
            int maxGenerations,
            int noImprovementLimit,
            double crossoverRate,
            double mutationRate,
            int elitismCount,
            long maxRuntimeMillis) {

        this.orders = orders;
        this.aisles = aisles;
        this.numItems = numItems;
        this.waveSizeLB = waveSizeLB;
        this.waveSizeUB = waveSizeUB;

        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.noImprovementLimit = noImprovementLimit;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        this.maxRuntimeMillis = maxRuntimeMillis;

        this.mutationRateInicial = mutationRate;

        this.random = new Random();
        this.population = new ArrayList<>();
        this.bestIndividual = null;
        this.generationsWithoutImprovement = 0;
        this.bestFitness = 0.0;

        // Exemplo de limites para UniformMutationOperator (ajuste conforme necessário)
        double[] lowerBounds = new double[orders.size()];
        double[] upperBounds = new double[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            lowerBounds[i] = 0.0;
            upperBounds[i] = 1.0;
        }
        this.mutationOperator = new ProbabilisticMutationOperator(
            new MutationOperator[] {
                new BitFlipMutationOperator(this.random),
                new SwapMutationOperator(this.random),
                new ScrambleMutationOperator(this.random),
                new InversionMutationOperator(this.random),
                new DisplacementMutationOperator(this.random),
                new UniformMutationOperator(this.random, lowerBounds, upperBounds),
                new InsertionMutationOperator(this.random),
                new org.sbpo2025.challenge.geneticAlgorithm.mutation.permutation.DisplacementMutationOperator(this.random),
                new GaussianMutationOperator(this.random, 0.1) // sigma fictício, ajuste conforme necessário
            },
            new double[] {0.12, 0.12, 0.12, 0.12, 0.12, 0.12, 0.10, 0.10, 0.08},
            this.random
        );

        this.crossoverOperator = new ProbabilisticCrossoverOperator(
            new CrossoverOperator[] {
                new OnePointCrossoverOperator(this.random),
                new TwoPointCrossoverOperator(this.random),
                new UniformCrossoverOperator(this.random),
                new HUXCrossoverOperator(this.random),
                new SegmentCrossoverOperator(this.random, 5), // Exemplo: segmento de tamanho 5
                new ShuffleExchangeCrossoverOperator(this.random),
                new SetBasedCrossoverOperator(this.random, waveSizeLB, waveSizeUB, orders, aisles),
                new GreedyHeuristicCrossoverOperator(this.random, waveSizeLB, waveSizeUB, orders, aisles)
            },
            new double[] {0.13, 0.13, 0.13, 0.13, 0.12, 0.12, 0.12, 0.12}, // Ajuste as probabilidades conforme desejado
            this.random
        );
    }

    public GeneticAlgorithm(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int numItems,
            int waveSizeLB,
            int waveSizeUB,
            int populationSize,
            int maxGenerations,
            int noImprovementLimit,
            double crossoverRate,
            double mutationRate,
            int elitismCount,
            long maxRuntimeMillis,
            MutationOperator mutationOperator,
            CrossoverOperator crossoverOperator) {

        this.orders = orders;
        this.aisles = aisles;
        this.numItems = numItems;
        this.waveSizeLB = waveSizeLB;
        this.waveSizeUB = waveSizeUB;

        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.noImprovementLimit = noImprovementLimit;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        this.maxRuntimeMillis = maxRuntimeMillis;

        this.mutationRateInicial = mutationRate;

        this.random = new Random();
        this.population = new ArrayList<>();
        this.bestIndividual = null;
        this.generationsWithoutImprovement = 0;
        this.bestFitness = 0.0;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
    }

    // Novo construtor privado para uso do builder
    private GeneticAlgorithm(Builder builder) {
        this.orders = builder.orders;
        this.aisles = builder.aisles;
        this.numItems = builder.numItems;
        this.waveSizeLB = builder.waveSizeLB;
        this.waveSizeUB = builder.waveSizeUB;
        this.populationSize = builder.populationSize;
        this.maxGenerations = builder.maxGenerations;
        this.noImprovementLimit = builder.noImprovementLimit;
        this.crossoverRate = builder.crossoverRate;
        this.mutationRate = builder.mutationRate;
        this.elitismCount = builder.elitismCount;
        this.maxRuntimeMillis = builder.maxRuntimeMillis;
        this.random = new Random();
        this.population = new ArrayList<>();
        this.bestIndividual = null;
        this.generationsWithoutImprovement = 0;
        this.bestFitness = 0.0;
        this.mutationRateInicial = builder.mutationRate;
        this.mutationOperator = builder.mutationOperator != null ? builder.mutationOperator : new BitFlipMutationOperator(this.random);
        this.crossoverOperator = builder.crossoverOperator != null ? builder.crossoverOperator : new ProbabilisticCrossoverOperator(
            new CrossoverOperator[] {
                new OnePointCrossoverOperator(this.random),
                new TwoPointCrossoverOperator(this.random),
                new UniformCrossoverOperator(this.random),
                new HUXCrossoverOperator(this.random),
                new SegmentCrossoverOperator(this.random, 5),
                new ShuffleExchangeCrossoverOperator(this.random),
                new SetBasedCrossoverOperator(this.random, waveSizeLB, waveSizeUB, orders, aisles),
                new GreedyHeuristicCrossoverOperator(this.random, waveSizeLB, waveSizeUB, orders, aisles)
            },
            new double[] {0.13, 0.13, 0.13, 0.13, 0.12, 0.12, 0.12, 0.12},
            this.random
        );
    }

    /**
     * Builder Pattern para configuração fluente e imutável do GeneticAlgorithm
     */
    public static class Builder {
        // Parâmetros obrigatórios
        private final List<Map<Integer, Integer>> orders;
        private final List<Map<Integer, Integer>> aisles;
        private final int numItems;
        private final int waveSizeLB;
        private final int waveSizeUB;

        // Parâmetros opcionais com valores padrão
        private int populationSize = 100;
        private int maxGenerations = 1000;
        private int noImprovementLimit = 50;
        private double crossoverRate = 0.8;
        private double mutationRate = 0.05;
        private int elitismCount = 10;
        private long maxRuntimeMillis = 60000;
        private MutationOperator mutationOperator = null;
        private CrossoverOperator crossoverOperator = null;

        public Builder(List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles, int numItems, int waveSizeLB, int waveSizeUB) {
            this.orders = orders;
            this.aisles = aisles;
            this.numItems = numItems;
            this.waveSizeLB = waveSizeLB;
            this.waveSizeUB = waveSizeUB;
        }

        public Builder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }
        public Builder maxGenerations(int maxGenerations) {
            this.maxGenerations = maxGenerations;
            return this;
        }
        public Builder noImprovementLimit(int noImprovementLimit) {
            this.noImprovementLimit = noImprovementLimit;
            return this;
        }
        public Builder crossoverRate(double crossoverRate) {
            this.crossoverRate = crossoverRate;
            return this;
        }
        public Builder mutationRate(double mutationRate) {
            this.mutationRate = mutationRate;
            return this;
        }
        public Builder elitismCount(int elitismCount) {
            this.elitismCount = elitismCount;
            return this;
        }
        public Builder maxRuntimeMillis(long maxRuntimeMillis) {
            this.maxRuntimeMillis = maxRuntimeMillis;
            return this;
        }
        public Builder mutationOperator(MutationOperator mutationOperator) {
            this.mutationOperator = mutationOperator;
            return this;
        }
        public Builder crossoverOperator(CrossoverOperator crossoverOperator) {
            this.crossoverOperator = crossoverOperator;
            return this;
        }

        public GeneticAlgorithm build() {
            return new GeneticAlgorithm(this);
        }
    }

    /**
     * Inicializa o algoritmo genético com uma solução inicial obtida pelo CP-SAT
     * @param initialSolution Solução inicial do CP-SAT
     */
    public void initialize(ChallengeSolution initialSolution) {
        population.clear();

        // Se temos uma solução inicial válida, usá-la como primeiro indivíduo
        if (initialSolution != null &&
            initialSolution.orders() != null &&
            initialSolution.aisles() != null &&
            !initialSolution.orders().isEmpty() &&
            !initialSolution.aisles().isEmpty()) {

            Individual initialIndividual = new Individual(initialSolution, orders.size());
            evaluateIndividual(initialIndividual);
            population.add(initialIndividual);

            System.out.println("Adicionando solução inicial CP-SAT como indivíduo 1: " + initialIndividual);
        }

        // Completar o resto da população com indivíduos aleatórios
        while (population.size() < populationSize) {
            Individual individual = new Individual(orders.size(), random);
            repairIndividual(individual); // Repara para garantir viabilidade
            evaluateIndividual(individual);
            population.add(individual);
        }

        // Ordena população por fitness
        Collections.sort(population);

        // Atualiza o melhor indivíduo
        if (!population.isEmpty()) {
            bestIndividual = population.get(0).copy();
            bestFitness = bestIndividual.getFitness();
        }
    }

    /**
     * Executa o algoritmo genético
     * @param stopWatch Cronômetro para controlar o tempo de execução
     * @return A melhor solução encontrada
     */
    public ChallengeSolution evolve(StopWatch stopWatch) {
        int generation = 0;
        generationsWithoutImprovement = 0;
        double lastBestFitness = 0.0;
        mutationRateAdaptativa = mutationRateInicial; // inicia com valor inicial

        System.out.println("Iniciando evolução genética...");

        while (generation < maxGenerations &&
               generationsWithoutImprovement < noImprovementLimit &&
               (stopWatch.getTime(TimeUnit.MILLISECONDS) < maxRuntimeMillis)) {

            // --- Controle adaptativo: ajusta mutationRate conforme melhoria ---
            double improvement = (bestFitness > 0) ? (bestFitness - lastBestFitness) / bestFitness : 0.0;
            if (improvement < improvementThresholdDown) {
                mutationRateAdaptativa = Math.min(mutationRateAdaptativa + mutationStep, mutationRateMax);
            } else if (improvement > improvementThresholdUp) {
                mutationRateAdaptativa = Math.max(mutationRateAdaptativa - mutationStep, mutationRateMin);
            }
            lastBestFitness = bestFitness;
            // --------------------------------------------------------------

            List<Individual> offspring = generateOffspring();

            offspring.parallelStream().forEach(child -> {
                repairIndividual(child);
                evaluateIndividual(child);
            });

            // Combina pais e filhos
            List<Individual> combinedPopulation = new ArrayList<>(population);
            combinedPopulation.addAll(offspring);

            // Ordena a população combinada por fitness
            Collections.sort(combinedPopulation);

            // Seleciona os melhores para a próxima geração (elitismo)
            population.clear();
            for (int i = 0; i < populationSize && i < combinedPopulation.size(); i++) {
                population.add(combinedPopulation.get(i));
            }

            // Atualiza o melhor indivíduo
            if (population.get(0).getFitness() > bestFitness) {
                bestIndividual = population.get(0).copy();
                bestFitness = bestIndividual.getFitness();
                generationsWithoutImprovement = 0;

                System.out.println("Geração " + generation +
                                   ": Nova melhor solução com fitness " + bestFitness +
                                   " (Pedidos: " + bestIndividual.getNumSelectedOrders() +
                                   ", Corredores: " + bestIndividual.getNumVisitedAisles() +
                                   ", Unidades: " + bestIndividual.getTotalUnits() + ")");
            } else {
                generationsWithoutImprovement++;
            }

            generation++;

            // Log a cada 10 gerações
            if (generation % 10 == 0) {
                System.out.println("Geração " + generation +
                                   ": Melhor fitness = " + bestFitness +
                                   ", Gerações sem melhoria: " + generationsWithoutImprovement +
                                   ", Tempo: " + stopWatch.getTime(TimeUnit.SECONDS) + "s");
            }
        }

        System.out.println("Evolução genética concluída após " + generation + " gerações.");
        System.out.println("Melhor solução: " + bestIndividual);

        return bestIndividual.toSolution();
    }

    private List<Individual> generateOffspring() {
        List<Individual> offspring = new ArrayList<>();
        int numOffspring = populationSize - elitismCount;
        while (offspring.size() < numOffspring) {
            Individual parent1 = tournamentSelection();
            Individual parent2 = tournamentSelection();
            // Média dos genes de crossover dos pais
            double crossoverRateGene = (parent1.getCrossoverRateGene() + parent2.getCrossoverRateGene()) / 2.0;
            if (random.nextDouble() < crossoverRateGene) {
                Individual[] children = crossoverOperator.crossover(parent1, parent2);
                for (Individual child : children) {
                    // Herdar/mutar genes de parâmetros
                    double mutGene = (parent1.getMutationRateGene() + parent2.getMutationRateGene()) / 2.0;
                    // Pequena mutação gaussiana
                    mutGene += random.nextGaussian() * 0.01;
                    child.setMutationRateGene(mutGene);
                    double crossGene = (parent1.getCrossoverRateGene() + parent2.getCrossoverRateGene()) / 2.0;
                    crossGene += random.nextGaussian() * 0.01;
                    child.setCrossoverRateGene(crossGene);
                    // Mutação usando gene do próprio filho
                    mutationOperator.mutate(child, child.getMutationRateGene());
                    offspring.add(child);
                    if (offspring.size() >= numOffspring) break;
                }
            } else {
                Individual child1 = parent1.copy();
                Individual child2 = parent2.copy();
                // Mutação dos genes de parâmetro mesmo sem crossover
                double mutGene1 = parent1.getMutationRateGene() + random.nextGaussian() * 0.01;
                double crossGene1 = parent1.getCrossoverRateGene() + random.nextGaussian() * 0.01;
                child1.setMutationRateGene(mutGene1);
                child1.setCrossoverRateGene(crossGene1);
                double mutGene2 = parent2.getMutationRateGene() + random.nextGaussian() * 0.01;
                double crossGene2 = parent2.getCrossoverRateGene() + random.nextGaussian() * 0.01;
                child2.setMutationRateGene(mutGene2);
                child2.setCrossoverRateGene(crossGene2);
                mutationOperator.mutate(child1, child1.getMutationRateGene());
                mutationOperator.mutate(child2, child2.getMutationRateGene());
                offspring.add(child1);
                if (offspring.size() < numOffspring) offspring.add(child2);
            }
        }
        return offspring;
    }

    /**
     * Seleciona um indivíduo por torneio binário
     * @return O indivíduo selecionado
     */
    private Individual tournamentSelection() {
        // Seleciona dois indivíduos aleatoriamente
        int idx1 = random.nextInt(population.size());
        int idx2 = random.nextInt(population.size());

        // Retorna o melhor dos dois (menor índice = melhor fitness)
        return (idx1 < idx2) ? population.get(idx1) : population.get(idx2);
    }

    /**
     * Avalia um indivíduo calculando seu fitness e verificando sua viabilidade
     * @param individual O indivíduo a ser avaliado
     */
    private void evaluateIndividual(Individual individual) {
        if (isEmptySelection(individual)) {
            setUnfeasible(individual, 0.0);
            return;
        }
        Map<Integer, Integer> totalDemand = calculateTotalDemand(individual);
        int totalUnits = totalDemand.values().stream().mapToInt(Integer::intValue).sum();
        individual.setTotalUnits(totalUnits);
        if (!checkSizeConstraints(individual, totalUnits)) {
            return;
        }
        Set<Integer> visitedAisles = findVisitedAisles(totalDemand);
        if (visitedAisles == null) {
            setUnfeasible(individual, 0.01);
            return;
        }
        if (!checkCapacityConstraints(totalDemand, visitedAisles)) {
            setUnfeasible(individual, 0.05);
            return;
        }
        double fitness = calculateFitness(totalUnits, visitedAisles.size());
        individual.setVisitedAisles(visitedAisles);
        individual.setFitness(fitness);
        individual.setFeasible(true);
    }

    private boolean isEmptySelection(Individual individual) {
        return individual.getSelectedOrders().isEmpty();
    }

    private void setUnfeasible(Individual individual, double fitness) {
        individual.setFitness(fitness);
        individual.setFeasible(false);
    }

    private Map<Integer, Integer> calculateTotalDemand(Individual individual) {
        Map<Integer, Integer> totalDemand = new HashMap<>();
        for (Integer orderId : individual.getSelectedOrders()) {
            Map<Integer, Integer> orderItems = orders.get(orderId);
            for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
                int itemId = entry.getKey();
                int units = entry.getValue();
                totalDemand.put(itemId, totalDemand.getOrDefault(itemId, 0) + units);
            }
        }
        return totalDemand;
    }

    private boolean checkSizeConstraints(Individual individual, int totalUnits) {
        boolean withinLimits = (totalUnits >= waveSizeLB && totalUnits <= waveSizeUB);
        if (!withinLimits) {
            individual.setFitness(0.1 * totalUnits / (Math.abs(totalUnits - waveSizeLB) + Math.abs(totalUnits - waveSizeUB) + 1));
            individual.setFeasible(false);
            return false;
        }
        return true;
    }

    private Set<Integer> findVisitedAisles(Map<Integer, Integer> totalDemand) {
        Set<Integer> visitedAisles = new HashSet<>();
        Set<Integer> coveredItems = new HashSet<>();
        Map<Integer, List<Integer>> itemToAisles = new HashMap<>();
        for (int i = 0; i < aisles.size(); i++) {
            Map<Integer, Integer> aisleItems = aisles.get(i);
            for (int itemId : aisleItems.keySet()) {
                if (totalDemand.containsKey(itemId)) {
                    itemToAisles.computeIfAbsent(itemId, k -> new ArrayList<>()).add(i);
                }
            }
        }
        while (coveredItems.size() < totalDemand.size()) {
            int bestAisle = -1;
            int maxNewItemsCovered = 0;
            for (int aisleId = 0; aisleId < aisles.size(); aisleId++) {
                if (visitedAisles.contains(aisleId)) continue;
                Map<Integer, Integer> aisleItems = aisles.get(aisleId);
                int newItemsCovered = 0;
                for (int itemId : aisleItems.keySet()) {
                    if (totalDemand.containsKey(itemId) && !coveredItems.contains(itemId)) {
                        newItemsCovered++;
                    }
                }
                if (newItemsCovered > maxNewItemsCovered) {
                    maxNewItemsCovered = newItemsCovered;
                    bestAisle = aisleId;
                }
            }
            if (bestAisle == -1 || maxNewItemsCovered == 0) {
                return null;
            }
            visitedAisles.add(bestAisle);
            Map<Integer, Integer> bestAisleItems = aisles.get(bestAisle);
            for (int itemId : bestAisleItems.keySet()) {
                if (totalDemand.containsKey(itemId)) {
                    coveredItems.add(itemId);
                }
            }
        }
        return visitedAisles;
    }

    private boolean checkCapacityConstraints(Map<Integer, Integer> totalDemand, Set<Integer> visitedAisles) {
        Map<Integer, Integer> totalSupply = new HashMap<>();
        for (Integer aisleId : visitedAisles) {
            Map<Integer, Integer> aisleItems = aisles.get(aisleId);
            for (Map.Entry<Integer, Integer> entry : aisleItems.entrySet()) {
                int itemId = entry.getKey();
                int capacity = entry.getValue();
                if (totalDemand.containsKey(itemId)) {
                    totalSupply.put(itemId, totalSupply.getOrDefault(itemId, 0) + capacity);
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : totalDemand.entrySet()) {
            int itemId = entry.getKey();
            int demand = entry.getValue();
            int supply = totalSupply.getOrDefault(itemId, 0);
            if (supply < demand) {
                return false;
            }
        }
        return true;
    }

    private double calculateFitness(int totalUnits, int numAisles) {
        return (double) totalUnits / numAisles;
    }

    /**
     * Repara um indivíduo para tentar torná-lo viável
     * @param individual O indivíduo a ser reparado
     */
    private void repairIndividual(Individual individual) {
        // Obtém os pedidos selecionados
        Set<Integer> selectedOrders = individual.getSelectedOrders();

        // Se não há pedidos selecionados, adiciona alguns aleatoriamente
        if (selectedOrders.isEmpty()) {
            for (int i = 0; i < 5; i++) { // Adiciona 5 pedidos aleatórios
                int randomOrder = random.nextInt(orders.size());
                individual.setGene(randomOrder, true);
            }
            selectedOrders = individual.getSelectedOrders();
        }

        // 1. Calcular total de unidades nos pedidos selecionados
        Map<Integer, Integer> totalDemand = new HashMap<>();
        int totalUnits = 0;

        for (Integer orderId : selectedOrders) {
            Map<Integer, Integer> orderItems = orders.get(orderId);

            for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
                int itemId = entry.getKey();
                int units = entry.getValue();

                totalDemand.put(itemId, totalDemand.getOrDefault(itemId, 0) + units);
                totalUnits += units;
            }
        }

        // 2. Reparar se o total de unidades está fora dos limites
        List<Integer> ordersList = new ArrayList<>(selectedOrders);
        Collections.shuffle(ordersList, random); // Embaralha para aleatoriedade na reparação

        // 2.1. Se estiver abaixo do limite inferior, adiciona pedidos aleatoriamente
        if (totalUnits < waveSizeLB) {
            List<Integer> candidateOrders = new ArrayList<>();
            for (int i = 0; i < orders.size(); i++) {
                if (!selectedOrders.contains(i)) {
                    candidateOrders.add(i);
                }
            }
            Collections.shuffle(candidateOrders, random);

            for (Integer orderId : candidateOrders) {
                Map<Integer, Integer> orderItems = orders.get(orderId);
                int orderUnits = orderItems.values().stream().mapToInt(Integer::intValue).sum();

                // Adiciona o pedido se não ultrapassar o limite superior
                if (totalUnits + orderUnits <= waveSizeUB) {
                    individual.setGene(orderId, true);
                    totalUnits += orderUnits;

                    // Atualiza a demanda total
                    for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
                        int itemId = entry.getKey();
                        int units = entry.getValue();
                        totalDemand.put(itemId, totalDemand.getOrDefault(itemId, 0) + units);
                    }

                    // Se já atingiu o limite inferior, para
                    if (totalUnits >= waveSizeLB) {
                        break;
                    }
                }
            }
        }
        // 2.2. Se estiver acima do limite superior, remove pedidos aleatoriamente
        else if (totalUnits > waveSizeUB) {
            for (Integer orderId : ordersList) {
                Map<Integer, Integer> orderItems = orders.get(orderId);
                int orderUnits = orderItems.values().stream().mapToInt(Integer::intValue).sum();

                // Remove o pedido
                individual.setGene(orderId, false);
                totalUnits -= orderUnits;

                // Atualiza a demanda total
                for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
                    int itemId = entry.getKey();
                    int units = entry.getValue();
                    totalDemand.put(itemId, totalDemand.getOrDefault(itemId, 0) - units);
                    if (totalDemand.get(itemId) <= 0) {
                        totalDemand.remove(itemId);
                    }
                }

                // Se já está dentro do limite superior e ainda acima do inferior, para
                if (totalUnits <= waveSizeUB && totalUnits >= waveSizeLB) {
                    break;
                }
            }
        }

        // Se ainda estiver fora dos limites após a reparação, não há muito o que fazer
        // O indivíduo será avaliado como inviável
    }
}

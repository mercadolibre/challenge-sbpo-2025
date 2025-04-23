package org.sbpo2025.challenge.geneticAlgorithm;

import org.apache.commons.lang3.time.StopWatch;
import org.sbpo2025.challenge.ChallengeSolution;
import org.sbpo2025.challenge.ChallengeSolver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Solver genético para o problema de wave-selection do SBPO 2025.
 * Utiliza uma solução inicial obtida pelo CP-SAT e a refina usando um Algoritmo Genético.
 */
public class GeneticSolver extends ChallengeSolver {

    // Definindo nossa própria constante para o tempo máximo de execução
    private static final long MAX_RUNTIME_MILLIS = 600000; // 10 minutos em milissegundos

    // Configurações padrão do algoritmo genético
    private static final int DEFAULT_POPULATION_SIZE = 100;
    private static final int DEFAULT_MAX_GENERATIONS = 1000;
    private static final int DEFAULT_NO_IMPROVEMENT_LIMIT = 50;
    private static final double DEFAULT_CROSSOVER_RATE = 0.8;
    private static final double DEFAULT_MUTATION_RATE = 0.05;
    private static final int DEFAULT_ELITISM_COUNT = 10;

    // Configurações personalizáveis
    private int populationSize;
    private int maxGenerations;
    private int noImprovementLimit;
    private double crossoverRate;
    private double mutationRate;
    private int elitismCount;
    private boolean useCpSat;

    /**
     * Construtor com configurações padrão
     * @param orders Lista de pedidos (mapa de item para quantidade)
     * @param aisles Lista de corredores (mapa de item para capacidade)
     * @param nItems Número total de itens diferentes
     * @param waveSizeLB Limite inferior da wave
     * @param waveSizeUB Limite superior da wave
     */
    public GeneticSolver(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB) {
        super(orders, aisles, nItems, waveSizeLB, waveSizeUB);
        initializeDefaultSettings();
    }

    /**
     * Construtor com configurações personalizadas
     * @param orders Lista de pedidos
     * @param aisles Lista de corredores
     * @param nItems Número total de itens diferentes
     * @param waveSizeLB Limite inferior da wave
     * @param waveSizeUB Limite superior da wave
     * @param populationSize Tamanho da população
     * @param maxGenerations Número máximo de gerações
     * @param noImprovementLimit Limite de gerações sem melhoria para parar
     * @param crossoverRate Taxa de crossover
     * @param mutationRate Taxa de mutação
     * @param elitismCount Número de indivíduos elite preservados entre gerações
     * @param useCpSat Indica se deve usar CP-SAT para solução inicial
     */
    public GeneticSolver(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB,
            int populationSize,
            int maxGenerations,
            int noImprovementLimit,
            double crossoverRate,
            double mutationRate,
            int elitismCount,
            boolean useCpSat) {

        super(orders, aisles, nItems, waveSizeLB, waveSizeUB);

        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.noImprovementLimit = noImprovementLimit;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        this.useCpSat = useCpSat;
    }

    /**
     * Inicializa as configurações padrão
     */
    private void initializeDefaultSettings() {
        this.populationSize = DEFAULT_POPULATION_SIZE;
        this.maxGenerations = DEFAULT_MAX_GENERATIONS;
        this.noImprovementLimit = DEFAULT_NO_IMPROVEMENT_LIMIT;
        this.crossoverRate = DEFAULT_CROSSOVER_RATE;
        this.mutationRate = DEFAULT_MUTATION_RATE;
        this.elitismCount = DEFAULT_ELITISM_COUNT;
        this.useCpSat = true;
    }

    @Override
    public ChallengeSolution solve(StopWatch stopWatch) {
        System.out.println("Iniciando Genetic Solver com algoritmo genético...");

        // Obter uma solução inicial viável usando CP-SAT (se configurado)
        ChallengeSolution initialSolution = null;

        if (useCpSat) {
            double initialTimeLimit = 30.0; // aumentando o tempo para encontrar solução inicial de qualidade
            initialSolution = solveWithCpSat(initialTimeLimit);

            if (initialSolution != null) {
                System.out.println("Solução inicial viável encontrada com CP-SAT!");
                System.out.println("Pedidos selecionados: " + initialSolution.orders().size());
                System.out.println("Corredores utilizados: " + initialSolution.aisles().size());
                System.out.println("Função objetivo: " + computeObjectiveFunction(initialSolution));
            } else {
                System.out.println("Não foi possível encontrar uma solução inicial viável com CP-SAT.");
            }
        }

        // Calcula o tempo disponível para o algoritmo genético
        long elapsedTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        long remainingTime = Math.max(0, MAX_RUNTIME_MILLIS - elapsedTime);

        System.out.println("Tempo restante para GA: " + remainingTime + " ms");

        // Configura e executa o algoritmo genético
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
            orders,
            aisles,
            nItems,
            waveSizeLB,
            waveSizeUB,
            populationSize,
            maxGenerations,
            noImprovementLimit,
            crossoverRate,
            mutationRate,
            elitismCount,
            remainingTime
        );

        // Inicializa com a solução do CP-SAT ou aleatória
        geneticAlgorithm.initialize(initialSolution);

        // Executa o algoritmo genético
        ChallengeSolution gaSolution = geneticAlgorithm.evolve(stopWatch);

        // Se encontrou uma solução viável com o GA
        if (gaSolution != null && isSolutionFeasible(gaSolution)) {
            double gaObjective = computeObjectiveFunction(gaSolution);
            double initialObjective = initialSolution != null ?
                                     computeObjectiveFunction(initialSolution) : 0.0;

            System.out.println("Solução final do GA:");
            System.out.println("Pedidos selecionados: " + gaSolution.orders().size());
            System.out.println("Corredores utilizados: " + gaSolution.aisles().size());
            System.out.println("Função objetivo: " + gaObjective);

            // Compara com a solução inicial
            if (initialSolution != null) {
                double improvement = ((gaObjective / initialObjective) - 1.0) * 100.0;
                System.out.println("Melhoria sobre solução inicial: " + String.format("%.2f", improvement) + "%");
            }

            return gaSolution;
        } else if (initialSolution != null) {
            System.out.println("GA não encontrou solução melhor. Usando solução inicial do CP-SAT.");
            return initialSolution;
        } else {
            System.out.println("Nenhuma solução viável encontrada.");
            return null;
        }
    }

    /**
     * Método que usa o solveWithCpSat da classe pai
     * Mantendo como protegido (protected) conforme a classe pai
     * @param timeLimit Limite de tempo em segundos
     * @return Solução inicial viável ou null
     */
    @Override
    protected ChallengeSolution solveWithCpSat(double timeLimit) {
        // Chama o método da classe pai diretamente
        return super.solveWithCpSat(timeLimit);
    }
}

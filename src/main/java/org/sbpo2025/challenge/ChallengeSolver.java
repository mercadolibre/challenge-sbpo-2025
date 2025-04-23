package org.sbpo2025.challenge;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.sbpo2025.challenge.geneticAlgorithm.GeneticAlgorithm;
import org.sbpo2025.challenge.ChallengeConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChallengeSolver {
    private final long MAX_RUNTIME = ChallengeConfig.MAX_RUNTIME_MILLIS; // milliseconds; 10 minutes

    protected List<Map<Integer, Integer>> orders;
    protected List<Map<Integer, Integer>> aisles;
    protected int nItems;
    protected int waveSizeLB;
    protected int waveSizeUB;

    public ChallengeSolver(
            List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles, int nItems, int waveSizeLB, int waveSizeUB) {
        this.orders = orders;
        this.aisles = aisles;
        this.nItems = nItems;
        this.waveSizeLB = waveSizeLB;
        this.waveSizeUB = waveSizeUB;
    }

    public ChallengeSolution solve(StopWatch stopWatch) {
        // 1. Primeiro obtém uma solução inicial viável usando CP-SAT
        double initialTimeLimit = ChallengeConfig.INITIAL_CP_SAT_TIME_LIMIT_SECONDS; // limite de tempo para a solução inicial
        ChallengeSolution initialSolution = solveWithCpSat(initialTimeLimit);

        // Se encontrou uma solução viável
        if (initialSolution != null) {
            System.out.println("Solução inicial viável encontrada com CP-SAT!");
            System.out.println("Pedidos selecionados: " + initialSolution.orders().size());
            System.out.println("Corredores utilizados: " + initialSolution.aisles().size());
            System.out.println("Função objetivo: " + computeObjectiveFunction(initialSolution));

            // 2. Aplicar Algoritmo Genético para refinar a solução
            System.out.println("Aplicando Algoritmo Genético para refinamento da solução...");

            // Calcula tempo disponível para GA após CP-SAT
            long elapsedTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
            long remainingTime = Math.max(0, MAX_RUNTIME - elapsedTime);

            // Configura e executa o algoritmo genético
            if (remainingTime > ChallengeConfig.MIN_TIME_FOR_GA_MILLIS) { // Se restam pelo menos 10 segundos
                // Configurações do GA otimizadas para a instância
                GeneticAlgorithm.Builder gaBuilder = new GeneticAlgorithm.Builder(
                    orders,
                    aisles,
                    nItems,
                    waveSizeLB,
                    waveSizeUB
                );
                if (orders.size() > 100 || aisles.size() > 15) {
                    gaBuilder.populationSize(ChallengeConfig.GA_POPULATION_SIZE_LARGE)
                            .maxGenerations(ChallengeConfig.GA_MAX_GENERATIONS_LARGE)
                            .mutationRate(ChallengeConfig.GA_MUTATION_RATE_LARGE);
                } else {
                    gaBuilder.populationSize(ChallengeConfig.GA_POPULATION_SIZE_SMALL)
                            .maxGenerations(ChallengeConfig.GA_MAX_GENERATIONS_SMALL);
                }
                gaBuilder.noImprovementLimit(ChallengeConfig.GA_NO_IMPROVEMENT_LIMIT)
                        .crossoverRate(ChallengeConfig.GA_CROSSOVER_RATE)
                        .elitismCount(ChallengeConfig.GA_ELITISM_COUNT)
                        .maxRuntimeMillis(remainingTime);

                GeneticAlgorithm geneticAlgorithm = gaBuilder.build();
                // Inicializa com a solução do CP-SAT
                geneticAlgorithm.initialize(initialSolution);

                // Executa o algoritmo genético
                ChallengeSolution gaSolution = geneticAlgorithm.evolve(stopWatch);

                // Se encontrou uma solução viável com o GA
                if (gaSolution != null && isSolutionFeasible(gaSolution)) {
                    double gaObjective = computeObjectiveFunction(gaSolution);
                    double initialObjective = computeObjectiveFunction(initialSolution);

                    System.out.println("Solução final do GA:");
                    System.out.println("Pedidos selecionados: " + gaSolution.orders().size());
                    System.out.println("Corredores utilizados: " + gaSolution.aisles().size());
                    System.out.println("Função objetivo: " + gaObjective);

                    // Compara com a solução inicial
                    double improvement = ((gaObjective / initialObjective) - 1.0) * 100.0;
                    System.out.println("Melhoria sobre solução inicial: " + String.format("%.2f", improvement) + "%");

                    // Retorna a melhor solução
                    return (gaObjective > initialObjective) ? gaSolution : initialSolution;
                }
            } else {
                System.out.println("Tempo insuficiente para execução do GA. Usando solução do CP-SAT.");
            }

            return initialSolution;
        } else {
            System.out.println("Não foi possível encontrar uma solução inicial viável com CP-SAT.");

            // Tenta encontrar uma solução usando apenas o GA
            System.out.println("Tentando encontrar solução usando apenas Algoritmo Genético...");

            // Calcula tempo disponível para GA
            long remainingTime = getRemainingTime(stopWatch) * 1000; // em milissegundos

            if (remainingTime > ChallengeConfig.MIN_TIME_FOR_GA_MILLIS) { // Se restam pelo menos 10 segundos
                // Configurações do GA para busca sem solução inicial
                GeneticAlgorithm.Builder gaBuilder = new GeneticAlgorithm.Builder(
                    orders,
                    aisles,
                    nItems,
                    waveSizeLB,
                    waveSizeUB
                );
                gaBuilder.populationSize(ChallengeConfig.GA_POPULATION_SIZE_LARGE)
                        .maxGenerations(ChallengeConfig.GA_MAX_GENERATIONS_NO_INIT)
                        .noImprovementLimit(ChallengeConfig.GA_NO_IMPROVEMENT_LIMIT_NO_INIT)
                        .crossoverRate(ChallengeConfig.GA_CROSSOVER_RATE_NO_INIT)
                        .mutationRate(ChallengeConfig.GA_MUTATION_RATE_NO_INIT)
                        .elitismCount(ChallengeConfig.GA_ELITISM_COUNT_NO_INIT)
                        .maxRuntimeMillis(remainingTime);
                GeneticAlgorithm geneticAlgorithm = gaBuilder.build();

                // Inicializa sem solução prévia
                geneticAlgorithm.initialize(null);

                // Executa o algoritmo genético
                ChallengeSolution gaSolution = geneticAlgorithm.evolve(stopWatch);

                if (gaSolution != null && isSolutionFeasible(gaSolution)) {
                    System.out.println("Solução viável encontrada com GA:");
                    System.out.println("Pedidos selecionados: " + gaSolution.orders().size());
                    System.out.println("Corredores utilizados: " + gaSolution.aisles().size());
                    System.out.println("Função objetivo: " + computeObjectiveFunction(gaSolution));
                    return gaSolution;
                }
            }

            return null;
        }
    }

    /*
     * Get the remaining time in seconds
     */
    protected long getRemainingTime(StopWatch stopWatch) {
        return Math.max(
                TimeUnit.SECONDS.convert(MAX_RUNTIME - stopWatch.getTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS),
                0);
    }

    protected boolean isSolutionFeasible(ChallengeSolution challengeSolution) {
        Set<Integer> selectedOrders = challengeSolution.orders();
        Set<Integer> visitedAisles = challengeSolution.aisles();
        if (selectedOrders == null || visitedAisles == null || selectedOrders.isEmpty() || visitedAisles.isEmpty()) {
            return false;
        }

        int[] totalUnitsPicked = new int[nItems];
        int[] totalUnitsAvailable = new int[nItems];

        // Calculate total units picked
        for (int order : selectedOrders) {
            for (Map.Entry<Integer, Integer> entry : orders.get(order).entrySet()) {
                totalUnitsPicked[entry.getKey()] += entry.getValue();
            }
        }

        // Calculate total units available
        for (int aisle : visitedAisles) {
            for (Map.Entry<Integer, Integer> entry : aisles.get(aisle).entrySet()) {
                totalUnitsAvailable[entry.getKey()] += entry.getValue();
            }
        }

        // Check if the total units picked are within bounds
        int totalUnits = Arrays.stream(totalUnitsPicked).sum();
        if (totalUnits < waveSizeLB || totalUnits > waveSizeUB) {
            return false;
        }

        // Check if the units picked do not exceed the units available
        for (int i = 0; i < nItems; i++) {
            if (totalUnitsPicked[i] > totalUnitsAvailable[i]) {
                return false;
            }
        }

        return true;
    }

    protected double computeObjectiveFunction(ChallengeSolution challengeSolution) {
        Set<Integer> selectedOrders = challengeSolution.orders();
        Set<Integer> visitedAisles = challengeSolution.aisles();
        if (selectedOrders == null || visitedAisles == null || selectedOrders.isEmpty() || visitedAisles.isEmpty()) {
            return 0.0;
        }
        int totalUnitsPicked = 0;

        // Calculate total units picked
        for (int order : selectedOrders) {
            totalUnitsPicked += orders.get(order).values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        // Calculate the number of visited aisles
        int numVisitedAisles = visitedAisles.size();

        // Objective function: total units picked / number of visited aisles
        return (double) totalUnitsPicked / numVisitedAisles;
    }

    /**
     * Encontra uma solução inicial viável usando o CP-SAT (OR-Tools)
     * Modelo de programação inteira 0-1 que satisfaz as restrições (2), (3) e (4) do enunciado
     *
     * @param timeLimit Limite de tempo em segundos
     * @return Uma solução inicial viável ou null se não encontrada
     */
    protected ChallengeSolution solveWithCpSat(double timeLimit) {
        try {
            // Carregar bibliotecas do OR-Tools
            Loader.loadNativeLibraries();
            CpModel model = new CpModel();

            int O = orders.size();
            int A = aisles.size();

            // 1) Variáveis de decisão
            // x[o]: variável binária que indica se o pedido o é selecionado
            IntVar[] x = new IntVar[O];
            for (int o = 0; o < O; o++) {
                x[o] = model.newBoolVar("x_order_" + o);
            }

            // y[a]: variável binária que indica se o corredor a é visitado
            IntVar[] y = new IntVar[A];
            for (int a = 0; a < A; a++) {
                y[a] = model.newBoolVar("y_corridor_" + a);
            }

            // 2.1) Restrição: LB ≤ total de unidades coletadas ≤ UB
            LinearExprBuilder totalUnitsBuilder = LinearExpr.newBuilder();
            for (int o = 0; o < O; o++) {
                Map<Integer, Integer> orderItems = orders.get(o);
                for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
                    totalUnitsBuilder.addTerm(x[o], entry.getValue());
                }
            }
            // Construir expressão imutável antes de adicionar restrições
            LinearExpr totalUnits = totalUnitsBuilder.build();
            model.addGreaterOrEqual(totalUnits, waveSizeLB);
            model.addLessOrEqual(totalUnits, waveSizeUB);

            // 2.2) Restrição: capacidade por item (demanda ≤ oferta)
            for (int i = 0; i < nItems; i++) {
                // Soma das demandas do item i
                LinearExprBuilder demandBuilder = LinearExpr.newBuilder();
                for (int o = 0; o < O; o++) {
                    Map<Integer, Integer> orderItems = orders.get(o);
                    if (orderItems.containsKey(i)) {
                        int uoi = orderItems.get(i);
                        demandBuilder.addTerm(x[o], uoi);
                    }
                }

                // Soma da oferta nos corredores que contêm o item i
                LinearExprBuilder supplyBuilder = LinearExpr.newBuilder();
                for (int a = 0; a < A; a++) {
                    Map<Integer, Integer> aisleItems = aisles.get(a);
                    if (aisleItems.containsKey(i)) {
                        int uai = aisleItems.get(i);
                        supplyBuilder.addTerm(y[a], uai);
                    }
                }

                // Construir expressões antes de adicionar restrição: demanda ≤ oferta
                LinearExpr demand = demandBuilder.build();
                LinearExpr supply = supplyBuilder.build();
                model.addLessOrEqual(demand, supply);
            }

            // 2.3) Restrição adicional: se um pedido o requer um item i,
            // deve haver pelo menos um corredor selecionado que contenha i
            for (int o = 0; o < O; o++) {
                Map<Integer, Integer> orderItems = orders.get(o);
                for (Integer i : orderItems.keySet()) {
                    // Lista de corredores que contêm o item i
                    LinearExprBuilder corridorsBuilder = LinearExpr.newBuilder();
                    for (int a = 0; a < A; a++) {
                        Map<Integer, Integer> aisleItems = aisles.get(a);
                        if (aisleItems.containsKey(i)) {
                            corridorsBuilder.addTerm(y[a], 1);
                        }
                    }

                    // Construir expressão antes de adicionar restrição de viabilidade
                    LinearExpr corridorsExpr = corridorsBuilder.build();
                    model.addLessOrEqual(x[o], corridorsExpr);
                }
            }

            // 3) Função objetivo: maximizar total de unidades coletadas
            model.maximize(totalUnits);

            // 4) Configurar e resolver o modelo
            CpSolver solver = new CpSolver();
            solver.getParameters().setMaxTimeInSeconds(timeLimit);
            CpSolverStatus status = solver.solve(model);

            // 5) Extrair a solução
            if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
                Set<Integer> selectedOrders = new HashSet<>();
                Set<Integer> visitedAisles = new HashSet<>();

                // Adicionar pedidos selecionados
                for (int o = 0; o < O; o++) {
                    if (solver.value(x[o]) > 0.5) {
                        selectedOrders.add(o);
                    }
                }

                // Adicionar corredores visitados
                for (int a = 0; a < A; a++) {
                    if (solver.value(y[a]) > 0.5) {
                        visitedAisles.add(a);
                    }
                }

                ChallengeSolution solution = new ChallengeSolution(selectedOrders, visitedAisles);

                // Verificar a solução
                if (isSolutionFeasible(solution)) {
                    return solution;
                }
            }

            return null; // Nenhuma solução viável encontrada

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

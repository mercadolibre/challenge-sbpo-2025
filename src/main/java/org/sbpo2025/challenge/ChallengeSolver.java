package org.sbpo2025.challenge;

import org.apache.commons.lang3.time.StopWatch;
import org.sbpo2025.challenge.GeneticAlgorithm.GeneticAlgorithmExecutor;
import org.sbpo2025.challenge.GeneticAlgorithm.GAConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ChallengeSolver {
    private final long MAX_RUNTIME = 600000; // milliseconds; 10 minutes

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
        // Implement your solution here

        // 1. Definir os parâmetros para o Algoritmo Genético
        // TODO: Estes parâmetros podem ser externalizados ou ajustados conforme necessário.
        GAConfiguration gaConfig = new GAConfiguration(
            100, // populationSize
            200, // numberOfGenerations
            0.8, // crossoverRate
            0.01,// mutationRate
            1000.0,// alphaCoveragePenalty
            100.0, // betaLBUBPenalty
            3    // tournamentSize
        );

        // 2. Criar uma instância do GeneticAlgorithmExecutor
        GeneticAlgorithmExecutor gaExecutor = new GeneticAlgorithmExecutor(
            this.orders,
            this.aisles,
            this.nItems,
            this.waveSizeLB,
            this.waveSizeUB,
            gaConfig
        );

        // 3. Executar o algoritmo genético
        // O método run() do executor agora retorna ChallengeSolution
        ChallengeSolution solution = gaExecutor.run();

        // 4. Retornar a solução encontrada
        // Se gaExecutor.run() retornar null (ou uma solução "vazia" indicando falha),
        // ChallengeSolver pode precisar lidar com isso, talvez retornando uma solução default ou vazia.
        // A implementação atual de convertToChallengeSolution em GeneticAlgorithmExecutor
        // já retorna uma solução vazia se o melhor indivíduo for nulo.
        if (solution == null) {
            // Isso não deve acontecer se convertToChallengeSolution sempre retorna uma instância.
            // Mas como uma salvaguarda ou se a lógica mudar:
            System.err.println("GeneticAlgorithmExecutor.run() retornou null. Retornando solução vazia do ChallengeSolver.");
            return new ChallengeSolution(new java.util.HashSet<>(), new java.util.HashSet<>());
        }

        return solution;
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
}

package org.sbpo2025.challenge;

import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.*;

public class ChallengeSolver {
    private final long MAX_RUNTIME = 600000; // milliseconds; 10 minutes

    protected List<Map<Integer, Integer>> orders;
    protected List<Map<Integer, Integer>> aisles;
    protected int nItems;
    protected int waveSizeLB;
    protected int waveSizeUB;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public ChallengeSolver(List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles, int nItems, int waveSizeLB, int waveSizeUB) {
    // Construtor permanece o mesmo.
    this.orders = new ArrayList<>(orders);
    this.aisles = new ArrayList<>(aisles);
    this.nItems = nItems;
    this.waveSizeLB = waveSizeLB;
    this.waveSizeUB = waveSizeUB;
}

// Classe auxiliar para armazenar informações do pedido e sua pontuação.
protected static class OrderInfo implements Comparable<OrderInfo> {
    int id;
    int units;
    double score;

    public OrderInfo(int id, int units, double score) {
        this.id = id;
        this.units = units;
        this.score = score;
    }

    // Ordena do maior para o menor score.
    @Override
    public int compareTo(OrderInfo other) {
        return Double.compare(other.score, this.score);
    }
}


public ChallengeSolution solve(StopWatch stopWatch) {
    // --- FASE 1: Pré-cálculo e Ordenação (FEITO UMA VEZ) ---
    Map<Integer, Set<Integer>> itemToAislesMap = new HashMap<>();
    for (int aisleIndex = 0; aisleIndex < aisles.size(); aisleIndex++) {
        for (int item : aisles.get(aisleIndex).keySet()) {
            itemToAislesMap.computeIfAbsent(item, k -> new HashSet<>()).add(aisleIndex);
        }
    }

    List<OrderInfo> orderInfos = new ArrayList<>();
    for (int i = 0; i < orders.size(); i++) {
        Map<Integer, Integer> order = orders.get(i);
        int units = order.values().stream().mapToInt(Integer::intValue).sum();
        if (units == 0) continue;

        Set<Integer> requiredAisles = new HashSet<>();
        for (int item : order.keySet()) {
            if (itemToAislesMap.containsKey(item)) {
                requiredAisles.addAll(itemToAislesMap.get(item));
            }
        }
        int numAisles = requiredAisles.size();
        double score = (numAisles > 0) ? (double) units / numAisles : Double.MAX_VALUE;
        orderInfos.add(new OrderInfo(i, units, score));
    }
    Collections.sort(orderInfos); // Ordena os pedidos pela pontuação de densidade.

    // --- FASE 2: Construção da Solução em Passada Única ---
    Set<Integer> selectedOrders = new HashSet<>();
    int totalUnitsPicked = 0;
    Map<Integer, Integer> requiredItemsCount = new HashMap<>();
    Set<Integer> visitedAisles = new HashSet<>();
    Map<Integer, Integer> availableStock = new HashMap<>();

    for (OrderInfo orderInfo : orderInfos) {
        if (totalUnitsPicked + orderInfo.units > waveSizeUB) {
            continue; // Pula se excede o limite máximo.
        }

        // Verifica a viabilidade do estoque ANTES de adicionar.
        boolean isFeasible = true;
        Map<Integer, Integer> itemsInOrder = orders.get(orderInfo.id);

        // Verifica se os itens que já precisamos + os novos itens têm estoque.
        for (Map.Entry<Integer, Integer> itemEntry : itemsInOrder.entrySet()) {
            int item = itemEntry.getKey();
            int needed = requiredItemsCount.getOrDefault(item, 0) + itemEntry.getValue();
            if (needed > availableStock.getOrDefault(item, 0)) {
                // Se o estoque atual não é suficiente, calcula o estoque com os novos corredores
                Set<Integer> newAislesForThisItem = itemToAislesMap.getOrDefault(item, Collections.emptySet());
                Set<Integer> tempAisles = new HashSet<>(visitedAisles);
                tempAisles.addAll(newAislesForThisItem);

                // Recalcula o estoque apenas se for estritamente necessário
                if (tempAisles.size() > visitedAisles.size()) {
                    int fullStock = 0;
                    for (int aisleIdx : tempAisles) {
                        fullStock += aisles.get(aisleIdx).getOrDefault(item, 0);
                    }
                    if (needed > fullStock) {
                        isFeasible = false;
                        break;
                    }
                } else if (needed > availableStock.getOrDefault(item, 0)) {
                     isFeasible = false;
                     break;
                }
            }
        }

        if (isFeasible) {
            // Adiciona o pedido à solução
            selectedOrders.add(orderInfo.id);
            totalUnitsPicked += orderInfo.units;
            
            Set<Integer> oldAisles = new HashSet<>(visitedAisles);

            // Atualiza contagem de itens e corredores
            itemsInOrder.forEach((item, quantity) -> requiredItemsCount.merge(item, quantity, Integer::sum));
            for (int item : itemsInOrder.keySet()) {
                visitedAisles.addAll(itemToAislesMap.getOrDefault(item, Collections.emptySet()));
            }

            // Atualiza o estoque disponível de forma incremental
            for(int aisleIdx : visitedAisles){
                if(!oldAisles.contains(aisleIdx)){
                    aisles.get(aisleIdx).forEach((item, quantity) -> availableStock.merge(item, quantity, Integer::sum));
                }
            }
        }
    }

    // --- FASE 3: Verificação Final e Retorno ---
    if (totalUnitsPicked < waveSizeLB || selectedOrders.isEmpty()) {
        // Se mesmo assim não atingiu o mínimo, a heurística falhou para esta instância.
        return new ChallengeSolution(new HashSet<>(), new HashSet<>());
    }
    
    return new ChallengeSolution(selectedOrders, visitedAisles);
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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

 /*
    * Avalia a população em paralelo usando um ExecutorService com 8 threads.
    * A função de avaliação computa a fitness de um indivíduo:
    * Se a solução for viável, fitness = total_units / num_aisles;
    * caso contrário, atribui um valor de fitness muito baixo (penaliza soluções inviáveis).
    */
    /* private void evaluatePopulation(List<Individual> population) {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<?>> futures = new ArrayList<>();
        
        for (Individual ind : population) {
            futures.add(executor.submit(() -> {
                ind.fitness = evaluateIndividual(ind);
            }));
        }
        // Aguarda a conclusão de todas as tarefas
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    } */
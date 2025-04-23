package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.*;

/**
 * Operador Greedy Heuristic Crossover.
 * Monta o filho pela união dos pais, ordenando pedidos por valor marginal (itens/coletor).
 */
public class GreedyHeuristicCrossoverOperator implements CrossoverOperator {
    private final Random random;
    private final int waveSizeLB;
    private final int waveSizeUB;
    private final List<Map<Integer, Integer>> orders;
    private final List<Map<Integer, Integer>> aisles;

    public GreedyHeuristicCrossoverOperator(Random random, int waveSizeLB, int waveSizeUB, List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles) {
        this.random = random;
        this.waveSizeLB = waveSizeLB;
        this.waveSizeUB = waveSizeUB;
        this.orders = orders;
        this.aisles = aisles;
    }

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        Set<Integer> setA = parent1.getSelectedOrders();
        Set<Integer> setB = parent2.getSelectedOrders();
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);

        // Ordena U por valor marginal (itens/coletor)
        List<Integer> orderList = new ArrayList<>(union);
        orderList.sort((o1, o2) -> Double.compare(
            marginalValue(o2, union),
            marginalValue(o1, union)
        ));

        Set<Integer> childOrders = new HashSet<>();
        int totalUnits = 0;
        for (Integer orderId : orderList) {
            int units = getOrderUnits(orderId);
            if (totalUnits + units > waveSizeUB) continue;
            childOrders.add(orderId);
            totalUnits += units;
        }

        // Se não atingiu LB, adiciona pedidos aleatórios ou por score heurístico
        if (totalUnits < waveSizeLB) {
            List<Integer> notUsed = new ArrayList<>();
            for (int i = 0; i < orders.size(); i++) {
                if (!childOrders.contains(i)) notUsed.add(i);
            }
            // Ordena por score heurístico (valor marginal)
            notUsed.sort((o1, o2) -> Double.compare(marginalValue(o2, childOrders), marginalValue(o1, childOrders)));
            for (Integer orderId : notUsed) {
                int units = getOrderUnits(orderId);
                if (totalUnits + units > waveSizeUB) continue;
                childOrders.add(orderId);
                totalUnits += units;
                if (totalUnits >= waveSizeLB) break;
            }
            // Se ainda não atingiu, adiciona aleatórios
            Collections.shuffle(notUsed, random);
            for (Integer orderId : notUsed) {
                if (childOrders.contains(orderId)) continue;
                int units = getOrderUnits(orderId);
                if (totalUnits + units > waveSizeUB) continue;
                childOrders.add(orderId);
                totalUnits += units;
                if (totalUnits >= waveSizeLB) break;
            }
        }

        // Monta vetor de genes
        boolean[] genes = new boolean[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            genes[i] = childOrders.contains(i);
        }
        return new Individual[] { new Individual(genes) };
    }

    // Valor marginal: incremento de itens/coletor ao adicionar o pedido
    private double marginalValue(int orderId, Set<Integer> currentOrders) {
        Set<Integer> items = new HashSet<>();
        for (Integer o : currentOrders) {
            items.addAll(orders.get(o).keySet());
        }
        Set<Integer> newItems = new HashSet<>(orders.get(orderId).keySet());
        newItems.removeAll(items);
        int newItemCount = newItems.size();
        int units = getOrderUnits(orderId);
        // Aproximação: número de novos itens por unidade
        return units == 0 ? 0 : (double) newItemCount / units;
    }

    private int getOrderUnits(int orderId) {
        Map<Integer, Integer> orderItems = orders.get(orderId);
        return orderItems.values().stream().mapToInt(Integer::intValue).sum();
    }
}

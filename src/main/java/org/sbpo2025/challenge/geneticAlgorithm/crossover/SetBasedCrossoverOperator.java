package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.*;

/**
 * Operador Set-Based (Intersection–Union) Crossover.
 * Gera filhos a partir da interseção e união dos pedidos dos pais, priorizando sinergia.
 */
public class SetBasedCrossoverOperator implements CrossoverOperator {
    private final Random random;
    private final int waveSizeLB;
    private final int waveSizeUB;
    private final List<Map<Integer, Integer>> orders;
    private final List<Map<Integer, Integer>> aisles;

    public SetBasedCrossoverOperator(Random random, int waveSizeLB, int waveSizeUB, List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles) {
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
        Set<Integer> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);
        Set<Integer> complement = new HashSet<>(union);
        complement.removeAll(intersection);

        // Calcula sinergia de cada pedido do complemento
        Map<Integer, Integer> synergy = new HashMap<>();
        for (Integer orderId : complement) {
            if (orderId < orders.size()) {
                synergy.put(orderId, computeSynergy(orderId));
            } else {
                synergy.put(orderId, 0);
            }
        }
        // Ordena pedidos do complemento por sinergia decrescente
        List<Integer> complementSorted = new ArrayList<>(complement);
        complementSorted.sort((a, b) -> Integer.compare(synergy.get(b), synergy.get(a)));

        // Gera filho baseando-se na interseção
        Set<Integer> childOrders = new HashSet<>(intersection);
        int totalUnits = getTotalUnits(childOrders);
        int idx = 0;
        // Complementa até LB
        while (totalUnits < waveSizeLB && idx < complementSorted.size()) {
            Integer orderId = complementSorted.get(idx++);
            if (orderId < orders.size()) {
                childOrders.add(orderId);
                totalUnits = getTotalUnits(childOrders);
            }
        }
        // Se ainda não atingiu LB, adiciona aleatórios do complemento restante
        List<Integer> left = new ArrayList<>();
        if (idx < complementSorted.size()) {
            left.addAll(complementSorted.subList(idx, complementSorted.size()));
        }
        Collections.shuffle(left, random);
        for (Integer orderId : left) {
            if (totalUnits >= waveSizeLB) break;
            if (orderId < orders.size()) {
                childOrders.add(orderId);
                totalUnits = getTotalUnits(childOrders);
            }
        }
        // Se excedeu UB, remove pedidos de menor contribuição
        while (totalUnits > waveSizeUB && !childOrders.isEmpty()) {
            Integer toRemove = getLowestContributionOrder(childOrders);
            childOrders.remove(toRemove);
            totalUnits = getTotalUnits(childOrders);
        }
        // Monta vetor de genes
        boolean[] genes = new boolean[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            genes[i] = childOrders.contains(i);
        }
        return new Individual[] { new Individual(genes) };
    }

    // Sinergia: número de itens do pedido presentes em corredores já cobertos pelo núcleo
    private int computeSynergy(int orderId) {
        if (orderId >= orders.size()) return 0;
        Set<Integer> items = orders.get(orderId).keySet();
        Set<Integer> coveredAisles = new HashSet<>();
        for (int aisleId = 0; aisleId < aisles.size(); aisleId++) {
            Map<Integer, Integer> aisleItems = aisles.get(aisleId);
            for (Integer item : items) {
                if (aisleItems.containsKey(item)) {
                    coveredAisles.add(aisleId);
                }
            }
        }
        return coveredAisles.size();
    }

    // Soma total de unidades dos pedidos
    private int getTotalUnits(Set<Integer> orderIds) {
        int sum = 0;
        for (Integer orderId : orderIds) {
            if (orderId < orders.size()) {
                Map<Integer, Integer> orderItems = orders.get(orderId);
                sum += orderItems.values().stream().mapToInt(Integer::intValue).sum();
            }
        }
        return sum;
    }

    // Remove o pedido de menor contribuição (menos unidades)
    private Integer getLowestContributionOrder(Set<Integer> orderIds) {
        Integer minOrder = null;
        int minUnits = Integer.MAX_VALUE;
        for (Integer orderId : orderIds) {
            if (orderId < orders.size()) {
                Map<Integer, Integer> orderItems = orders.get(orderId);
                int units = orderItems.values().stream().mapToInt(Integer::intValue).sum();
                if (units < minUnits) {
                    minUnits = units;
                    minOrder = orderId;
                }
            }
        }
        return minOrder;
    }
}

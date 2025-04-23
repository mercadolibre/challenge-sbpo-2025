package org.sbpo2025.challenge.geneticAlgorithm;

import org.sbpo2025.challenge.ChallengeSolution;
import java.util.*;

/**
 * Representa um indivíduo na população do algoritmo genético.
 * Cada indivíduo é um vetor binário onde cada posição representa um pedido (1=incluído, 0=excluído).
 */
public class Individual implements Comparable<Individual> {
    private boolean[] genes; // Genótipo: vetor de bits onde cada posição representa um pedido
    private Set<Integer> selectedOrders; // Pedidos selecionados (apenas os com gene=1)
    private Set<Integer> visitedAisles; // Corredores visitados (calculados durante a decodificação)
    private double fitness; // Valor da função objetivo (unidades / corredores)
    private boolean isFeasible; // Indica se o indivíduo representa uma solução viável
    private int totalUnits; // Total de unidades coletadas nos pedidos selecionados
    private double mutationRateGene; // Gene de auto-adaptação: taxa de mutação individual
    private double crossoverRateGene; // Gene de auto-adaptação: taxa de crossover individual
    private static final double MUTATION_GENE_MIN = 0.001;
    private static final double MUTATION_GENE_MAX = 0.15;
    private static final double CROSSOVER_GENE_MIN = 0.5;
    private static final double CROSSOVER_GENE_MAX = 1.0;

    /**
     * Cria um novo indivíduo aleatório
     * @param numOrders Número total de pedidos disponíveis
     * @param random Gerador de números aleatórios
     */
    public Individual(int numOrders, Random random) {
        this.genes = new boolean[numOrders];
        this.selectedOrders = new HashSet<>();
        this.visitedAisles = new HashSet<>();

        // Inicializa com genes aleatórios
        for (int i = 0; i < numOrders; i++) {
            this.genes[i] = random.nextBoolean();
            if (this.genes[i]) {
                this.selectedOrders.add(i);
            }
        }

        this.fitness = 0.0;
        this.isFeasible = false;
        this.totalUnits = 0;
        this.mutationRateGene = MUTATION_GENE_MIN + (MUTATION_GENE_MAX - MUTATION_GENE_MIN) * random.nextDouble();
        this.crossoverRateGene = CROSSOVER_GENE_MIN + (CROSSOVER_GENE_MAX - CROSSOVER_GENE_MIN) * random.nextDouble();
    }

    /**
     * Cria um indivíduo a partir de uma solução existente (ex: solução do CP-SAT)
     * @param solution Solução existente
     * @param numOrders Número total de pedidos disponíveis
     */
    public Individual(ChallengeSolution solution, int numOrders) {
        this.genes = new boolean[numOrders];
        this.selectedOrders = new HashSet<>(solution.orders());
        this.visitedAisles = new HashSet<>(solution.aisles());

        // Inicializa os genes de acordo com os pedidos selecionados
        for (int orderId : this.selectedOrders) {
            if (orderId < numOrders) {
                this.genes[orderId] = true;
            }
        }

        this.fitness = 0.0; // Será calculado durante a avaliação
        this.isFeasible = true; // Assumimos que a solução passada é viável
        this.totalUnits = 0; // Será calculado durante a avaliação
        this.mutationRateGene = MUTATION_GENE_MIN + (MUTATION_GENE_MAX - MUTATION_GENE_MIN) * Math.random();
        this.crossoverRateGene = CROSSOVER_GENE_MIN + (CROSSOVER_GENE_MAX - CROSSOVER_GENE_MIN) * Math.random();
    }

    /**
     * Cria um indivíduo a partir de um vetor de genes específico
     * @param genes Vetor de genes (true=pedido incluído, false=pedido excluído)
     */
    public Individual(boolean[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
        this.selectedOrders = new HashSet<>();
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                this.selectedOrders.add(i);
            }
        }
        this.visitedAisles = new HashSet<>();
        this.fitness = 0.0;
        this.isFeasible = false;
        this.totalUnits = 0;
        this.mutationRateGene = MUTATION_GENE_MIN + (MUTATION_GENE_MAX - MUTATION_GENE_MIN) * Math.random();
        this.crossoverRateGene = CROSSOVER_GENE_MIN + (CROSSOVER_GENE_MAX - CROSSOVER_GENE_MIN) * Math.random();
    }

    /**
     * Cria um novo indivíduo vazio
     * @param numOrders Número total de pedidos disponíveis
     */
    public Individual(int numOrders) {
        this.genes = new boolean[numOrders];
        this.selectedOrders = new HashSet<>();
        this.visitedAisles = new HashSet<>();
        this.fitness = 0.0;
        this.isFeasible = false;
        this.totalUnits = 0;
        this.mutationRateGene = MUTATION_GENE_MIN + (MUTATION_GENE_MAX - MUTATION_GENE_MIN) * Math.random();
        this.crossoverRateGene = CROSSOVER_GENE_MIN + (CROSSOVER_GENE_MAX - CROSSOVER_GENE_MIN) * Math.random();
    }

    // Getters and setters
    public boolean[] getGenes() {
        return genes;
    }

    public void setGene(int index, boolean value) {
        if (index >= 0 && index < genes.length) {
            if (value && !genes[index]) {
                selectedOrders.add(index);
                genes[index] = true;
            } else if (!value && genes[index]) {
                selectedOrders.remove(index);
                genes[index] = false;
            }
        }
    }

    public Set<Integer> getSelectedOrders() {
        return selectedOrders;
    }

    public Set<Integer> getVisitedAisles() {
        return visitedAisles;
    }

    public void setVisitedAisles(Set<Integer> aisles) {
        this.visitedAisles = new HashSet<>(aisles);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public boolean isFeasible() {
        return isFeasible;
    }

    public void setFeasible(boolean feasible) {
        isFeasible = feasible;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public int getNumOrders() {
        return genes.length;
    }

    public int getNumSelectedOrders() {
        return selectedOrders.size();
    }

    public int getNumVisitedAisles() {
        return visitedAisles.size();
    }

    public double getMutationRateGene() {
        return mutationRateGene;
    }

    public void setMutationRateGene(double value) {
        this.mutationRateGene = Math.max(MUTATION_GENE_MIN, Math.min(MUTATION_GENE_MAX, value));
    }

    public double getCrossoverRateGene() {
        return crossoverRateGene;
    }

    public void setCrossoverRateGene(double value) {
        this.crossoverRateGene = Math.max(CROSSOVER_GENE_MIN, Math.min(CROSSOVER_GENE_MAX, value));
    }

    /**
     * Converte o indivíduo em uma solução para o problema
     * @return Solução correspondente ao indivíduo
     */
    public ChallengeSolution toSolution() {
        return new ChallengeSolution(selectedOrders, visitedAisles);
    }

    /**
     * Compara dois indivíduos com base no fitness (para ordenação decrescente)
     */
    @Override
    public int compareTo(Individual other) {
        // Se ambos são viáveis, compara por fitness
        if (this.isFeasible && other.isFeasible) {
            return Double.compare(other.fitness, this.fitness);
        }
        // Se apenas um é viável, ele é melhor
        if (this.isFeasible && !other.isFeasible) {
            return -1;
        }
        if (!this.isFeasible && other.isFeasible) {
            return 1;
        }
        // Se nenhum é viável, compara por fitness mesmo assim
        return Double.compare(other.fitness, this.fitness);
    }

    /**
     * Cria uma cópia do indivíduo atual
     * @return Cópia do indivíduo
     */
    public Individual copy() {
        Individual copy = new Individual(this.genes.length);
        copy.genes = Arrays.copyOf(this.genes, this.genes.length);
        copy.selectedOrders = new HashSet<>(this.selectedOrders);
        copy.visitedAisles = new HashSet<>(this.visitedAisles);
        copy.fitness = this.fitness;
        copy.isFeasible = this.isFeasible;
        copy.totalUnits = this.totalUnits;
        copy.mutationRateGene = this.mutationRateGene;
        copy.crossoverRateGene = this.crossoverRateGene;
        return copy;
    }

    // Métodos para suportar mutações em genes inteiros e contínuos (implementação segura)
    public int[] getIntGenes() {
        throw new UnsupportedOperationException("Indivíduo binário não suporta genes inteiros");
    }

    public void setIntGene(int index, int value) {
        throw new UnsupportedOperationException("Indivíduo binário não suporta genes inteiros");
    }

    public double[] getDoubleGenes() {
        throw new UnsupportedOperationException("Indivíduo binário não suporta genes contínuos");
    }

    public void setDoubleGene(int index, double value) {
        throw new UnsupportedOperationException("Indivíduo binário não suporta genes contínuos");
    }

    @Override
    public String toString() {
        return "Individual{" +
                "selectedOrders=" + selectedOrders.size() +
                ", visitedAisles=" + visitedAisles.size() +
                ", fitness=" + fitness +
                ", isFeasible=" + isFeasible +
                ", totalUnits=" + totalUnits +
                '}';
    }
}

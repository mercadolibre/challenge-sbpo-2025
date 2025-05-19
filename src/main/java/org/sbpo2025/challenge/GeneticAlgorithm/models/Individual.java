package org.sbpo2025.challenge.GeneticAlgorithm.models;

/**
 * Representa um indivíduo (cromossomo) na população do GA.
 * Contém genes para pedidos (orderGenes) e corredores (aisleGenes),
 * além do valor de fitness computado.
 */
public class Individual implements Cloneable {
    private boolean[] orderGenes;  // Genes de pedidos
    private boolean[] aisleGenes;  // Genes de corredores
    private double fitness;        // Fitness do indivíduo

    /**
     * Construtor principal que inicializa os genes com valores falsos.
     * @param numOrders  número de pedidos (tamanho do vetor orderGenes)
     * @param numAisles  número de corredores (tamanho do vetor aisleGenes)
     */
    public Individual(int numOrders, int numAisles) {
        this.orderGenes = new boolean[numOrders];
        this.aisleGenes = new boolean[numAisles];
        this.fitness = Double.NaN;  // Fitness ainda não calculado
    }

    // Getters e Setters
    public boolean[] getOrderGenes() {
        return orderGenes;
    }

    public void setOrderGenes(boolean[] orderGenes) {
        this.orderGenes = orderGenes;
    }

    public boolean[] getAisleGenes() {
        return aisleGenes;
    }

    public void setAisleGenes(boolean[] aisleGenes) {
        this.aisleGenes = aisleGenes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Cria uma cópia profunda do indivíduo, incluindo genes e fitness.
     */
    @Override
    public Individual clone() {
        try {
            Individual copy = (Individual) super.clone();
            copy.orderGenes = orderGenes.clone();
            copy.aisleGenes = aisleGenes.clone();
            // fitness é primitivo, cópia por valor
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone não suportado", e);
        }
    }

    /**
     * Exibe indivíduo para debug: genes e fitness.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Individual{fitness=").append(fitness).append(", orders=[");
        for (boolean g : orderGenes) sb.append(g ? "1" : "0");
        sb.append("], aisles=[");
        for (boolean g : aisleGenes) sb.append(g ? "1" : "0");
        sb.append("]}");
        return sb.toString();
    }
}

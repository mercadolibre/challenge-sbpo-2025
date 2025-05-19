package org.sbpo2025.challenge.GeneticAlgorithm.population;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import org.sbpo2025.challenge.GeneticAlgorithm.config.GAConfiguration;
import org.sbpo2025.challenge.GeneticAlgorithm.evaluator.FitnessEvaluator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // Para inicializar população
import java.util.Collections; // Para embaralhar pedidos na inicialização
import java.util.Random; // Para decisões aleatórias

public class PopulationManager {
    private final GAConfiguration gaConfiguration;
    public final List<Map<Integer, Integer>> ordersData;
    public final List<Map<Integer, Integer>> aislesData;
    public final int nItems;
    public final int waveSizeLB;
    public final int waveSizeUB;
    private final int numOrders;
    private final int numAisles;
    private final FitnessEvaluator fitnessEvaluator; // Para calcular fitness após inicialização
    private final Random random;

    public PopulationManager(GAConfiguration gaConfiguration,
                             List<Map<Integer, Integer>> ordersData,
                             List<Map<Integer, Integer>> aislesData,
                             int nItems, int waveSizeLB, int waveSizeUB,
                             FitnessEvaluator fitnessEvaluator, Random random) {
        this.gaConfiguration = gaConfiguration;
        this.ordersData = ordersData;
        this.aislesData = aislesData;
        this.nItems = nItems;
        this.waveSizeLB = waveSizeLB;
        this.waveSizeUB = waveSizeUB;
        this.numOrders = ordersData.size();
        this.numAisles = aislesData.size();
        this.fitnessEvaluator = fitnessEvaluator;
        this.random = random;
    }

    /**
     * Inicializa a população com indivíduos.
     * Conforme Seção 3 do roteiro.
     */
    public List<Individual> initializePopulation() {
        List<Individual> population = new ArrayList<>(gaConfiguration.getPopulationSize());
        for (int i = 0; i < gaConfiguration.getPopulationSize(); i++) {
            Individual individual = new Individual(numOrders, numAisles);
            // Inicialização construtiva simples: seleciona aleatoriamente pedidos e corredores, mas garante pelo menos um de cada
            boolean algumPedido = false;
            boolean algumCorredor = false;
            for(int j=0; j<numOrders; j++) {
                boolean val = random.nextBoolean();
                individual.getOrderGenes()[j] = val;
                if (val) algumPedido = true;
            }
            for(int j=0; j<numAisles; j++) {
                boolean val = random.nextBoolean();
                individual.getAisleGenes()[j] = val;
                if (val) algumCorredor = true;
            }
            // Garante pelo menos um pedido e um corredor
            if (!algumPedido && numOrders > 0) individual.getOrderGenes()[0] = true;
            if (!algumCorredor && numAisles > 0) individual.getAisleGenes()[0] = true;
            fitnessEvaluator.evaluate(individual, ordersData, aislesData, waveSizeLB, waveSizeUB);
            population.add(individual);
        }
        return population;
    }

    /**
     * Encontra o melhor indivíduo na população.
     */
    public Individual findBest(List<Individual> population) {
        if (population == null || population.isEmpty()) {
            return null;
        }
        return Collections.min(population, (ind1, ind2) -> Double.compare(ind2.getFitness(), ind1.getFitness())); // Maior fitness é melhor
    }

    /**
     * Seleciona a próxima geração (pode incluir elitismo).
     * @param currentPopulation A população atual.
     * @param offspring A lista de filhos gerados.
     * @return A lista de indivíduos para a próxima geração.
     */
    public List<Individual> selectNextGeneration(List<Individual> currentPopulation, List<Individual> offspring) {
        // Elitismo: mantém o melhor da geração anterior
        List<Individual> nextGeneration = new ArrayList<>(gaConfiguration.getPopulationSize());
        if (!currentPopulation.isEmpty()) {
            Individual elite = findBest(currentPopulation);
            nextGeneration.add(elite.clone());
        }
        nextGeneration.addAll(offspring);
        // Garante o tamanho correto
        while(nextGeneration.size() < gaConfiguration.getPopulationSize()) {
            if (!currentPopulation.isEmpty()){
                nextGeneration.add(currentPopulation.get(random.nextInt(currentPopulation.size())).clone());
            } else {
                Individual randomInd = new Individual(numOrders, numAisles);
                for(int j=0; j<numOrders; j++) randomInd.getOrderGenes()[j] = random.nextBoolean();
                for(int j=0; j<numAisles; j++) randomInd.getAisleGenes()[j] = random.nextBoolean();
                fitnessEvaluator.evaluate(randomInd, ordersData, aislesData, waveSizeLB, waveSizeUB);
                nextGeneration.add(randomInd);
            }
        }
        return nextGeneration.subList(0, gaConfiguration.getPopulationSize());
    }
}

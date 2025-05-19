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
    private final List<Map<Integer, Integer>> ordersData;
    private final List<Map<Integer, Integer>> aislesData;
    private final int nItems;
    private final int waveSizeLB;
    private final int waveSizeUB;
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
            // TODO: Implementar lógica da Seção 3: População Inicial
            // 1. Sequência aleatória de pedidos: ...
            // 2. Gere corredores: ...
            // 3. Se T<LB, tente adicionar pedidos ...

            // Exemplo de inicialização muito simples (genes aleatórios - NÃO USAR EM PRODUÇÃO)
            for(int j=0; j<numOrders; j++) individual.getOrderGenes()[j] = random.nextBoolean();
            for(int j=0; j<numAisles; j++) individual.getAisleGenes()[j] = random.nextBoolean();

            fitnessEvaluator.calculateFitness(individual); // Calcula fitness inicial
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
        // TODO: Implementar lógica de seleção da próxima geração, possivelmente com elitismo.
        // Exemplo simples: substituir toda a população (sem elitismo)
        List<Individual> nextGeneration = new ArrayList<>(gaConfiguration.getPopulationSize());
        nextGeneration.addAll(offspring);
        // Garantir que a população tenha o tamanho correto, preenchendo se necessário ou truncando.
        while(nextGeneration.size() < gaConfiguration.getPopulationSize()) {
             if (!currentPopulation.isEmpty()){
                nextGeneration.add(currentPopulation.get(random.nextInt(currentPopulation.size())).clone()); // Preenche com aleatórios da pop atual
             } else {
                 // Caso extremo: se currentPop está vazia e offspring não é suficiente.
                 // Criar novos indivíduos aleatórios (ou usar uma melhor estratégia)
                 Individual randomInd = new Individual(numOrders, numAisles);
                 for(int j=0; j<numOrders; j++) randomInd.getOrderGenes()[j] = random.nextBoolean();
                 for(int j=0; j<numAisles; j++) randomInd.getAisleGenes()[j] = random.nextBoolean();
                 fitnessEvaluator.calculateFitness(randomInd);
                 nextGeneration.add(randomInd);
             }
        }
        return nextGeneration.subList(0, gaConfiguration.getPopulationSize());
    }
}

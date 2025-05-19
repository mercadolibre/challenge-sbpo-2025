# Plano de Implementação do GA para SBPO 2025

Este documento organiza **em um único lugar** todas as extensões e componentes sugeridos para implementar o Algoritmo Genético (GA) em Java, conforme discutido.

---

## 1. Estrutura de Pacotes

```
org.sbpo2025.challenge
│
└── GeneticAlgorithm
    ├── models
    │   └── Individual.java
    ├── config
    │   └── GAConfiguration.java
    ├── evaluator
    │   └── FitnessEvaluator.java
    ├── operators
    │   ├── SelectionOperator.java       (interface)
    │   ├── TournamentSelection.java     (implements SelectionOperator)
    │   ├── CrossoverOperator.java       (interface)
    │   ├── UniformCrossover.java        (implements CrossoverOperator)
    │   ├── MutationOperator.java        (interface)
    │   ├── BitFlipMutation.java         (implements MutationOperator)
    │   └── RepairOperator.java          (interface)
    │   └── WaveRepair.java              (implements RepairOperator)
    ├── population
    │   └── PopulationManager.java
    ├── runner
    │   └── GeneticAlgorithmExecutor.java
    └── util
        └── ChallengeSolutionConverter.java
```

---

## 2. Descrição de Cada Classe e Tarefas de Implementação

### 2.1 models/Individual.java

* [x] **Já implementado**: representação do cromossomo, clone(), toString(), getters/setters.

### 2.2 config/GAConfiguration.java

* [x] **Objetivo**: agrupar parâmetros do GA.
* Campos:

  * `int populationSize`
  * `int numberOfGenerations`
  * `double crossoverRate`
  * `double mutationRate`
  * `double alphaCoveragePenalty`
  * `double betaLBUBPenalty`
* Métodos: *getters*, possivelmente *builder* para facilitar instância.

### 2.3 evaluator/FitnessEvaluator.java

* [x] **Objetivo**: encapsular cálculo de fitness e penalidades.
* Métodos:

  * `double evaluate(Individual ind, List<Map<Integer,Integer>> orders, List<Map<Integer,Integer>> aisles, int LB, int UB)`
* Lógica:

  1. Cobertura de pedidos + penalidade (`alpha * itensNaoCobertos`).
  2. Penalidade LB/UB (`beta * violacao`).
  3. Cálculo da razão itens/corredores.

### 2.4 operators/SelectionOperator.java (interface)

* Método: `Individual select(List<Individual> population, double[] fitnesses)`.

### 2.5 operators/TournamentSelection.java

* Implemente torneio de tamanho configurável (e.g., k=3).

### 2.6 operators/CrossoverOperator.java (interface)

* Método: `Individual[] crossover(Individual p1, Individual p2)`.

### 2.7 operators/UniformCrossover.java

* Implementa uniform crossover em `orderGenes` e `aisleGenes`.

### 2.8 operators/MutationOperator.java (interface)

* Método: `void mutate(Individual ind)`.

### 2.9 operators/BitFlipMutation.java

* Aplica bit-flip com probabilidade `mutationRate` a cada gene.

### 2.10 operators/RepairOperator.java (interface)

* Método: `void repair(Individual ind, List<Map<Integer,Integer>> orders, List<Map<Integer,Integer>> aisles, int LB, int UB)`.

### 2.11 operators/WaveRepair.java

* Implemente:

  1. Garantir cobertura adicionando corredores.
  2. Se `T>UB`, remover pedidos de menor benefício.
  3. Se `T<LB`, adicionar pedidos de maior benefício.

### 2.12 population/PopulationManager.java

* 📌 **Responsabilidades**:

  * `List<Individual> initializePopulation()` seguindo heurística construtiva.
  * `List<Individual> selectNextGeneration(List<Individual> current, List<Individual> offspring)` (elitismo opcional).
  * `Individual findBest(List<Individual> pop)`.

### 2.13 runner/GeneticAlgorithmExecutor.java

* 📌 **Fluxo**:

  1. Carrega configuração (`GAConfiguration`).
  2. Inicializa população via `PopulationManager`.
  3. Loop de gerações:

     * Seleção p/ pais via `SelectionOperator`.
     * Crossover via `CrossoverOperator`.
     * Mutação via `MutationOperator`.
     * Reparo via `RepairOperator`.
     * Avaliação via `FitnessEvaluator`.
     * Geração da próxima população (elitismo opcional).
  4. Converte melhor indivíduo para `ChallengeSolution` via `ChallengeSolutionConverter`.
* Métodos faltantes a implementar: `initializePopulation()`, `calculateFitness()`, `selectParent()`, `crossover()`, `mutate()`, `repair()`, `convertToChallengeSolution()` (utilizar `ChallengeSolutionConverter`).

### 2.14 util/ChallengeSolutionConverter.java

* 📌 **Objetivo**: mapear `boolean[]` → `Set<Integer>` para pedidos e corredores.
* Método: `ChallengeSolution convert(Individual ind)`.

---

## 3. Boas Práticas e Extras

* **Configuração Externa**: considerar carregar `GAConfiguration` de um arquivo `.properties` ou JSON.
* **Logging**: integrar SLF4J para registrar melhor e média de fitness por geração.
* **Paralelismo**: avaliar `parallelStream()` em `evaluatePopulation()` se instâncias forem grandes.
* **Testes**:

  * Unit tests para cada operador.
  * Cenários de fitness com instâncias pequenas (2–5 pedidos).

---

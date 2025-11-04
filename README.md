# challenge-sbpo-2025

Welcome to the Mercado Libre First Optimization Challenge repository! This challenge is part of the [LVII Brazilian Symposium on Operations Research (SBPO 2025)](https://sbpo2025.galoa.com.br/sbpo-2025/page/5407-home). For further details, please read the post on Medium ([Portuguese version](https://medium.com/mercadolibre-tech/desafio-mercado-livre-de-otimiza%C3%A7%C3%A3o-3a4009607ee3); [Spanish version](https://medium.com/mercadolibre-tech/primer-desaf%C3%ADo-mercado-libre-de-optimizaci%C3%B3n-e8dad236054c)).
In this repository, you will find the base code for the framework, documentation, and other resources related to the challenge.

## Change Log

- **03-11-2025**: Best solutions across all three challenge phases are now available, including [individual solution files](best_solutions/) and a [`best_objectives.csv`](best_solutions/best_objectives.csv) summary with the best (maximum) objective value for each instance.

**Final Remarks**: We would like to extend our deepest gratitude to all participants who made this challenge an incredible success. Your dedication, creativity, and technical excellence have been truly inspiring. We also wish to thank the SBPO organizing team for their outstanding support in making this challenge possible.

- **08-09-2025**: [Final phase results](results/final_phase.pdf) are now available. See section [Challenge Results Explanation](#challenge-results-explanation) for more details.
- **08-09-2025**: Dataset `X` is now available.
- **26-06-2025**: [Preliminary qualification phase results](results/qualification_phase.pdf) are now available. See section [Challenge Results Explanation](#challenge-results-explanation) for more details.
- **16-04-2025**: [Sprint phase results](results/sprint_phase.pdf) updated. See section [Challenge Results Explanation](#challenge-results-explanation) for more details.
- **15-04-2025**: [Sprint phase results](results/sprint_phase.pdf) are now available.
- **15-04-2025**: Dataset `B` is now available.
- **05-03-2025**: Updated the challenge rules to clarify that, due to allowing a multithread environment, no seed for random generation will be provided.
- **27-02-2025**: Updated the challenge rules to include specific details of the computer environment in which the challenge will be run.
- **21-02-2025**: Corrected OR-Tools version to 9.11.
- **17-01-2025**: Base framework code, documentation and dataset `A`.

## Challenge Results Explanation

### Understanding Your Score

Below is a general explanation of the different scores you may see in the rankings (for a detailed explanation, please refer to the [challenge rules](docs/es_challenge_rules.pdf)):

#### Positive Score

A positive score indicates successful submissions that produced valid solutions, although there may be some instances that produced invalid solutions or errors.

#### Zero Score

Teams with a score of 0 could have encountered one or more of the following issues:

- **Compilation Success, no valid solutions**: your code compiled successfully, but no solutions met the feasibility criteria across any test cases.
- **Timeouts**: your program successfully compiled but exceeded the time limit (600 seconds) on the test instances.
- **Empty output files**: your program ran but produced empty output files or failed to generate any output.
- **Invalid Format**: your outputs did not follow the required format and could not be processed by the evaluation system.

#### Negative Score

Teams with a negative score typically encountered:

- **Compilation errors**: the submitted code failed to compile using the standard Maven build process. Common causes include:
    - Incompatible Java or library (e.g., CPLEX or OR-Tools) versions
    - Missing files or classes
    - Syntax errors
    - References to libraries that weren't included in the submission
    - Dependency issues

- **Runtime errors**: the program compiled but encountered errors during execution, such as:
    - Null pointer exceptions
    - Array index out of bounds
    - Class not found exceptions
    - Other runtime exceptions

## Challenge Rules and Problem Description

Spanish and Portuguese versions of the challenge rules and problem description can be found in the `docs` directory:

- **Spanish**:
  - [Problem description](docs/es_problem_description.pdf)
  - [Challenge rules](docs/es_challenge_rules.pdf)


- **Portuguese**:
  - [Problem description](docs/pt_problem_description.pdf)
  - [Challenge rules](docs/pt_challenge_rules.pdf)

## Project Structure

- `src/main/java/org/sbpo2025/challenge`
  - `Challenge.java` ⟶ Main Java class for reading an input, solving the challenge, and writing the output.
  - `ChallengeSolver.java` ⟶ Java class responsible for solving the wave order picking problem. Most of the solving logic should be implemented here.
  - `ChallengeSolution.java` ⟶ Java class representing the solution to the wave order picking problem.
- `datasets/` ⟶ Directory containing input instance files.
  - `a/` ⟶ Dataset A with 20 instances.
  - `b/` ⟶ Dataset B with 15 instances.
  - `x/` ⟶ Dataset X with 15 instances.
- `run_challenge.py` ⟶ Python script to compile code, run benchmarks, and evaluate solutions.
- `checker.py` ⟶ Python script for evaluating the feasibility and objective value of solutions.

## Prerequisites

- Java 17
- Maven
- Python 3.8 or higher
- CPLEX 22.11 (optional)
- OR-Tools 9.11 (optional)

## Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/mercadolibre/challenge-sbpo-2025
    ```
2. Set the paths to CPLEX and OR-Tools libraries in `run_challenge.py` if needed, e.g.:
    ```sh
    cplex_path = "$HOME/CPLEX_Studio2211/opl/bin/arm64_osx/"
    or_tools_path = "$HOME/Documents/or-tools/build/lib/"
    ```

## Usage

### Running the challenge

To compile the code and run benchmarks, use the following command:
```sh
python run_challenge.py <source_folder> <input_folder> <output_folder>
```
Where `<source_folder>` is the path to the Java source code, more specifically, where the `pom.xml` file is located.

In order to run this script you will need the `timeout` (or `gtimeout` on macOS) command installed. You can install it using `apt-get install coreutils` (or equivalent) on Linux or `brew install coreutils` on macOS.

### Checking solution viability

To check the feasibility and objective value of a solution, use the following command:
```sh
python checker.py <input_file> <solution_file>
```

## Examples

1. Compile and run benchmarks:
    ```sh
    python run_challenge.py src/main/java/org/sbpo2025/challenge src/main/resources/instances output
    ```
   
2. To check solution viability run the script below:
    ```sh
    chmod +x check_all.sh
    ./check_all
    ```
    

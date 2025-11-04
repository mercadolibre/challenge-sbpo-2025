#!/bin/bash
input_folder="datasets/b"
output_folder="outputs/dataset_b"

count_true=0
count_false=0

for input_file in "$input_folder"/*.txt; do
    instance_name=$(basename "$input_file")  # Pega apenas o nome do arquivo
    output_file="$output_folder/$instance_name"

    echo "Verificando $instance_name..."
    
    result=$(python3 checker.py "$input_file" "$output_file")

    is_feasible=$(echo "$result" | grep "Is solution feasible")
    objective_value=$(echo "$result" | grep "Objective function value")

    echo "$is_feasible"
    echo "$objective_value"

    if echo "$is_feasible" | grep -q "True"; then
        ((count_true++))
    else
        ((count_false++))
    fi

    echo "-----------------------------"
done

echo "Total de instâncias que deram TRUE: $count_true"
echo "Total de instâncias que deram FALSE: $count_false"

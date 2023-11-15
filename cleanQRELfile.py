input_file_path = './PerformanceEvaluatedFile/qrel-msmarco-2019.txt'
output_file_path = './PerformanceEvaluatedFile/cleaned-qrel-msmarco-2019.txt'

with open(input_file_path, 'r') as input_file, open(output_file_path, 'w') as output_file:
    for line in input_file:
        parts = line.split()
        # The relevance score is at index 3
        if len(parts) >= 4 and parts[3] != '0':
            output_file.write(line)

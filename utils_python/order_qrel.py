# Function to read data from a file and create a dictionary
def read_data(file_path):
    data_dict = {}
    with open(file_path, 'r') as file:
        for line in file:
            parts = line.strip().split()
            if len(parts) >= 3:
                qid, doc_id, relevance = parts[0], parts[2], int(parts[3])
                if qid not in data_dict:
                    data_dict[qid] = []
                data_dict[qid].append((doc_id, relevance))

    # Sort the data for each qid in descending order based on relevance
    for qid, doc_list in data_dict.items():
        data_dict[qid] = sorted(doc_list, key=lambda x: x[1], reverse=True)

    return data_dict

# File paths
scores_file_path = './qrel_file/cleaned-qrel-msmarco-2019.txt'
output_file_path = './qrel_file/ordered-qrel-msmarco-2019.txt'

# Step 1: Create a dictionary from your scores
scores = read_data(scores_file_path)

# Step 2: Write the sorted data to the output file
with open(output_file_path, 'w') as output_file:
    for qid, doc_list in scores.items():
        for doc_id, relevance in doc_list:
            output_file.write(f"{qid} Q0 {doc_id} {relevance}\n")


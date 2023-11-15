# Function to read relevance scores from the qrel file
def read_qrel_relevance(file_path):
    qrel_relevance_dict = {}
    with open(file_path, 'r') as file:
        for line in file:
            parts = line.strip().split()
            if len(parts) >= 4:
                qid, doc_id, relevance = parts[0], (parts[2]), int(parts[3])
                if qid not in qrel_relevance_dict:
                    qrel_relevance_dict[qid] = {}
                qrel_relevance_dict[qid][doc_id] = relevance
    return qrel_relevance_dict

# Function to read data from a file and create a dictionary
def read_data(file_path):
    data_dict = {}
    with open(file_path, 'r') as file:
        for line in file:
            parts = line.strip().split()
            if len(parts) >= 3:
                qid, doc_id = parts[0], str(int(parts[2]))
                if qid not in data_dict:
                    data_dict[qid] = {}
                data_dict[qid][doc_id]=0
    return data_dict

# File paths
your_file_path = './PerformanceEvaluatedFile/DAATBM25.txt'
qrel_file_path = './PerformanceEvaluatedFile/cleaned-qrel-msmarco-2019.txt'
output_file_path = './PerformanceEvaluatedFile/DAATBM25withRelevance.txt'

# Step 1: Read data from your file and qrel file
your_data = read_data(your_file_path)
qrel_relevance_data = read_qrel_relevance(qrel_file_path)

for common in qrel_relevance_data.keys() & your_data.keys():
    docids = your_data[common]
    for doc_id in docids:
        relevance = qrel_relevance_data[common].get(doc_id, 0)
        your_data[common][doc_id] = relevance


with open(output_file_path, 'w') as output_file:
    for qid in your_data.keys():
        doc_list=your_data[qid]
        for doc_id, relevance in doc_list.items():
            output_file.write(f"{qid} Q0 {doc_id} {relevance}\n")

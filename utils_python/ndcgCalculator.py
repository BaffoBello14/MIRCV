import math

def read_data(file_path):
    system_ranking={}
    with open(file_path,'r') as file:
        for line in file:
            parts=line.strip().split()
            qid,doc_id=parts[0],str(int(parts[2]))
            if qid not in system_ranking:
                system_ranking[qid]=[]
            system_ranking[qid].append(doc_id)
        
    return system_ranking

def read_data_ground_true(file_path,keys):
    ground_true={}
    with open(file_path,'r') as file:
        for line in file :
            parts=line.strip().split()
            qid,doc_id,relevance=parts[0],str(int(parts[2])),int(parts[3])
            if qid not in keys:
                continue
            if qid not in ground_true:
                ground_true[qid]={}
            ground_true[qid][doc_id]=relevance
    return ground_true

system_rank_file="./relevance_file/DYNAMICPRUNINGTFIDFwithRelevance.txt"
ground_true_file="./qrel_file/ordered-qrel-msmarco-2019.txt"
system_ranking=read_data(system_rank_file)
ground_true=read_data_ground_true(ground_true_file,system_ranking.keys())
system_ranking


def dcg(relevance,k)->float:
    return relevance[0]+sum([relevance[i]/math.log(i+1,2)for i in range(1,min(k,len(relevance)))])
def ndcg(system_ranking,ground_true,k):
    relevances=[ground_true.get(rank,0)for rank in system_ranking]
    base_ideal=[i for i in ground_true.values()]
    return dcg(relevances,k)/dcg(base_ideal,k)


with open("NDCG_file/DYNAMICPRUNINGTFIDF_NDCG", 'w') as file:
    for qid in system_ranking.keys():
        file.write(qid + "\t" + str(ndcg(system_ranking[qid], ground_true[qid], 10)) + "\n")

    

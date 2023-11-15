file_path = 'qrel_file/ordered-qrel-msmarco-2019.txt'
output_path = 'qrel_file/id_counts.txt'

# Dizionario per tenere traccia del conteggio per ogni ID
id_counts = {}

# Leggere il file e aggiornare il conteggio per ogni ID
with open(file_path, 'r') as file:
    for line in file:
        id = line.split()[0]
        if id in id_counts:
            id_counts[id] += 1
        else:
            id_counts[id] = 1

# Scrivere i conteggi in un file di output
with open(output_path, 'w') as output_file:
    for id, count in id_counts.items():
        output_file.write(f"{id} {count}\n")

print(f"I conteggi degli ID sono stati salvati in: {output_path}")

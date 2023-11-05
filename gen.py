import tarfile
import random

# Lista di frasi casuali in inglese
test_collection = [
    "Hello, how are you beautiful?",
    "I love programming.",
    "The quick brown fox jumps over the lazy dog.",
    "Python is a versatile programming language.",
    "Today is a beautiful day.",
    "Learning a new language is fun.",
    "The sky is blue.",
    "I enjoy reading books.",
    "Coding is my passion.",
    "Coffee is my favorite drink."
]

# Genera il file .tsv con ID, tab e la frase
with open("test_collection.tsv", "w") as tsv_file:
    for i, frase in enumerate(test_collection, start=1):
        tsv_file.write(f"{i}\t{frase}\n")

# Crea un file .tar.gz e aggiunge il file .tsv
with tarfile.open("test_collection.tar.gz", "w:gz") as tar:
    tar.add("test_collection.tsv")

print("File .tar.gz creato con successo.")

# Multimedia Information Retrieval and Computer Vision Project

This repository contains the source code for the project developed as part of the Multimedia Information Retrieval and Computer Vision course, within the Master's Degree in Artificial Intelligence and Data Engineering at the University of Pisa for the academic year 2023/2024.

## Project Overview

The project implements an information retrieval system capable of efficiently processing, indexing, and querying large collections of textual data. It features a robust indexing system, effective query processing algorithms, and uses optimization techniques to ensure high performance and scalability.

## Features

- Implements the Single-Pass In-Memory Indexing (SPIMI) algorithm for efficient indexing.
- Supports various types of queries, including conjunctive and disjunctive queries.
- Employs optimization techniques such as LFU Caching and Skipping Blocks.
- Performance evaluation using standard TREC metrics.

## Getting Started

These instructions will help you set up and run the project on your local machine for development and testing purposes.

### Prerequisites

Ensure you have the following software installed:

- Java JDK 8 or higher
- Apache Maven

### Installation

To set up the project, follow these steps:

1. Clone the repository: `git clone https://github.com/BaffoBello14/SearchEngine`
2. Create a folder named "Collection" and insert the "collection.tar.gz" file
3. Build the project with Maven: `mvn clean install`
4. Execute: `java -jar target/SearchEngine-1.0-SNAPSHOT.jar`

   **Note:**
   If it's the first time running the application, it will prompt you to create the index. Follow the on-screen instructions.
   If you want to change the type of indexing just delete the "IndexData" folder.

### Testing

Run the automated tests for this system using: `mvn test`

### Performance Evaluation

1. Insert in the "Collection" folder the "msmarco-test2019-queries.tsv.gz" file
2. Execute: `java -cp target/SearchEngine-1.0-SNAPSHOT.jar it.unipi.MIRCV.PerformanceEvaluation.PerformanceEvaluationOfQueries`

### Authors

Giulio Bello
Federico Frati
Chang Liu

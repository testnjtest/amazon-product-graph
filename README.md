# Amazon Product Graph

A Java program that models Amazon "also bought" relationships as an undirected graph, then explores it.

## Features
- **Shortest path (BFS):** finds the shortest chain of related products between two product IDs. O(V + E)
- **Error handling:** reports when a product ID isn't in the graph
- **Adding products:** inserts a new product and links it to existing ones. O(n)
- **Centrality:** samples 100 random products and ranks the top 5 by average shortest-path length to all reachable products. O(V · (V + E))

## Data
Two CSV files, each with a header row:
- `amazon_edges.csv`: `from,to` product-ID pairs
- `amazon_meta.csv`: product metadata, with the ID in column 1 and the title in column 3

Update the file paths at the top of `main` to point to your copies.

## Run
```
javac FinalProductFix.java
java FinalProductFix
```

# Amazon Product Graph

A graph-based analysis tool for Amazon's "customers who also bought" data. It treats the product catalog as a network: each product is a node, and co-purchase patterns are the connections between them.

## Overview

The project models how products relate to one another based on real customer purchasing behavior. That is the same idea behind recommendation engines that suggest related items to shoppers.

- **Data processing:** builds a structured model of product relationships from raw product and purchase-relationship files.
- **Shortest paths:** finds the shortest "path" between any two products based on co-purchase patterns.
- **Influential products:** surfaces the top 5 products that are most closely connected to everything else in the catalog. Retailers use the same kind of analysis to decide which products to prioritize for merchandising, marketing or inventory.
- **Live updates:** new products can be added and immediately evaluated for how they connect into the existing catalog.

## Technical details

- **Parsing and graph construction:** loads tens of thousands of product records and their co-purchase relationships from raw CSV files into an in-memory graph (adjacency sets). Malformed or missing entries are handled.
- **Breadth-first search (BFS):** finds the shortest path between any two products. In other words, it answers "how many degrees of separation are there between two items based on customer purchasing behavior?"
- **Centrality analysis:** samples the network and calculates each product's average distance to every other product it can reach. It reports the 5 products with the lowest average distance as the most "central" or influential.
- **Dynamic updates:** new products and their relationships can be added on the fly and queried right away, simulating how a live catalog might change over time.
- **Complexity analysis:** each core function was analyzed in Big-O terms to reason about how it scales as the dataset grows.

| Function | Purpose | Time complexity |
|---|---|---|
| `bfs` | Shortest path between two products | O(V + E) |
| `computeCentrality` | Average shortest-path length per product | O(V · (V + E)) |
| `addProduct` | Insert a product and its links | O(n), where n = number of linked products |

## Data

Two CSV files, each with a header row:

- `amazon_edges.csv`: `from,to` product-ID pairs (co-purchase links)
- `amazon_meta.csv`: product metadata, with the product ID in column 1 and the title in column 3

Update the file paths at the top of `main` to point to your copies.

## Run

```
javac FinalProductFix.java
java FinalProductFix
```

import java.io.*;
import java.util.*;

public class FinalProductFix {

    static Map<Integer, Set<Integer>> graph = new HashMap<>();
    static Map<Integer, String> idToTitle = new HashMap<>();

    public static void main(String[] args) {
        loadAmazonData("/Users/naishasuda/IdeaProjects/FinalProject/src/amazon_edges.csv");
        loadMetaData("/Users/naishasuda/IdeaProjects/FinalProject/src/amazon_meta.csv");

        List<Integer> path = bfs(1, 30);
        if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path from 1 to 30:");
            for (int id : path) {
                System.out.println(id + ": " + idToTitle.getOrDefault(id, "Unknown Product"));
            }
        }

        System.out.println();
        System.out.println("Testing Errors");
        bfs(1000000000, 30);

        System.out.println();
        System.out.println("Adding New Products");
        int newProductId = 12345;
        Set<Integer> links = new HashSet<>(Arrays.asList(3, 5, 9));
        addProduct(newProductId, links);
        idToTitle.put(newProductId, "New Product");

        List<Integer> newPath = bfs(3, 12345);
        System.out.println("Path from 3 to 12345:");
        for (int id : newPath) {
            System.out.println(id + ": " + idToTitle.getOrDefault(id, "Unknown Product"));
        }

        System.out.println();
        displayTop5CentralProducts();
    }

    public static void loadAmazonData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 2) {
                    int from = Integer.parseInt(tokens[0].trim());
                    int to = Integer.parseInt(tokens[1].trim());
                    addEdge(from, to);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading edges: " + e.getMessage());
        }
    }

    public static void loadMetaData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", 6);
                if (tokens.length >= 3) {
                    try {
                        int id = Integer.parseInt(tokens[0].trim());
                        String title = tokens[2].trim();
                        idToTitle.put(id, title);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading metadata: " + e.getMessage());
        }
    }

    public static void addEdge(int from, int to) {
        graph.putIfAbsent(from, new HashSet<>());
        graph.putIfAbsent(to, new HashSet<>());
        graph.get(from).add(to);
        graph.get(to).add(from);
    }

    // Time complexity: O(V + E)
    public static List<Integer> bfs(int start, int target) {
        if (!graph.containsKey(start)) {
            System.out.println("This product was not found in graph");
            return new ArrayList<>();
        }
        if (!graph.containsKey(target)) {
            System.out.println("This product was not found in graph");
            return new ArrayList<>();
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> prevNode = new HashMap<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == target) break;

            for (int neighbor : graph.getOrDefault(current, new HashSet<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    prevNode.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<Integer> path = new LinkedList<>();
        Integer current = target;

        while (current != null && current != start) {
            path.add(0, current);
            current = prevNode.get(current);
        }

        if (current == null) return Collections.emptyList();
        path.add(0, start);
        return path;
    }

    // Complexity: O(V * (V + E))
    public static Map<Integer, Double> computeCentrality() {
        Map<Integer, Double> centralityMap = new HashMap<>();
        List<Integer> nodeList = new ArrayList<>(graph.keySet());
        Collections.shuffle(nodeList); // Shuffle for random sampling
        int limit = Math.min(100, nodeList.size()); // Cap sample size to 100 nodes

        System.out.println("Computing centrality for " + limit + " nodes...");

        for (int i = 0; i < limit; i++) {
            int node = nodeList.get(i);
            double totalDistance = 0;
            Map<Integer, Integer> distances = bfsDistances(node);

            if (distances.size() <= 1) {
                centralityMap.put(node, Double.MAX_VALUE);
                continue;
            }

            for (int dist : distances.values()) {
                totalDistance += dist;
            }

            double averageDistance = totalDistance / (distances.size() - 1);
            centralityMap.put(node, averageDistance);

            if (i % 10 == 0) {
                System.out.println("Processed " + (i + 1) + "/" + limit);
            }
        }

        System.out.println("Centrality computation finished.");
        return centralityMap;
    }

    public static Map<Integer, Integer> bfsDistances(int start) {
        Map<Integer, Integer> distances = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(start);
        distances.put(start, 0);
        visited.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            int currentDist = distances.get(current);

            for (int neighbor : graph.getOrDefault(current, new HashSet<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distances.put(neighbor, currentDist + 1);
                    queue.add(neighbor);
                }
            }
        }
        return distances;
    }

    public static void displayTop5CentralProducts() {
        Map<Integer, Double> centrality = computeCentrality();
        List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(centrality.entrySet());

        sortedEntries.sort(Comparator.comparingDouble(Map.Entry::getValue));

        System.out.println("Top 5 Most Central Products:");
        for (int i = 0; i < Math.min(5, sortedEntries.size()); i++) {
            int id = sortedEntries.get(i).getKey();
            double avgDist = sortedEntries.get(i).getValue();
            System.out.printf("%d: %s (Avg Shortest Path Length: %.2f)\n",
                    id, idToTitle.getOrDefault(id, "Unknown Product"), avgDist);
        }
    }

    // Time complexity: O(n), where n = number of also-bought products
    public static void addProduct(int Id, Set<Integer> otherids) {
        graph.putIfAbsent(Id, new HashSet<>());
        for (int l : otherids) {
            graph.putIfAbsent(l, new HashSet<>());
            graph.get(Id).add(l);
            graph.get(l).add(Id);
        }
    }
}

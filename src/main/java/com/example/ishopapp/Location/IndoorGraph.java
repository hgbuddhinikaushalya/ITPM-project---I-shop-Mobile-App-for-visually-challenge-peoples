package com.example.ishopapp.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class IndoorGraph {
    private Map<IndoorLocation, Map<IndoorLocation, Double>> graph = new HashMap<>();

    public void addNode(IndoorLocation node) {
        graph.putIfAbsent(node, new HashMap<>());
    }

    public void addEdge(IndoorLocation source, IndoorLocation destination, double weight) {
        graph.get(source).put(destination, weight);
        graph.get(destination).put(source, weight); // If bidirectional
    }

    public List<IndoorLocation> shortestPath(IndoorLocation start, IndoorLocation end) {
        Map<IndoorLocation, Double> distances = new HashMap<>();
        Map<IndoorLocation, IndoorLocation> previous = new HashMap<>();
        PriorityQueue<IndoorLocation> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (IndoorLocation node : graph.keySet()) {
            if (node.equals(start)) {
                distances.put(node, 0.0);
                queue.add(node);
            } else {
                distances.put(node, Double.POSITIVE_INFINITY);
            }
            previous.put(node, null);
        }

        while (!queue.isEmpty()) {
            IndoorLocation current = queue.poll();
            if (current.equals(end)) {
                List<IndoorLocation> path = new ArrayList<>();
                while (previous.get(current) != null) {
                    path.add(current);
                    current = previous.get(current);
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            }

            for (IndoorLocation neighbor : graph.get(current).keySet()) {
                double alt = distances.get(current) + graph.get(current).get(neighbor);
                if (alt < distances.get(neighbor)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    public static double calculateDistance(IndoorLocation location1, IndoorLocation location2) {
        double dx = location1.getX() - location2.getX();
        double dy = location1.getY() - location2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}

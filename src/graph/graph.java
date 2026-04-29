package graph;
import java.util.*;

public class graph {
public int node;
public List<List<int[]>> adj = new ArrayList<>();
public Set blockedEdge = new HashSet<>();
//creating
public graph(int node) {
this.node = node;
adj = new ArrayList<>();
blockedEdge = new HashSet<>();

for (int i = 0; i < node; i++) {
adj.add(new ArrayList<>());
}
}
//adding edges
public void addEdge(int n2, int n1, int w) {
adj.get(n2).
add(new int[]{n1, w});
adj.get(n1).
add(new int[]{n2, w});
}
//checking blocked edhges
public void blockEdge(int n2, int n1) {
blockedEdge.add(n2 + "-" + n1);
blockedEdge.add(n1 + "-" + n2);
}

public void printGraph() {
// Graph printing
System.out.println("\nGraph (Adjacency List):");
for (int i = 0; i < adj.size(); i++) {
System.out.print(i + " -> ");

for (int[] edge : adj.get(i)) {
int n1 = edge[0];
int w = edge[1];

if (blockedEdge.contains(i + "-" + n1)) {
System.out.print("(" + n1 + ", w=" + w + " BLOCKED) ");
} else {
System.out.print("(" + n1 + ", w=" + w + ") ");
}
}
System.out.println();
}
}
}

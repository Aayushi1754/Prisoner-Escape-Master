package graph;
import java.util.*;

public class Dijkstra
{
static class Pair implements Comparable<Pair>
// this is a java class that helps with comparison
// we use this because in the priority queue, the pairs will come in and we need to arrange them in ascending order
{
int node;
int path;
public Pair(int node, int path)
{
this.node = node;
this.path = path;
}
@Override
// this is for comparing in ascending order, for descending order we can reverse it
public int compareTo(Pair p2)
{
return Integer.compare(this.path, p2.path);
}
}

public static List shortestPath(ArrayList<edge>[] graph, node[] nodes, int src, int dest)
{
PriorityQueue<Pair> pq = new PriorityQueue<>(); // easy to compare
int n = graph.length;

int dist[] = new int[n];
boolean visited[] = new boolean[n];
int parent[] = new int[n];

Arrays.fill(dist, Integer.MAX_VALUE);
Arrays.fill(parent, -1);

if (nodes[src].
blocked) // source is blocked
{
System.out.println("Source blocked");
return new ArrayList<>();
}

dist[src] = 0;
pq.add(new Pair(src, 0));

while (!
pq.isEmpty())
{
Pair curr = pq.remove();

if (!visited[curr.node])
{
visited[curr.node] = true;
for (int i = 0; i < graph[curr.node].
size(); i++)
{
edge e = graph[curr.node].
get(i);
int u = curr.node;
int v = e.dest;
if (e.blocked || nodes[v].
blocked)
continue;

if (dist[u] != Integer.MAX_VALUE && dist[u] + e.weight < dist[v])
{
dist[v] = dist[u] + e.weight;
parent[v] = u;
pq.add(new Pair(v, dist[v]));
}
}
}
}

if (dist[dest] == Integer.MAX_VALUE) // no path found
{
return new ArrayList<>();
}

List path = new ArrayList<>();
for (int i = dest; i != -1; i = parent[i])
{
path.add(i);
}
Collections.reverse(path);
return path;
}
}
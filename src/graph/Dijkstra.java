package graph;
import java.util.*;

public class Dijkstra
{
    static class Pair implements Comparable<Pair>
    //yh java class hai joh comapre karne mei help karta hai
    //we use this kyunki pq mei joh aaygea voh pair wise lere or usko ascending mei dalnge
    {
        int node;
        int path;
        public Pair(int node, int path)
        {
            this.node = node;
            this.path = path;
        }
        @Override//it is comparing in ascending for decending do ulta
        public int compareTo(Pair p2)
        {
            return Integer.compare(this.path, p2.path);
        }
    }

    public static List<Integer> shortestPath(ArrayList<edge>[] graph, node[] nodes, int src, int dest)
    {
        PriorityQueue<Pair> pq = new PriorityQueue<>();//easy to compare
        int n = graph.length;

        int dist[] = new int[n];
        boolean visited[] = new boolean[n];
        int parent[] = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        if (nodes[src].blocked)//ratsa hi ni hai
        {
            System.out.println("Source blocked");
            return new ArrayList<>();
        }

        dist[src] = 0;
        pq.add(new Pair(src, 0));

        while (!pq.isEmpty())
        {
            Pair curr = pq.remove();

            if (!visited[curr.node])
            {
                visited[curr.node] = true;
                for (int i = 0; i < graph[curr.node].size(); i++)
                {
                    edge e = graph[curr.node].get(i);
                    int u = curr.node;
                    int v = e.dest;
                    if (e.blocked || nodes[v].blocked) 
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

        if (dist[dest] == Integer.MAX_VALUE)//no path found
        {
            return new ArrayList<>();
        }

        List<Integer> path = new ArrayList<>();
        for (int i = dest; i != -1; i = parent[i])
        {
            path.add(i);
        }
        Collections.reverse(path);
        return path;
    }
}
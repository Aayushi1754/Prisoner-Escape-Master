package graph;

public class edge {

    public int src;
    public int dest;
    public int weight;
    public boolean blocked;

    public edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        this.blocked = false;
    }
}
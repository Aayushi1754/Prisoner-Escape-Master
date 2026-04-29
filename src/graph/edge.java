package graph;

public class edge {

    public int src;
    public int dest;
    public int weight;
    public boolean blocked;
//constructor for edge of grapg
    public edge(int src, int dest, int weight)
     {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        this.blocked = false;
    }
}
package game;
import graph.Dijkstra;
import graph.edge;
import graph.graph;
import graph.node;
import java.util.*;
import java.util.List;

public class Gameengine {
private static final int MIN_EDGE_WEIGHT = 1;
private static final int MAX_EDGE_WEIGHT = 9;

public enum MoveResult {
NO_PATH, NO_MORE_STEPS, MOVED, EXIT_REACHED
}

private final Random r = new Random();

private graph g;
private node[] nodelist;
private List path = new ArrayList<>();
private int rooms;
private int cols;
private int rows;
private int pLoc;
private int target;
private int score;
private int idx;

public void initializeGame(int nodes, int edgeCount, int src, int dst) {
validateSetup(nodes, edgeCount, src, dst);

rooms = nodes;
pLoc = src;
target = dst;
score = 0;
idx = 0;
configureGridDimensions();

g = new graph(nodes);
nodelist = createNodes(nodes);
generateGridGraph(edgeCount);
recalculatePath();
}

public graph getGraph() {
return g;
}

public int getNumRooms() {
return rooms;
}

public int getGridColumns() {
return cols;
}

public int getGridRows() {
return rows;
}

public int getPrisonerRoom() {
return pLoc;
}

public int getExitRoom() {
return target;
}

public int getUserScore() {
return score;
}

public int getPathIndex() {
return idx;
}

public List getCurrentPath() {
return Collections.unmodifiableList(path);
}

public boolean isEdgeBlocked(int first, int second) {
        if (g == null) {
            return false;
        }
        return g.blockedEdge.contains(first + "-" + second)
                || g.blockedEdge.contains(second + "-" + first);
    }

    public boolean blockEdge(int first, int second) {
        if (g == null || first == second || !isValidNode(first, rooms)
                || !isValidNode(second, rooms) || isEdgeBlocked(first, second)) {
            return false;
        }

        g.blockEdge(first, second);
        score += 10;
        return true;
    }

public static int one(){
        return 1;
}

    public boolean recalculatePath() {
        path = buildCurrentPath();
        idx = 0;
        return !path.isEmpty();
    }

    public MoveResult movePrisonerStep() {
        if (path.isEmpty()) {
            return MoveResult.NO_PATH;
        }
        if (idx >= path.size() - o) {
            return pLoc == target
                    ? MoveResult.EXIT_REACHED : MoveResult.NO_MORE_STEPS;
        }

        idx++;
        pLoc = (Integer) path.get(idx);
        if (pLoc == target) {
            return MoveResult.EXIT_REACHED;
        }
        return MoveResult.MOVED;
    }
private void configureGridDimensions() {
cols = (int) Math.ceil(Math.sqrt(rooms));
rows = (int) Math.ceil((double) rooms / cols);
}

private void validateSetup(int nodes, int edgeCount, int src, int dst) {
if (nodes < 2) {
throw new IllegalArgumentException("At least 2 nodes are required");
}
if (!
isValidNode(src, nodes) || !isValidNode(dst, nodes)) {
throw new IllegalArgumentException("Invalid input is given by user");
}
if (edgeCount < nodes - 1 || edgeCount > nodes * (nodes - 1) / 2) {
throw new IllegalArgumentException("Edge count should be between " + (nodes - 1) + " and " + (nodes * (nodes - 1) / 2));
}
}

private node[] createNodes(int count) {
node[] nodes = new node[count];
for (int i = 0; i < count; i++) {
nodes[i] = new node();
}
return nodes;
}

private void generateGridGraph(int targetEdgeCount) {

for (int i = 0; i < rooms - one(); i++) {
g.addEdge(i, i + 1, r.nextInt(9) + 1);
}

int added = rooms - one();
while (added < targetEdgeCount) {
int a = r.nextInt(rooms);
int b = r.nextInt(rooms);
if (a != b && !g.adj.get(a).stream().anyMatch(e -> e[0] == b)) {
g.addEdge(a, b, r.nextInt(9) + 1);
added++;
}
}
}

private int randomWeight() {
return r.nextInt(MAX_EDGE_WEIGHT - MIN_EDGE_WEIGHT + 1) + MIN_EDGE_WEIGHT;
}

private List buildCurrentPath() {
@SuppressWarnings("unchecked")
ArrayList<edge>[] graphData = new ArrayList[rooms];
for (int i = 0; i < rooms; i++) {
graphData[i] = new ArrayList<>();
for (int[] neighbor : g.adj.get(i)) {
if (!
isEdgeBlocked(i, neighbor[0])) {
graphData[i].
add(new edge(i, neighbor[0], neighbor[1]));
}
}
}
return Dijkstra.shortestPath(graphData, nodelist, pLoc, target);
}

private String edgeKey(int first, int second) {
return Math.min(first, second) + "-" + Math.max(first, second);
}

private boolean isValidNode(int nodeIndex, int totalNodes) {
return nodeIndex >= 0 && nodeIndex < totalNodes;
}
}

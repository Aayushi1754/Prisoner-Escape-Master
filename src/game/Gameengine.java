package game;
import graph.Dijkstra;
import graph.edge;
import graph.graph;
import graph.node;
import java.util.*;

public class Gameengine {
private static final int MIN_EDGE_WEIGHT = 1;
private static final int MAX_EDGE_WEIGHT = 9;

public enum MoveResult {
NO_PATH, NO_MORE_STEPS, MOVED, EXIT_REACHED
}

private final Random r = new Random();

private graph g;
private node[] nodelist;
private List<Integer> path = new ArrayList<>();
private int rooms;
private int cols;
private int rows;
private int pLoc;
private int target;
private int score;
private int idx;
private int generatedEdges;

public void initializeGame(int nodes, int edgeCount, int src, int dst) {

rooms = nodes;
configureGridDimensions();
validateSetup(nodes, edgeCount, src, dst);
int normalizedEdgeCount = normalizeRequestedEdgeCount(edgeCount);
pLoc = src;
target = dst;
score = 0;
idx = 0;
generatedEdges = 0;

g = new graph(nodes);
nodelist = createNodes(nodes);
generatedEdges = generateGridGraph(normalizedEdgeCount);
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
public int getGeneratedEdges() {
return generatedEdges;
}

public List<Integer> getCurrentPath(){
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
                || !isValidNode(second, rooms) || isEdgeBlocked(first, second)
                || !isEdgePresent(first, second)) {
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
        if (idx >= path.size() - 1) {
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
int maxSegmentEdges = maxGridSegmentEdges();
if (edgeCount < nodes - 1) {
throw new IllegalArgumentException("Edge count should be at least " + (nodes - 1));
}
}

private node[] createNodes(int count) {
node[] nodes = new node[count];
for (int i = 0; i < count; i++) {
nodes[i] = new node();
}
return nodes;
}

private int generateGridGraph(int targetEdgeCount) {
for (int node = 1; node < rooms; node++) {
int parent = (node % cols == 0) ? node - cols : node - 1;
g.addEdge(parent, node, randomWeight());
}

List<int[]> candidates = buildAdjacentGridEdgeCandidates();
Collections.shuffle(candidates, r);

int added = rooms - one();
for (int[] candidate : candidates) {
if (added >= targetEdgeCount) {
break;
}
int a = candidate[0];
int b = candidate[1];
if (!isEdgePresent(a, b)) {
g.addEdge(a, b, randomWeight());
added++;
}
}
return added;
}

private int randomWeight() {
return r.nextInt(MAX_EDGE_WEIGHT - MIN_EDGE_WEIGHT + 1) + MIN_EDGE_WEIGHT;
}

private List<Integer> buildCurrentPath() {
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
private int maxGridSegmentEdges() {
return buildAdjacentGridEdgeCandidates().size();
}

private int normalizeRequestedEdgeCount(int edgeCount) {
return Math.min(edgeCount, maxGridSegmentEdges());
}

private List<int[]> buildAdjacentGridEdgeCandidates() {
List<int[]> candidates = new ArrayList<>();
for (int node = 0; node < rooms; node++) {
int row = node / cols;
int col = node % cols;

int right = node + 1;
if (col + 1 < cols && right < rooms) {
candidates.add(new int[]{node, right});
}

int down = node + cols;
if (row + 1 < rows && down < rooms) {
candidates.add(new int[]{node, down});
}

int downRight = node + cols + 1;
if (row + 1 < rows && col + 1 < cols && downRight < rooms) {
candidates.add(new int[]{node, downRight});
}

int downLeft = node + cols - 1;
if (row + 1 < rows && col - 1 >= 0 && downLeft < rooms) {
candidates.add(new int[]{node, downLeft});
}
}
return candidates;
}

private boolean isEdgePresent(int first, int second) {
for (int[] neighbor : g.adj.get(first)) {
if (neighbor[0] == second) {
return true;
}
}
return false;
}

private boolean isValidNode(int nodeIndex, int totalNodes) {
return nodeIndex >= 0 && nodeIndex < totalNodes;
}
}


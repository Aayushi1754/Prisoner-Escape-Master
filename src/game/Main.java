package game;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Gameengine engine = new Gameengine();

        try {
            System.out.println("Enter number of nodes:");
            int nodes = sc.nextInt();

            System.out.println("Enter number of edges:");
            int edges = sc.nextInt();

            System.out.println("Enter source node:");
            int source = sc.nextInt();

            System.out.println("Enter destination node:");
            int destination = sc.nextInt();

            engine.initializeGame(nodes, edges, source, destination);
            System.out.println("Generated graph. Initial shortest path: " + engine.getCurrentPath());

            while (true) {
                if (engine.getCurrentPath().isEmpty()) {
                    System.out.println("No path left. User wins!");
                    break;
                }

                Gameengine.MoveResult moveResult = engine.movePrisonerStep();
                if (moveResult == Gameengine.MoveResult.EXIT_REACHED) {
                    System.out.println("Prisoner reached exit. Computer wins!");
                    break;
                }
                if (moveResult == Gameengine.MoveResult.NO_PATH) {
                    System.out.println("No path left. User wins!");
                    break;
                }

                System.out.println("Prisoner is in room " + engine.getPrisonerRoom());

                System.out.println("Enter edge to block (u v):");
                int u = sc.nextInt();
                int v = sc.nextInt();

                if (!engine.blockEdge(u, v)) {
                    System.out.println("Invalid edge or edge already blocked.");
                    continue;
                }

                boolean hasPath = engine.recalculatePath();
                System.out.println("Blocked edge " + u + "-" + v);
                List<Integer> updatedPath = engine.getCurrentPath();
                System.out.println("Updated shortest path: " + updatedPath);

                if (!hasPath) {
                    System.out.println("No path left. User wins!");
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Invalid input: " + ex.getMessage());
        } finally {
            sc.close();
        }
    }
}
//java -cp bin ui.PrisonEscapeGame
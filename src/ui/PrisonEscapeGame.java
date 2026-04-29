package ui;
import game.Gameengine;
import game.Timer;
import game.useraction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


public class PrisonEscapeGame extends JFrame {
     public PrisonEscapeGame() {
        setTitle("Prison Escape - Dijkstra's Algorithm Game");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createUI();     // your existing UI method
        setVisible(true);
    }
    private static final int GRID_GAP = 110;
    private static final int GRID_PADDING_X = 90;
    private static final int GRID_PADDING_Y = 90;

    // Game components
    private JPanel gamePanel;
    private JLabel timerLabel, statusLabel, scoreLabel;
    private JButton startGameBtn, pauseBtn;

    // Game data
    private final Gameengine gameEngine = new Gameengine();
    private Map<Integer, Point> roomPositions = new HashMap<>();
    private BufferedImage prisonBackground;
    private int gridOriginX = GRID_PADDING_X;
    private int gridOriginY = GRID_PADDING_Y;
    private int currentGridGap = GRID_GAP;

    // Game state
    private int prisonerRoom = 0;
    private int exitRoom = 15;
    private int gameTime = Timer.GAME_DURATION_SECONDS;
    private int userScore = 0;
    private boolean gameRunning = false;
    private boolean prisonerMoving = false;
    private int pathIndex = 0;

    // Timers
    private javax.swing.Timer gameTimer;
    private javax.swing.Timer prisonerTimer;
    private javax.swing.Timer freezeTimer;
    private javax.swing.Timer pauseTimer;

    private void createUI() {
        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(50, 50, 50));

        startGameBtn = new JButton("Start New Game");
        startGameBtn.setBackground(Color.GREEN);
        startGameBtn.setForeground(Color.WHITE);
        startGameBtn.addActionListener(e -> startNewGame());

        pauseBtn = new JButton("Pause");
        pauseBtn.setBackground(Color.ORANGE);
        pauseBtn.setEnabled(false);
        pauseBtn.addActionListener(e -> togglePause());

        timerLabel = new JLabel("Time: 60");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        topPanel.add(startGameBtn);
        topPanel.add(pauseBtn);
        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);

        add(topPanel, BorderLayout.NORTH);

        // Game panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame((Graphics2D) g);
            }
        };
        gamePanel.setBackground(Color.BLACK);
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameRunning && !prisonerMoving) {
                    handleUserClick(e.getX(), e.getY());
                }
            }
        });
        add(gamePanel, BorderLayout.CENTER);
        gamePanel.setOpaque(false);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(30, 30, 30));

        statusLabel = new JLabel("<html><center>Prison Escape Game<br>" +
                               "Green = Prisoner | Blue = Exit | Red = Blocked<br>" +
                               "Click edges to block prisoner's path!</center></html>");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // Initialize timers
        gameTimer = Timer.createGameTimer(e -> updateGameTime());
        prisonerTimer = Timer.createPrisonerTimer(e -> movePrisoner());
        pauseTimer = Timer.createPauseTimer(e -> resumePrisonerMovement());
        freezeTimer = Timer.createFreezeTimer(e -> endFreeze());
    }

    private void startNewGame() {
    try {
        String nInput = JOptionPane.showInputDialog(this, "Enter number of nodes:");
        int n = Integer.parseInt(nInput);
        if (n < 2) {
            throw new IllegalArgumentException("At least 2 nodes are required");
        }

        String eInput = JOptionPane.showInputDialog(this, "Enter number of edges:");
        int edgeCount = Integer.parseInt(eInput);

        String srcInput = JOptionPane.showInputDialog(this, "Enter source node:");
        String destInput = JOptionPane.showInputDialog(this, "Enter destination node:");

        prisonerRoom = Integer.parseInt(srcInput);
        exitRoom = Integer.parseInt(destInput);
        gameEngine.initializeGame(n, edgeCount, prisonerRoom, exitRoom);

        generateRoomPositions();
        prisonBackground = null;

        gameTime = Timer.GAME_DURATION_SECONDS;
        userScore = gameEngine.getUserScore();
        gameRunning = true;
        prisonerMoving = false;
        pathIndex = gameEngine.getPathIndex();

        gameTimer.start();

        startGameBtn.setEnabled(false);
        pauseBtn.setEnabled(true);

        timerLabel.setText("Time: " + gameTime);
        scoreLabel.setText("Score: " + userScore);
        statusLabel.setText("Auto-generated grid graph with " + edgeCount + " weighted edges.");

        gamePanel.repaint();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage() != null ? ex.getMessage() : "Invalid input! Please try again.");
    }
}

    private void generateRoomPositions() {
        updateGridLayout();
        roomPositions.clear();
        for (int i = 0; i < getVisibleRoomCount(); i++) {
            int row = i / gameEngine.getGridColumns();
            int col = i % gameEngine.getGridColumns();
            int x = gridOriginX + (col * currentGridGap);
            int y = gridOriginY + (row * currentGridGap);
            roomPositions.put(i, new Point(x, y));
        }
    }

    private void updateGridLayout() {
        int panelWidth = Math.max(gamePanel.getWidth(), 620);
        int panelHeight = Math.max(gamePanel.getHeight(), 480);

        int horizontalSteps = Math.max(1, gameEngine.getGridColumns() - 1);
        int verticalSteps = Math.max(1, gameEngine.getGridRows() - 1);
        int maxGapX = Math.max(70, (panelWidth - (2 * GRID_PADDING_X)) / horizontalSteps);
        int maxGapY = Math.max(70, (panelHeight - (2 * GRID_PADDING_Y)) / verticalSteps);
        currentGridGap = Math.min(GRID_GAP, Math.min(maxGapX, maxGapY));

        int graphWidth = (gameEngine.getGridColumns() - 1) * currentGridGap;
        int graphHeight = (gameEngine.getGridRows() - 1) * currentGridGap;
        gridOriginX = (panelWidth - graphWidth) / 2;
        gridOriginY = (panelHeight - graphHeight) / 2;
    }

    private void ensurePrisonBackground() {
        int width = Math.max(gamePanel.getWidth(), 700);
        int height = Math.max(gamePanel.getHeight(), 520);

        if (prisonBackground != null &&
            prisonBackground.getWidth() == width &&
            prisonBackground.getHeight() == height) {
            return;
        }

        prisonBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = prisonBackground.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        GradientPaint wallGradient = new GradientPaint(0, 0, new Color(18, 24, 35), 0, height, new Color(7, 10, 18));
        g.setPaint(wallGradient);
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(55, 62, 74, 110));
        for (int row = 0; row < height; row += 42) {
            int offset = (row / 42) % 2 == 0 ? 0 : 28;
            for (int col = -offset; col < width; col += 56) {
                g.drawRoundRect(col, row, 54, 30, 6, 6);
            }
        }

        GradientPaint floorGradient = new GradientPaint(0, height - 150, new Color(34, 35, 40, 230),
            0, height, new Color(8, 8, 10, 255));
        g.setPaint(floorGradient);
        g.fillRect(0, height - 150, width, 150);

        int windowWidth = Math.min(220, width / 3);
        int windowHeight = Math.min(260, height / 2);
        int windowX = width / 2 - windowWidth / 2;
        int windowY = 50;

        g.setColor(new Color(20, 26, 34));
        g.fillRoundRect(windowX - 16, windowY - 16, windowWidth + 32, windowHeight + 32, 30, 30);
        GradientPaint glow = new GradientPaint(windowX, windowY, new Color(102, 149, 182, 210),
            windowX, windowY + windowHeight, new Color(21, 46, 71, 120));
        g.setPaint(glow);
        g.fillRoundRect(windowX, windowY, windowWidth, windowHeight, 20, 20);

        g.setStroke(new BasicStroke(10f));
        g.setColor(new Color(88, 96, 110, 220));
        int barGap = Math.max(26, windowWidth / 6);
        for (int x = windowX + 20; x < windowX + windowWidth - 10; x += barGap) {
            g.drawLine(x, windowY + 8, x, windowY + windowHeight - 8);
        }
        g.drawLine(windowX + 12, windowY + windowHeight / 2, windowX + windowWidth - 12, windowY + windowHeight / 2);

        GradientPaint spotlight = new GradientPaint(width / 2f, 0, new Color(255, 244, 198, 120),
            width / 2f, height, new Color(255, 244, 198, 0));
        g.setPaint(spotlight);
        Polygon beam = new Polygon(
            new int[]{windowX + windowWidth / 2 - 18, windowX + windowWidth / 2 + 18, width - 90, 90},
            new int[]{windowY + windowHeight - 10, windowY + windowHeight - 10, height - 30, height - 30},
            4
        );
        g.fillPolygon(beam);

        g.setStroke(new BasicStroke(6f));
        g.setColor(new Color(92, 99, 110, 170));
        drawChain(g, width - 140, 90, 80);
        drawChain(g, 120, 120, 72);

        g.setColor(new Color(0, 0, 0, 110));
        g.fillRect(0, 0, width, height);

        g.dispose();
    }

    private void drawChain(Graphics2D g, int startX, int startY, int links) {
        for (int i = 0; i < links; i++) {
            int x = startX + (i % 2 == 0 ? 0 : 10);
            int y = startY + (i * 8);
            g.drawOval(x, y, 18, 12);
        }
    }

    private void togglePause() {
        if (gameRunning) {
            gameTimer.stop();
            prisonerTimer.stop();
            pauseBtn.setText("Resume");
            statusLabel.setText("Game Paused");
        } else {
            gameTimer.start();
            if (prisonerMoving) prisonerTimer.start();
            pauseBtn.setText("Pause");
            statusLabel.setText("Game Resumed!");
        }
        gameRunning = !gameRunning;
    }

    private void updateGameTime() {
        if (!gameRunning) return;

        gameTime--;
        timerLabel.setText("Time: " + gameTime);

        if (gameTime <= 0) {
            endGame("Time's up! Prisoner wins!");
        }
    }

    private void handleUserClick(int x, int y) {
        if (!gameRunning || prisonerMoving) return;

        generateRoomPositions();

        int selectedStart = -1;
        int selectedEnd = -1;
        double bestDistance = Double.MAX_VALUE;
        final int clickThreshold = 18;

        // Find the nearest unblocked edge segment to the click.
        for (int i = 0; i < getVisibleRoomCount(); i++) {
            for (int[] neighbor : gameEngine.getGraph().adj.get(i)) {
                int j = neighbor[0];
                if (i < j) {
                    if (gameEngine.isEdgeBlocked(i, j)) {
                        continue;
                    }

                    Point p1 = roomPositions.get(i);
                    Point p2 = roomPositions.get(j);
                    if (p1 == null || p2 == null) {
                        continue;
                    }
                    double distance = distanceToSegment(x, y, p1.x, p1.y, p2.x, p2.y);

                    if (distance <= clickThreshold && distance < bestDistance) {
                        bestDistance = distance;
                        selectedStart = i;
                        selectedEnd = j;
                    }
                }
            }
        }

        if (selectedStart == -1) {
            statusLabel.setText("Click closer to a gray edge to block it.");
            return;
        }

        useraction.BlockResult blockResult = useraction.blockEdge(gameEngine, selectedStart, selectedEnd);
        if (blockResult.getStatus() != useraction.BlockStatus.BLOCKED) {
            statusLabel.setText(blockResult.getMessage());
            return;
        }
        userScore = blockResult.getScore();

        // Freeze for 5 seconds
        prisonerMoving = true;
        prisonerTimer.stop();
        gameTimer.stop();
        freezeTimer.start();

        statusLabel.setText(blockResult.getMessage());
        scoreLabel.setText("Score: " + userScore);
        gamePanel.repaint();
    }

    private void endFreeze() {
        freezeTimer.stop();
        gameTimer.start();

        // Recalculate path after freeze
        gameEngine.recalculatePath();
        pathIndex = gameEngine.getPathIndex();

        if (gameEngine.getCurrentPath().isEmpty()) {
            endGame("Prisoner trapped! You win!");
        } else {
            prisonerMoving = true;
            prisonerTimer.start();
            statusLabel.setText("Prisoner recalculated path! Moving...");
        }
    }

    private void movePrisoner() {
        Gameengine.MoveResult moveResult = gameEngine.movePrisonerStep();
        pathIndex = gameEngine.getPathIndex();
        prisonerRoom = gameEngine.getPrisonerRoom();

        if (moveResult == Gameengine.MoveResult.NO_PATH || moveResult == Gameengine.MoveResult.NO_MORE_STEPS) {
            prisonerTimer.stop();
            prisonerMoving = false;
            statusLabel.setText("Prisoner stopped. Your turn to block!");
            return;
        }

        if (moveResult == Gameengine.MoveResult.EXIT_REACHED) {
            endGame("Prisoner reached exit! Computer wins!");
            return;
        }

        // Pause for 2 seconds to let user block
        statusLabel.setText("Prisoner in room " + prisonerRoom + " (2 sec pause - block now!)");
        prisonerMoving = false;  // Allow user to block during pause
        prisonerTimer.stop();
        pauseTimer.start();
        gamePanel.repaint();
    }

    private void resumePrisonerMovement() {
        pauseTimer.stop();
        if (gameRunning && !gameEngine.getCurrentPath().isEmpty()) {
            prisonerMoving = true;
            prisonerTimer.start();
        }
    }

    private void endGame(String message) {
        gameRunning = false;
        prisonerMoving = false;
        Timer.stopAll(gameTimer, prisonerTimer, freezeTimer, pauseTimer);

        startGameBtn.setEnabled(true);
        pauseBtn.setEnabled(false);

        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private double distanceToSegment(int px, int py, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return Point.distance(px, py, x1, y1);
        }

        double projection = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        projection = Math.max(0, Math.min(1, projection));

        double closestX = x1 + projection * dx;
        double closestY = y1 + projection * dy;
        return Point.distance(px, py, closestX, closestY);
    }

    private int getVisibleRoomCount() {
        if (gameEngine.getGraph() == null) {
            return 0;
        }
        return Math.min(gameEngine.getNumRooms(), gameEngine.getGraph().adj.size());
    }

    private void drawGame(Graphics2D g) {
        if (gameEngine.getGraph() == null) {
            g.setColor(Color.WHITE);
            g.drawString("Click Start New Game", 250, 300);
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ensurePrisonBackground();
        generateRoomPositions();
        g.drawImage(prisonBackground, 0, 0, null);
        java.util.List<Integer> currentPath = gameEngine.getCurrentPath();
        int roomCount = getVisibleRoomCount();

        // Draw edges
        for (int i = 0; i < roomCount; i++) {
            for (int[] neighbor : gameEngine.getGraph().adj.get(i)) {
                int j = neighbor[0];
                if (i < j) {
                    Point p1 = roomPositions.get(i);
                    Point p2 = roomPositions.get(j);
                    if (p1 == null || p2 == null) {
                        continue;
                    }

                    if (gameEngine.isEdgeBlocked(i, j)) {
                        g.setColor(Color.RED);
                        g.setStroke(new BasicStroke(4));
                    } else {
                        g.setColor(Color.GRAY);
                        g.setStroke(new BasicStroke(2));
                    }
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // Draw current path
        if (!currentPath.isEmpty() && gameRunning) {
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(3));
            for (int i = 0; i < Math.min(pathIndex + 1, currentPath.size() - 1); i++) {
                Point p1 = roomPositions.get(currentPath.get(i));
                Point p2 = roomPositions.get(currentPath.get(i + 1));
                if (p1 == null || p2 == null) {
                    continue;
                }
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Draw rooms
        for (int i = 0; i < roomCount; i++) {
            Point p = roomPositions.get(i);
            if (p == null) {
                continue;
            }

            if (i == gameEngine.getPrisonerRoom()) {
                g.setColor(Color.GREEN);
                g.fillOval(p.x - 20, p.y - 20, 40, 40);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("P", p.x - 5, p.y + 5);
            } else if (i == gameEngine.getExitRoom()) {
                g.setColor(Color.BLUE);
                g.fillOval(p.x - 20, p.y - 20, 40, 40);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("EXIT", p.x - 15, p.y + 5);
            } else {
                g.setColor(new Color(100, 100, 100));
                g.fillOval(p.x - 15, p.y - 15, 30, 30);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(String.valueOf(i), p.x - 3, p.y + 4);
            }
        }

        // Draw the underlying grid used for auto-generated nodes.
        g.setColor(new Color(50, 50, 50));
        g.setStroke(new BasicStroke(1));
        for (int col = 0; col < gameEngine.getGridColumns(); col++) {
            int x = gridOriginX + (col * currentGridGap);
            g.drawLine(x, gridOriginY - 40, x, gridOriginY + ((gameEngine.getGridRows() - 1) * currentGridGap) + 40);
        }
        for (int row = 0; row < gameEngine.getGridRows(); row++) {
            int y = gridOriginY + (row * currentGridGap);
            g.drawLine(gridOriginX - 40, y, gridOriginX + ((gameEngine.getGridColumns() - 1) * currentGridGap) + 40, y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrisonEscapeGame());
    }
}

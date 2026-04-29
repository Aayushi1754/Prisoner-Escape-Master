package game;

public final class useraction {

    public enum BlockStatus {
        BLOCKED,
        INVALID_EDGE,
        ALREADY_BLOCKED
    }

    public static final class BlockResult {

        private final BlockStatus status;
        private final String message;
        private final int score;

        public BlockResult(BlockStatus status, String message, int score) {
            this.status = status;
            this.message = message;
            this.score = score;
        }

        public BlockStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public int getScore() {
            return score;
        }
    }

    private useraction() {
    }

    public static BlockResult blockEdge(Gameengine engine, int first, int second) {
        if (engine == null) {
            return new BlockResult(BlockStatus.INVALID_EDGE, "Game engine is not ready.", 0);
        }
        if (first == second || first < 0 || second < 0 || first >= engine.getNumRooms() || second >= engine.getNumRooms()) {
            return new BlockResult(BlockStatus.INVALID_EDGE, "Invalid edge selection.", engine.getUserScore());
        }
        if (engine.isEdgeBlocked(first, second)) {
            return new BlockResult(BlockStatus.ALREADY_BLOCKED, "That edge is already blocked.", engine.getUserScore());
        }
        if (!engine.blockEdge(first, second)) {
            return new BlockResult(BlockStatus.INVALID_EDGE, "Invalid edge selection.", engine.getUserScore());
        }

        return new BlockResult(
                BlockStatus.BLOCKED,
                "Blocked edge " + first + "-" + second + ".Recalculating... (5 sec freeze)", engine.getUserScore());
    }
}

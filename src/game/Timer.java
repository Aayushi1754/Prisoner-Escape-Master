package game;

import java.awt.event.ActionListener;

public final class Timer {
    public static final int GAME_DURATION_SECONDS = 60;
    public static final int GAME_TICK_MS = 1000;
    public static final int PRISONER_STEP_MS = 1500;
    public static final int USER_BLOCK_WINDOW_MS = 2000;
    public static final int EDGE_FREEZE_MS = 5000;

    private Timer() {
    }

    public static javax.swing.Timer createGameTimer(ActionListener listener) {
        return new javax.swing.Timer(GAME_TICK_MS, listener);
    }

    public static javax.swing.Timer createPrisonerTimer(ActionListener listener) {
        return new javax.swing.Timer(PRISONER_STEP_MS, listener);
    }

    public static javax.swing.Timer createPauseTimer(ActionListener listener) {
        return new javax.swing.Timer(USER_BLOCK_WINDOW_MS, listener);
    }

    public static javax.swing.Timer createFreezeTimer(ActionListener listener) {
        return new javax.swing.Timer(EDGE_FREEZE_MS, listener);
    }

    public static void stopAll(javax.swing.Timer... timers) {
        for (javax.swing.Timer timer : timers) {
            if (timer != null) {
                timer.stop();
            }
        }
    }
}

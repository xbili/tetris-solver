/**
 * Game Runner class that takes in Input from STDIN and output result to STDOUT
 */
public class GameRunner {

    private static boolean display = true;

    /**
     * Runs the game using the specified weights.
     *
     * @return number of blocks cleared by the agent
     */
    private static int run(State state, float[] weights) {
        PlayerSkeleton player = new PlayerSkeleton();

        // Create new display frame only if display setting is true
        if (display) new TFrame(state);

        while(!state.hasLost()) {
            state.makeMove(player.pickMove(state, state.legalMoves(), weights));
            if (display) draw(state);
        }

        return state.getRowsCleared();
    }

    /**
     * Draws updated state onto the TFrame
     */
    private static void draw(State state) {
        state.draw();
        state.drawNext(0, 0);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        float weights[] = new float[args.length];
        for(int i = 0; i < args.length; i++) {
            weights[i] = Float.parseFloat(args[i]);
        }
        int result = run(new State(), weights);
        System.out.printf("%d\n", result);
    }
}


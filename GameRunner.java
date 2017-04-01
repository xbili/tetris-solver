import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Game Runner class that takes in Input from STDIN and output result to STDOUT
 */
public class GameRunner {

    private static boolean display = false;
    private static BufferedInputStream bis;
//    private static TFrame frame;
    /**
     * Runs the game using the specified weights.
     *
     * @return number of blocks cleared by the agent
     */
    private static int run(ExtendedState state, Float[] weights) {
        PlayerSkeleton player = new PlayerSkeleton();

        // Create new display frame only if display setting is true
//        if (display)
//            frame = new TFrame(state);

        while(!state.hasLost()) {
            state.makeMove(player.pickMove(state, state.legalMoves(), weights));
//            if (display) draw(state);
        }
//        if (state.getRowsCleared() > 0) {
//            frame = new TFrame(state);
//            draw(state);
//        }
//        frame.setVisible(false);
//        frame.dispose();

        return state.getRowsCleared();
    }

    /**
     * Draws updated state onto the TFrame
     */
//    private static void draw(State state) {
//        state.draw();
//        state.drawNext(0, 0);
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    private static void parseWeights(String arg) {
        String[] args = arg.split(" ");
        Float weights[] = new Float[args.length];
        for(int i = 0; i < args.length; i++) {
            weights[i] = Float.parseFloat(args[i]);
        }

        int result = run(new ExtendedState(), weights);
//        Log.info(String.format("%d", result));
        System.out.printf("%d\n", result);
    }
    public static void listen() {
        try {
            bis = new BufferedInputStream(System.in);
            while (true) {
                byte[] buffer = new byte[1<<10];
                if (bis.available() > 0) {
                    bis.read(buffer, 0, bis.available());
//                    Log.debug(new String(buffer));
                    parseWeights(new String(buffer));
                } else {
                }
            }
        } catch (IOException e) {
            // it failed...
        }
    }

    public static void main(String[] args) {
        listen();
    }
}


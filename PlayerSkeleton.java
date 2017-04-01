import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PlayerSkeleton {
    private static final int ROWS = 21;
    private static final int COLS = 10;
    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(ExtendedState s, int[][] legalMoves) {
        int[] features = s.getFeatures();
//        printArray(features);
        return 0;
    }

    public int pickMove(ExtendedState s, int[][] legalMoves, float[] weights) {

        return 0;
    }


    private static void printArray(int[] as) {
        for (int a : as) {
            System.out.printf("%d, ", a);
        }
        System.out.println();
    }
    private static void printField(State s) {
        int[][] field = s.getField();
        for (int row = 0; row < ROWS; row ++) {
            for (int col = 0; col < COLS; col ++) {
                System.out.printf("%2d ", field[row][col]);
            }
            System.out.println();
        }
    }
    /**
     * Calculates the utility value with the specified weights.
     *
     * NOTE: The first element of `features` need to be equal to 1 in order to
     * take into account `w(0)` (the zero-th weight) for our utility function.
     *
     * @return utility value obtained from the weights
     */
    private static int getUtilityValue(int[] weights, int[] features) {
        // Zero-th feature should be equal to 1
        assert features[0] == 1;

        // Weights and features should have the same length
        assert weights.length == features.length;

        int result = 0;
        for (int i = 0; i < weights.length; i++) {
            result += weights[i] * features[i];
        }

        return result;
    }
    public static void main(String[] args) {
        State s = new State();
        State previousState;
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while(!s.hasLost()) {
            previousState = s;
            s.makeMove(p.pickMove(s,s.legalMoves()));
            s.draw();
            s.drawNext(0,0);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }

}


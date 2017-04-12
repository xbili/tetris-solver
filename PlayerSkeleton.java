import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PlayerSkeleton {
    private static final int ROWS = 21;
    private static final int COLS = 10;

    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(ExtendedState s, int[][] legalMoves) {
        double[] weights = { -2.475374129358239, -0.0017855641093171282, -0.5717679188666376, 4.581707413540975, 2.357230362294515, -0.10865658976791168 };
        return pickMove(s, legalMoves, weights);
    }

    public int pickMove(ExtendedState s, int[][] legalMoves, double[] weights) {
        double maxUtil = Double.MIN_VALUE;
        int maxMove = 0;
        for (int i = 0; i < legalMoves.length; i++) {
            double currUtil = getUtilityValue(weights, s.test(i));
            if (currUtil > maxUtil) {
                maxMove = i;
                maxUtil = currUtil;
            }
        }
        return maxMove;
    }

    /**
     * Calculates the utility value with the specified weights.
     *
     * NOTE: The first element of `features` need to be equal to 1 in order to
     * take into account `w(0)` (the zero-th weight) for our utility function.
     *
     * @return utility value obtained from the weights
     */
    private static double getUtilityValue(double[] weights, double[] features) {
        double result = weights[0];
        for (int i = 1; i < weights.length; i++) {
            result += weights[i] * features[i-1];
        }

        return result;
    }

    public static void main(String[] args) {
        ExtendedState s = new ExtendedState();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s,s.legalMoves()));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s.draw();
            s.drawNext(0,0);
        }

        System.out.println("Lines cleared: " + s.getRowsCleared());
    }

}

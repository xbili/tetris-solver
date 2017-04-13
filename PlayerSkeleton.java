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
        double[] weights = { 0.35635480662382224, -2.0540732823872383, -0.07875115892525542, 2.184969056269532, 0.9643824972568638, -0.2974681607895562, -0.014314780723829301, -0.4258138335920463 };
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
            s.draw();
            s.drawNext(0,0);
        }

        System.out.println("Lines cleared: " + s.getRowsCleared());
    }

}

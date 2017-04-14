import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PlayerSkeleton {
    private static final int GAME_RUNS = 20;

    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(ExtendedState s, int[][] legalMoves) {
        double[] weights = { 1.4939890559760567, -2.7315407242210563, -0.7388534648099786, 3.552967163124901, -0.05689703083028738, -0.014030831067594618, -0.06855356172819582 };

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
        double sum = 0;
        int count = GAME_RUNS;

        while (count > 0) {
            ExtendedState s = new ExtendedState();
            PlayerSkeleton p = new PlayerSkeleton();
            while(!s.hasLost()) {
                s.makeMove(p.pickMove(s,s.legalMoves()));
            }
            System.out.println("Lines cleared in iteration #"
                + (GAME_RUNS - count + 1)
                +  ": "
                + s.getRowsCleared()
            );

            sum += s.getRowsCleared();

            count--;
        }

        System.out.println("Average number of lines cleared: " + sum / GAME_RUNS);
    }

}

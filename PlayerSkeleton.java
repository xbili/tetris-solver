import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PlayerSkeleton {
    private static final int GAME_RUNS = 20;

    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(ExtendedState s, int[][] legalMoves) {
        double[] weights = { 0.35138155805675464, -2.060682304074502, -0.06640136176191473, 2.1885343910663937, 0.9478116339692214, -0.2988755083566592, -0.015287575660316514, -0.425630211365857 };

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
        int count = 0;
        while (count < GAME_RUNS) {
            ExtendedState s = new ExtendedState();
            PlayerSkeleton p = new PlayerSkeleton();
            while(!s.hasLost()) {
                s.makeMove(p.pickMove(s,s.legalMoves()));
            }
            System.out.println("Lines cleared in iteration #"
                + count
                +  ": "
                + s.getRowsCleared()
            );

            sum += s.getRowsCleared();

            count++;
        }

        System.out.println("Average number of lines cleared: " + sum / GAME_RUNS);
    }

}

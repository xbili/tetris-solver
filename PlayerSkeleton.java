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
        Float[] weights = fakeWeights(ExtendedState.NUM_FEATURES);
//        printArray(features);
        return pickMove(s, legalMoves, weights);
    }
    // Testing only.
    public Float[] fakeWeights(int numWeights) {
      Random rand = new Random();
      Float[] arr = new Float[numWeights];
      for (int i=0; i<numWeights; i++) {
        arr[i] = rand.nextFloat() * numWeights;
      }
      return arr;
    }

    public int pickMove(ExtendedState s, int[][] legalMoves, Float[] weights) {
      Float maxUtil = 0.0f;
      int maxMove = 0;
      for (int i=0; i<legalMoves.length; i++) {
        System.out.println(">>> option " + i);
        Float currUtil = getUtilityValue(weights, s.test(i));
        System.out.println("util: " + currUtil);
        if (maxUtil < currUtil) {
          maxMove = i;
          maxUtil = currUtil;
        }
      }
      System.out.println("Making move: " + maxMove);
      return maxMove;
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
    private static Float getUtilityValue(Float[] weights, Float[] features) {
        // Zero-th feature should be equal to 1
        assert features[0] == 1.0f;

        // Weights and features should have the same length
        assert weights.length == features.length;

        Float result = 0.0f;
        for (int i = 0; i < weights.length; i++) {
            result += weights[i] * features[i];
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
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }

}

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
        // Float[] weights = fakeWeights(ExtendedState.NUM_FEATURES);
        Float[] weights = fixedWeights();
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
    public Float[] fixedWeights() {
      float[] learnt = new float[] {-15.353264f, -23.422897f, -27.359068f, -37.603813f, -4.7835426f, -38.916718f, -1.2364388f, -10.347687f, -39.305233f, 41.935448f, -21.864826f, -21.664375f, -15.289585f, -21.748257f, -17.72837f, -29.211473f, -17.51176f, -32.001263f, 1.5772476f, 37.67379f, -8.539192f};
      assert learnt.length==ExtendedState.NUM_FEATURES;
      Float[] res = new Float[learnt.length];
      for (int i=0; i<learnt.length; i++) {
        res[i] = learnt[i];
      }
      return res;
    }

    public int pickMove(ExtendedState s, int[][] legalMoves, Float[] weights) {
      Float maxUtil = 0.0f;
      int maxMove = 0;
      for (int i=0; i<legalMoves.length; i++) {
        Float currUtil = getUtilityValue(weights, s.test(i));
        if (maxUtil < currUtil) {
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

       System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }

}

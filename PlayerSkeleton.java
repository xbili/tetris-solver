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
        // double[] weights = fakeWeights(ExtendedState.NUM_FEATURES);
        double[] weights = fixedWeights();
        return pickMove(s, legalMoves, weights);
    }
    // Testing only.
    public double[] fakeWeights(int numWeights) {
      Random rand = new Random();
      double[] arr = new double[numWeights];
      for (int i=0; i<numWeights; i++) {
        arr[i] = rand.nextDouble() * numWeights;
      }
      return arr;
    }
    public double[] fixedWeights() {
      double[] learnt = new double[] {-0.81337675495684, -9.899307912320644, 9.987112431323887, 6.389129519558114, -1.8599693017065704};
      assert learnt.length==ExtendedState.NUM_FEATURES;
      double[] res = new double[learnt.length];
      for (int i=0; i<learnt.length; i++) {
        res[i] = learnt[i];
      }
      return res;
    }

    public int pickMove(ExtendedState s, int[][] legalMoves, double[] weights) {
      double maxUtil = getUtilityValue(weights, s.test(0));
      int maxMove = 0;
      for (int i=1; i<legalMoves.length; i++) {
        double currUtil = getUtilityValue(weights, s.test(i));
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
    private static double getUtilityValue(double[] weights, double[] features) {
        // Zero-th feature should be equal to 1
        assert features[0] == 1.0f;

        // Weights and features should have the same length
        assert weights.length == features.length;

        double result = 0.0;
        for (int i = 0; i < weights.length; i++) {
            result += weights[i] * features[i];
        }

        return result;
    }
    public static void main(String[] args) {
        ExtendedState s = new ExtendedState();
//        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s,s.legalMoves()));
//            s.draw();
//            s.drawNext(0,0);
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

       System.out.println("You have completed "+s.getRowsCleared()+" rows.");
    }

}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PlayerSkeleton {
    private static final int ROWS = 21;
    private static final int COLS = 10;
    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(State s, int[][] legalMoves) {
        return 0;
    }

    public int pickMove(State s, int[][] legalMoves, float[] weights) {
        return 0;
    }

    /**
     * Feature 1-10:  Column height
     * @param s: state
     * @return int[] hs: column height
     */
    private static int[] getColumnHeights(State s) {
        int[] heights = new int[10];
        int[][] field = s.getField();
        for (int row = 0; row < ROWS; row ++) {
            for (int col = 0; col < COLS; col ++) {
                if (field[row][col] != 0) {
                    if (heights[col] != 0) {
                        heights[col] = ROWS - row;
                    }
                }
            }
        }
        return heights;
    }
    /**
     * Feature 11-19:  adjacent height differences
     * @param s: state
     * @return int[] ahs: column height
     */
    private static int[] getAdjacentColumnHeightAbsoluteDifferences(State s) {
        int[] heights = getColumnHeights(s);
        int[] adjacentColumnHeightAbsoluteDifferences = new int[9];
        for (int col = 0; col < COLS-1; col++) {
            adjacentColumnHeightAbsoluteDifferences[col] = Math.abs(heights[col]-heights[col+1]);
        }
        return adjacentColumnHeightAbsoluteDifferences;
    }

    /**
     * Feature 20: Maximum Column
     * @param s: state
     * @return Maximum column height
     */
    private static int getMaximumColumnHeight(State s) {
        int[] heights = getColumnHeights(s);
        int max = 0;
        for (int col = 0; col < COLS; col++) {
            max = Math.max(max, heights[col]);
        }
        return max;
    }

    private static boolean inGameBoundary(int row, int col) {
        return (row >= 0 && row <= ROWS && col >=0 && col <= COLS);
    }
    private static boolean isHole(int[][] field, int row, int col) {
        return (inGameBoundary(row-1, col-1) &&
                inGameBoundary(row, col-1) &&
                inGameBoundary(row+1, col-1) &&
                inGameBoundary(row-1, col) &&
                inGameBoundary(row, col) &&
                inGameBoundary(row+1, col) &&
                inGameBoundary(row-1, col+1) &&
                inGameBoundary(row, col+1) &&
                inGameBoundary(row+1, col+1) &&
                field[row-1][col-1] !=0 &&
                field[row][col-1] !=0 &&
                field[row+1][col-1] !=0 &&
                field[row-1][col] !=0 &&
                field[row][col] ==0 &&
                field[row+1][col] !=0 &&
                field[row-1][col+1] !=0 &&
                field[row][col+1] !=0 &&
                field[row+1][col+1] !=0);
    }
    /**
     * Feature 21: Number of Holes
     * @param s: state
     * @return number of holes
     */
    private static int getNumberOfHoles(State s) {
        int[][] field = s.getField();
        int numHoles = 0;
        for (int row = 0; row < ROWS; row ++) {
            for (int col = 0; col < COLS; col++) {
                if (isHole(field, row, col)) {
                    numHoles++;
                }

            }
        }
        return numHoles;
    }

    /**
     * Feature 22: Bumpiness:
     the sum of the absolute differences in height between adjacent
     columns
     * @param s: state
     * @return bumpiness
     */
    private static int getBumpiness(State s) {
        int[] adjacentColumnHeightAbsoluteDifferences = getAdjacentColumnHeightAbsoluteDifferences(s);
        int sum = 0;
        for (int i=0; i<adjacentColumnHeightAbsoluteDifferences.length; i++) {
            sum += adjacentColumnHeightAbsoluteDifferences[i];
        }
        return sum;
    }

    /**
     * Feature 23: Number of holes created in last step
     * @param previousState: state, currentState: state
     * @return number of holes made
     */
    private static int getNumberOfHolesMade(State previousState, State currentState) {
        int lastNumHoles = getNumberOfHoles(previousState);
        int numHoles = getNumberOfHoles(currentState);
        return numHoles-lastNumHoles;
    }

    /**
     * Feature 24: Number of lines cleared in the last step
     * @param previousState: state, currentState: state
     * @return number of holes made
     */
    private static int getNumberOfLinesCleared(State previousState, State currentState) {
        return currentState.getRowsCleared() - previousState.getRowsCleared();
    }

    /**
     * Feature 24: Calculated feature
     * @param s: state
     * @return array of values for feature 0-21
     */
    private static int[] getFeatures(State s) {
        ArrayList<Integer> features = new ArrayList<>(21);
        int[] heights = getColumnHeights(s);
        for (int height : heights) {
            features.add(height);
        }
        int[] adjColHiDiffs = getAdjacentColumnHeightAbsoluteDifferences(s);
        for (int adjColHiDiff : adjColHiDiffs) {
            features.add(adjColHiDiff);
        }
        features.add(getMaximumColumnHeight(s));
        features.add(getNumberOfHoles(s));
        return features.stream().mapToInt(i -> i).toArray();

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


public class PlayerSkeleton {

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
    public static int[] getColumnHeights(State s) {
        int[] heights = new int[10];
        return heights;
    }
    /**
     * Feature 11-19:  adjacent height differences
     * @param s: state
     * @return int[] ahs: column height
     */
    public static int[] getAdjacentColumnHeightAbsoluteDifferences(State s) {
        int[] adjacentColumnHeightAbsoluteDifferences = new int[9];
        return adjacentColumnHeightAbsoluteDifferences;
    }

    /**
     * Feature 20: Maximum Column
     * @param s: state
     * @return Maximum column height
     */
    public static int getMaximumColumnHeight(State s) {
        return 0;
    }

    /**
     * Feature 21: Number of Holes
     * @param s: state
     * @return number of holes
     */
    public static int getNumberOfHoles(State s) {
        return 0;
    }

    /**
     * Feature 22: Bumpiness:
     the sum of the absolute differences in height between adjacent
     columns
     * @param s: state
     * @return bumpiness
     */
    public static int getBumpiness(State s) {
        return 0;
    }

    /**
     * Feature 23: Number of holes created in last step
     * @param previousState: state, currentState: state
     * @return number of holes made
     */
    public static int getNumberOfHolesMade(State previousState, State currentState) {
        return 0;
    }

    /**
     * Feature 24: Number of lines cleared in the last step
     * @param previousState: state, currentState: state
     * @return number of holes made
     */
    public static int getNumberOfLinesCleared(State previousState, State currentState) {
        return 0;
    }


    /**
     * Calculates the utility value with the specified weights.
     *
     * NOTE: The first element of `features` need to be equal to 1 in order to
     * take into account `w(0)` (the zero-th weight) for our utility function.
     *
     * @return utility value obtained from the weights
     */
    protected static int getUtilityValue(int[] weights, int[] features) {
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


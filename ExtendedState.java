/**
 * State class with additional functionalities.
 */
public class ExtendedState extends State {

    public static final int NUM_FEATURES = 21;
    private float[] features = new float[NUM_FEATURES];

    /**
     * @return a copy of ExtendedState.
     */
    public ExtendedState(State s) {
      this.turn = s.getTurnNumber();
      this.cleared = s.getRowsCleared();
      this.field = get2dClone(s.getField(), State.ROWS);
      this.top = s.getTop().clone();
      this.nextPiece = s.getNextPiece();
    }

    public int[][] get2dClone(int[][] input, int num1dArrays) {
      int [][] arrClone = new int[num1dArrays][];
      for(int i = 0; i < num1dArrays; i++) {
        arrClone[i] = this.field[i].clone();
      }
      return arrClone;
    }

    /**
     * @return extended state of what will happen if a certain move is made.
     */
    public int[] test(int move) {
        ExtendedState es = new ExtendedState(this);
        es.makeMove(move);
        return es.getFeatures();
    }

    /**
     * @return number of holes in the current state.
     */
    public int getHoles() {
        // TODO: Stubbed
        return 0;
    }

    /**
     * @return height of each column in the current state.
     */
    public int[] getColumnHeights() {
        // TODO: Stubbed
        return new int[10];
    }

    /**
     * @return difference between the height of each column.
     */
    public int[] getColumnHeightsDiff() {
        // TODO: Stubbed
        return new int[10];
    }

    /**
     * @return the height of the tallest column in the current state.
     */
    public int getMaxColumnHeight() {
        // TODO: Stubbed
        return 0;
    }

    /**
     * @return the feature array of the current state
     */
    public float[] getFeatures() {
        return features;
    }

}

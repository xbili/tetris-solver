import java.util.ArrayList;
import java.util.Arrays;

/**
 * State class with additional functionalities.
 */
public class ExtendedState extends State {

    public static final int NUM_FEATURES = 3;

    ExtendedState previousState = null;
    public boolean isCloned = false;
    public int clonedTurn = 0;
    public int clonedCleared = 0;
    public int[][] clonedField = new int[ROWS][COLS];
    public int[] clonedTop = new int[COLS];

    public int[][] get2dClone(int[][] input, int num1dArrays) {
        int [][] arrClone = new int[num1dArrays][];
        for(int i = 0; i < num1dArrays; i++) {
            arrClone[i] = input[i].clone();
        }
        return arrClone;
    }
    /**
     * @return a copy/clone of ExtendedState.
     */
    public ExtendedState(State s) {
        this.clonedTurn = s.getTurnNumber();
        this.clonedCleared = s.getRowsCleared();
        this.clonedField = get2dClone(s.getField(), ROWS);
        this.clonedTop = s.getTop().clone();
        this.isCloned = true;
        this.nextPiece = s.getNextPiece();
    }
    public ExtendedState() {
        super();
    }
    //the next several arrays define the piece vocabulary in detail
    //width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = {
        {2},
        {1,4},
        {2,3,2,3},
        {2,3,2,3},
        {2,3,2,3},
        {3,2},
        {3,2}
    };

    //height of the pieces [piece ID][orientation]
    private static int[][] pHeight = {
        {2},
        {4,1},
        {3,2,3,2},
        {3,2,3,2},
        {3,2,3,2},
        {2,3},
        {2,3}
    };

    private static int[][][] pBottom = {
        {{0,0}},
        {{0},{0,0,0,0}},
        {{0,0},{0,1,1},{2,0},{0,0,0}},
        {{0,0},{0,0,0},{0,2},{1,1,0}},
        {{0,1},{1,0,1},{1,0},{0,0,0}},
        {{0,0,1},{1,0}},
        {{1,0,0},{0,1}}
    };

    private static int[][][] pTop = {
        {{2,2}},
        {{4},{1,1,1,1}},
        {{3,1},{2,2,2},{3,3},{1,1,2}},
        {{1,3},{2,1,1},{3,3},{2,2,2}},
        {{3,2},{2,2,2},{2,3},{1,2,1}},
        {{1,2,2},{3,2}},
        {{2,2,1},{2,3}}
    };

    private int randomPiece() {
        return (int)(Math.random()*N_PIECES);
    }

    public int[][] getField() {
        return isCloned ? clonedField : super.getField();
    }

    public int getRowsCleared() {
        return isCloned ? clonedCleared : super.getRowsCleared();
    }
    public int[] getTop() {
        return isCloned ? clonedTop : super.getTop();
    }

    // Make a move based on the move index - its order in the legalMoves list
    public void makeMove(int move) {
        makeMove(legalMoves[nextPiece][move]);
    }

    // Make a move based on an array of orient and slot
    public void makeMove(int[] move) {
        makeMove(move[ORIENT],move[SLOT]);
    }


    public boolean makeMove(int orient, int slot) {
        this.previousState = new ExtendedState(this);
        if (this.isCloned) {
            clonedTurn++;
            // Height if the first column makes contact
            int height = clonedTop[slot]-pBottom[nextPiece][orient][0];
            // For each column beyond the first in the piece
            for(int c = 1; c < pWidth[nextPiece][orient];c++) {
                height = Math.max(height,clonedTop[slot+c]-pBottom[nextPiece][orient][c]);
            }

            // Check if game ended
            if(height+pHeight[nextPiece][orient] >= ROWS) {
                lost = true;
                return false;
            }


            // For each column in the piece - fill in the appropriate blocks
            for(int i = 0; i < pWidth[nextPiece][orient]; i++) {

                // From bottom to top of brick
                for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
                    clonedField[h][i+slot] = clonedTurn;
                }
            }

            // Adjust top
            for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
                clonedTop[slot+c]=height+pTop[nextPiece][orient][c];
            }

            int rowsCleared = 0;

            // Check for full rows - starting at the top
            for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
                // Check all columns in the row
                boolean full = true;
                for(int c = 0; c < COLS; c++) {
                    if(clonedField[r][c] == 0) {
                        full = false;
                        break;
                    }
                }
                // If the row was full - remove it and slide above stuff down
                if(full) {
                    rowsCleared++;
                    clonedCleared++;
                    // For each column
                    for(int c = 0; c < COLS; c++) {

                        // Slide down all bricks
                        for(int i = r; i < clonedTop[c]; i++) {
                            clonedField[i][c] = clonedField[i+1][c];
                        }
                        // Lower the top
                        clonedTop[c]--;
                        while(clonedTop[c]>=1 && clonedField[clonedTop[c]-1][c]==0)	clonedTop[c]--;
                    }
                }
            }

            // Pick a new piece
            nextPiece = randomPiece();

            return true;
        } else {
            return super.makeMove(orient, slot);
        }
    }


    /**
     * @return extended state of what will happen if a certain move is made.
     */
    public double[] test(int move) {
        ExtendedState clonedState = new ExtendedState(this); //cloned
        clonedState.makeMove(move);
        return clonedState.getFeatures();
    }

    /**
     * Feature 1-10:  Column height
     * @return int[] hs: column height
     */
    private int[] getColumnHeights() {
        return getTop();
    }
    /**
     * Feature 11-19:  adjacent height differences
     * @return int[] ahs: column height
     */
    private int[] getAdjacentColumnHeightAbsoluteDifferences() {
        int[] heights = getColumnHeights();
        int[] adjacentColumnHeightAbsoluteDifferences = new int[9];
        for (int col = 0; col < COLS-1; col++) {
            adjacentColumnHeightAbsoluteDifferences[col] = Math.abs(heights[col]-heights[col+1]);
        }
        return adjacentColumnHeightAbsoluteDifferences;
    }

    private int getAggregateHeight() {
        int[] heights = getColumnHeights();
        int sum = 0;
        for (int i = 0; i < heights.length; i++) {
            sum += heights[i];
        }

        return sum;
    }

    /**
     * Feature 20: Maximum Column
     * @return Maximum column height
     */
    private int getMaximumColumnHeight() {
        int[] heights = getColumnHeights();
        int max = 0;
        for (int col = 0; col < COLS; col++) {
            max = Math.max(max, heights[col]);
        }
        return max;
    }

    private static boolean inGameBoundary(int row, int col) {
        return (row >= 0 && row < ROWS && col >=0 && col < COLS);
    }

    /**
     * Feature 21: Number of Holes
     * @return number of holes
     */
    private int getNumberOfHoles() {
        int[][] field = getField();
        int numHoles = 0;
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < getTop()[col] - 1; row++) {
                if (field[row][col] == 0) {
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
     * @return bumpiness
     */
    private int getBumpiness() {
        int[] adjacentColumnHeightAbsoluteDifferences = getAdjacentColumnHeightAbsoluteDifferences();
        int sum = 0;
        for (int i=0; i<adjacentColumnHeightAbsoluteDifferences.length; i++) {
            sum += adjacentColumnHeightAbsoluteDifferences[i];
        }
        return sum;
    }

    /**
     * Feature 23: Number of holes created in last step
     * @return number of holes made
     */
    private int getNumberOfHolesMade() {
        int lastNumHoles = this.previousState.getNumberOfHoles();
        int numHoles = getNumberOfHoles();
        return numHoles-lastNumHoles;
    }

    /**
     * Feature 24: Number of lines cleared in the last step
     * @return number of holes made
     */
    private int getNumberOfLinesCleared() {
        return getRowsCleared() - this.previousState.getRowsCleared();
    }

    /**
     * Feature 25: Standard deviation of column heights
     * @return number of holes made
     */
    private double getHeightStandardDeviation() {
        int[] heights = getColumnHeights();
        double avg = 0f;
        double stddev = 0f;
        for (int h : heights) {
            avg += h;
        }
        avg /= COLS;
        for (int h : heights) {
            double diff = avg - h;
            stddev += diff * diff;
        }
        return stddev;
    }

    /**
     * Feature 26: Wall
     * @return wall(djt)
     */
    private double getNumberOfWalls() {
        double numWalls = 0;
        for (int i = 1; i < COLS-1; i++ ) {
            int left = getTop()[i - 1] - getTop()[i];
            int right = getTop()[i + 1] - getTop()[i];
            if ((left >= 2) && (right >= 2)) {
                numWalls += Math.min(left, right);
            }
        }
        int col0 = getTop()[1] - getTop()[0];
        int col10 = getTop()[COLS-2] - getTop()[COLS - 1];
        if (col0 >= 2) {
            numWalls += col0;
        }
        if (col10 >= 2) {
            numWalls += col10;
        }
        return numWalls;
    }

    private double getCompactness() {
        double weight = 1;
        double sum = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (getField()[i][j] != 0) {
                    sum += weight;
                }
            }
            weight *= 0.5;
        }

        return sum;
    }

    /**
     * Calculated feature
     * @return calculated features
     */
    public double[] getFeatures() {
        double holesMade = getNumberOfHoles();
        double aggregateHeight = getAggregateHeight();
        double compactness = getCompactness();
        double linesCleared = getNumberOfLinesCleared();
        double bumpiness = getBumpiness();
        double stdDev = getHeightStandardDeviation();
        double walls = getNumberOfWalls();
        double[] features = { holesMade, aggregateHeight, compactness, linesCleared, bumpiness, stdDev, walls };

        return features;
    }
}

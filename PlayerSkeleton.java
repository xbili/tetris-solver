import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.*;

public class PlayerSkeleton {

    private static final int GAME_RUNS = 10;

    // Implement this function to have a working system
    // Legal move, 2D array: [Orientation, Slot]
    public int pickMove(ExtendedState s, int[][] legalMoves) {
        double[] weights = {
            0.35138155805675464,
            -2.060682304074502,
            -0.06640136176191473,
            2.1885343910663937,
            0.9478116339692214,
            -0.2988755083566592,
            -0.015287575660316514,
            -0.425630211365857
        };

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

    /** 
     * Main method to run game.
     *
     * We run the game 10 times and get the average result.
     */
    public static void main(String[] args) {
        double sum = 0;
        int count = 0;
        while (count < GAME_RUNS) {
            ExtendedState s = new ExtendedState();
            PlayerSkeleton p = new PlayerSkeleton();
            new TFrame(s);
            while(!s.hasLost()) {
                s.makeMove(p.pickMove(s,s.legalMoves()));
                s.draw();
                s.drawNext(0, 0);
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

    /**
     * State class with additional functionalities.
     */
    public static class ExtendedState extends State {

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
                            while(clonedTop[c]>=1 && clonedField[clonedTop[c]-1][c]==0) clonedTop[c]--;
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
            // In case there is no previousState
            try {
                int lastNumHoles = this.previousState.getNumberOfHoles();
                int numHoles = getNumberOfHoles();
                return numHoles-lastNumHoles;
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * Feature 24: Number of lines cleared in the last step
         * @return number of holes made
         */
        private int getNumberOfLinesCleared() {
            // In case there is no previousState
            try {
                return getRowsCleared() - this.previousState.getRowsCleared();
            } catch (Exception e) {
                return 0;
            }
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

    /**
     * Particle Swarm Optimisation learner.
     */
    public static class SwarmLearner {

        private static final int SWARM_SIZE = 1000;
        private static final int MAX_ITERATION = Integer.MAX_VALUE;
        private static final int FEATURES = 7;

        // Bounds for randomized weight
        private static final double WEIGHT_UPPER_BOUND = 1;
        private static final double WEIGHT_LOWER_BOUND = -1;

        // Bounds for randomized velocities
        private static double VEL_UPPER_BOUND = 2;
        private static double VEL_LOWER_BOUND = -2;

        // Bounds for velocity updates
        private static final double C1 = 1.49618;
        private static final double C2 = 1.49618;
        private static final double W_UPPER_BOUND = 1;
        private static final double W_LOWER_BOUND = 0;

        // Times to run the game for each particle
        private static final int GAME_RUNS = 5;

        // Tolerance for error
        private static double ERR_TOLERANCE = 1E-20;

        // Test method to run the learner
        public static void main(String[] args) {
            int swarmSize;
            if (args.length > 0) {
                swarmSize = Integer.parseInt(args[0]);
            } else {
                swarmSize = SWARM_SIZE;
            }

            SwarmProcess pso = new SwarmProcess(swarmSize);
            pso.execute();
        }

        private static double evaluate(double[] weights) {
            double sum = 0;
            int count = GAME_RUNS;
            while (count > 0) {
                PlayerSkeleton.ExtendedState state = new PlayerSkeleton.ExtendedState();
                PlayerSkeleton player = new PlayerSkeleton();

                while(!state.hasLost()) {
                    state.makeMove(player.pickMove(state, state.legalMoves(), weights));
                }

                sum += state.getRowsCleared();

                count--;
            }

            return sum / GAME_RUNS;
        }

        /**
         * Runs the Particle Swarm Optimization.
         */
        private static class SwarmProcess {

            private ExecutorService pool = Executors.newFixedThreadPool(10);

            private int swarmSize;

            // All particles in the swarm
            private Vector<Particle> swarm = new Vector<Particle>();

            // Personal best fitness value
            private double[] pBest;

            // Personal best weight values
            private Vector<double[]> pBestWeights = new Vector<double[]>();

            // Global best fitness value
            private double gBest;

            // Global best weight values
            private double[] gBestWeights;

            // List of all fitness values
            private double[] fitnessValueList;

            private double[] previousBest = new double[FEATURES + 1];

            // Random number generator for initial positions
            Random generator = new Random();

            public SwarmProcess(int swarmSize) {
                this.swarmSize = swarmSize;
                fitnessValueList = new double[swarmSize];
                pBest = new double[swarmSize];
            }

            public void execute() {
                // Log run parameters
                System.out.println("Swarm size: " + swarmSize);
                System.out.println("Weight upper bound: " + WEIGHT_UPPER_BOUND);
                System.out.println("Weight lower bound: " + WEIGHT_LOWER_BOUND);
                System.out.println("Velocity upper bound: " + VEL_UPPER_BOUND);
                System.out.println("Velocity lower bound: " + VEL_LOWER_BOUND);

                System.out.println("C1: " + C1);
                System.out.println("C2: " + C2);
                System.out.println("w upper bound: " + W_UPPER_BOUND);
                System.out.println("w lower bound: " + W_LOWER_BOUND);

                initializeSwarm();
                updateFitnessList();

                for (int i = 0; i < swarmSize; i++) {
                    pBest[i] = fitnessValueList[i];
                    pBestWeights.add(swarm.get(i).getWeights());
                }

                // Main execution loop
                int iter = 0;
                while(iter < MAX_ITERATION) {
                    // Update personal best
                    for (int i = 0; i < swarmSize; i++) {
                        if (fitnessValueList[i] > pBest[i]) {
                            pBest[i] = fitnessValueList[i];
                            pBestWeights.set(
                                    i, Arrays.copyOf(
                                        swarm.get(i).getWeights(),
                                        swarm.get(i).getWeights().length
                                        )
                                    );
                        }
                    }

                    // Update global best
                    int bestParticleIndex = getMaxParticleIndex();
                    if (iter == 0 || fitnessValueList[bestParticleIndex] > gBest) {
                        System.out.println("New best fitness found: " + fitnessValueList[bestParticleIndex]);

                        gBest = fitnessValueList[bestParticleIndex];
                        gBestWeights = Arrays.copyOf(
                                swarm.get(bestParticleIndex).getWeights(),
                                swarm.get(bestParticleIndex).getWeights().length
                                );
                    }

                    // Update velocity for each particle
                    double w = generateRandomDouble(W_LOWER_BOUND, W_UPPER_BOUND);

                    for (int i = 0; i < swarmSize; i++) {
                        Particle p = swarm.get(i);

                        // Generate random updates
                        double r1 = generator.nextDouble();
                        double r2 = generator.nextDouble();

                        // Update velocities for each set of weights
                        double[] velocities = p.getVelocities();
                        for (int j = 0; j < velocities.length; j++) {
                            velocities[j] =  (w * velocities[j]) +
                                (r1 * C1) * (pBestWeights.get(i)[j] - p.getWeights()[j]) +
                                (r2 * C2) * (gBestWeights[j] - p.getWeights()[j]);
                        }
                        p.setVelocities(velocities);

                        // Update weights
                        double[] weights = p.getWeights();
                        for (int j = 0; j < weights.length; j++) {
                            weights[j] = weights[j] + velocities[j];
                        }
                        p.setWeights(weights);
                    }

                    // Update iteration count
                    iter++;

                    // Update fitness value list
                    updateFitnessList();

                    // Log progress
                    System.out.println("Iteration #" + iter);

                    // Log out weights
                    System.out.println("Best weights: " + Arrays.toString(gBestWeights));

                    // Log best number of lines cleared in that iteration
                    System.out.println("Best number of lines cleared: " + gBest);
                    System.out.println("=======================================");
                }
            }

            private void initializeSwarm() {
                Particle p;

                for (int i = 0; i < swarmSize; i++) {
                    p = new Particle();

                    // Randomize weights
                    double[] weights = new double[FEATURES + 1];
                    for (int j = 0; j < FEATURES + 1; j++) {
                        weights[j] = generateRandomDouble(
                                WEIGHT_LOWER_BOUND, WEIGHT_UPPER_BOUND);
                    }

                    // Randomize velocities
                    double[] velocities = new double[FEATURES + 1];
                    for (int j = 0; j < FEATURES + 1; j++) {
                        velocities[j] = generateRandomDouble(
                                VEL_LOWER_BOUND, VEL_UPPER_BOUND);
                    }

                    // Set weights and velocities
                    p.setWeights(weights);
                    p.setVelocities(velocities);
                    p.setIndexInFitnessValueList(i);

                    // Add this particle to the swarm
                    swarm.add(p);
                }
            }

            private void updateFitnessList() {
                ArrayList<Future<Double>> futures = new ArrayList<Future<Double>>(fitnessValueList.length);
                for (int i = 0; i < fitnessValueList.length; i++) {
                    Callable<Double> runGame = new RunGame(swarm.get(i));
                    Future<Double> future = this.pool.submit(runGame);
                    futures.add(future);
                }

                for (int i = 0; i < fitnessValueList.length; i++) {
                    try {
                        fitnessValueList[i] = futures.get(i).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Thread interrupted.");
                    }
                }
            }

            private void updateParticleFitness(Particle particle) {
                fitnessValueList[particle.getIndexInFitnessValueList()] = particle.getFitnessValue();
            }

            private double generateRandomDouble(double lower, double upper) {
                return lower + generator.nextDouble() * (upper - lower);
            }

            private int getMaxParticleIndex() {
                int result = 0;
                double currFitness = fitnessValueList[0];
                for (int i = 1; i < fitnessValueList.length; i++) {
                    if (fitnessValueList[i] > currFitness) {
                        result = i;
                        currFitness = result;
                    }
                }

                return result;
            }

        }

        /**
         * One particle in the Particle Swarm Optimization.
         */
        private static class Particle {

            private double fitnessValue;
            private double[] velocities;
            private double[] weights;
            private int indexInFitnessValueList;

            public Particle() {
                super();
            }

            public Particle(double fitnessValue, double[] velocities, double[] weights, int indexInFitnessValueList) {
                super();
                this.fitnessValue = fitnessValue;
                this.velocities = velocities;
                this.weights = weights;
                this.indexInFitnessValueList = indexInFitnessValueList;
            }

            public double[] getVelocities() {
                return velocities;
            }

            public void setVelocities(double[] velocities) {
                this.velocities = velocities;
            }

            public double[] getWeights() {
                return weights;
            }

            public void setWeights(double[] weights) {
                assert this.weights.length == weights.length;
                this.weights = weights;
            }

            public double getFitnessValue() {
                return evaluate(getWeights());
            }

            public void setIndexInFitnessValueList(int indexInFitnessValueList) {
                this.indexInFitnessValueList = indexInFitnessValueList;
            }

            public int getIndexInFitnessValueList() {
                return indexInFitnessValueList;
            }
        }

        private static class RunGame implements Callable<Double> {

            private Particle p;

            public RunGame(Particle p) {
                this.p = p;
            }

            public Double call() {
                return this.p.getFitnessValue();
            }

        }

    }

    /**
     * Genetic Algorithm Learner.
     *
     * References:
     * http://www.theprojectspot.com/tutorial-post/creating-a-genetic-algorithm-for-beginners/3
     * http://stackoverflow.com/questions/1575061/ga-written-in-java
     */
    public static class GeneticLearner {
        private static final int POPULATION_SIZE = 500;
        private static final int ITERATIONS = 50;
        private static final double INDIV_GENE_MAX = 1.0;
        private static final double INDIV_GENE_MIN = -1.0;
        private static final int FITNESS_NUM_GAMES = 5;

        private boolean display = false;
        private Population pop;

        public static void main(String[] args) {
            int nWeights = new PlayerSkeleton.ExtendedState(new State()).getFeatures().length;

            System.out.println("New GeneticLearner");
            GeneticLearner gl = new GeneticLearner(POPULATION_SIZE,nWeights,INDIV_GENE_MAX,INDIV_GENE_MIN);
            double[] learnedWeights = gl.start(ITERATIONS);
            System.out.println("Learned weights: " + Arrays.toString(learnedWeights));

            // Test the final result
            gl.setDisplay(true);
            System.out.println("Final score: " + Integer.toString(gl.run(new PlayerSkeleton.ExtendedState(), learnedWeights)));
        }

        public GeneticLearner(int popSize, int numWeights, double maxWeightValue, double minWeightValue) {
            Individual.setDefaultValues(numWeights, maxWeightValue, minWeightValue);
            pop = new Population(popSize, true);
        }

        public double[] start(int iterations) {
            double[] res = new double[0];
            for (int i=0; i<iterations; i++) {
                res = learn();
                System.out.println("Fitness: " + pop.getFittest().getFitnessValue());
                System.out.println("Fittest's weight: " + Arrays.toString(pop.getFittest().getAllGenes()));
                pop.evolve();
            }
            return res;
        }

        protected double[] learn() {
            ArrayList<Individual> individuals = this.pop.getIndividuals();
            // Play a game with each Individual and update its fitness value
            for(int i=0; i<this.pop.getSize(); i++) {
                updateIndividualFitness(individuals.get(i));
            }
            // Parallel implementation
            // individuals.parallelStream().forEach(individual -> updateIndividualFitness(individual));
            // Return the weights from the best Individual
            return this.pop.getFittest().getAllGenes();
        }

        private void updateIndividualFitness(Individual individual) {
            double currFitness = this.run(new PlayerSkeleton.ExtendedState(), individual.getAllGenes());
            for(int i=1; i<FITNESS_NUM_GAMES; i++) {
                currFitness += this.run(new PlayerSkeleton.ExtendedState(), individual.getAllGenes());
            }
            currFitness /= FITNESS_NUM_GAMES;
            individual.setFitnessValue(currFitness);
        }

        /**
         * Runs the game using the specified weights.
         *
         * @return number of blocks cleared by the agent
         */
        protected int run(PlayerSkeleton.ExtendedState state, double[] weights) {
            PlayerSkeleton player = new PlayerSkeleton();

            // Create new display frame only if display setting is true
            if (display) new TFrame(state);

            while(!state.hasLost()) {
                state.makeMove(player.pickMove(state, state.legalMoves(), weights));
                if (display) draw(state);
            }

            return state.getRowsCleared();
        }

        /**
         * Set display setting to true if you want to see the game being
         * played in each iteration of the learning.
         */
        public void setDisplay(boolean display) {
            this.display = display;
        }

        /**
         * Draws updated state onto the TFrame
         */
        private void draw(State state) {
            state.draw();
            state.drawNext(0, 0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Population {
        private static final double MUTATION_PROB = 0.02;  // Probability of mutation
        private static final double FIFTY_PERCENT = 0.5;   // For selecting parent in crossover
        private static final int TOURNAMENT_SIZE = 5;     // Using tournament selection method

        private static Random rand = new Random();

        private int size;
        private Individual[] individuals;

        public Population(int size, boolean toInit) {
            this.size = size;
            this.individuals = new Individual[size];
            if (toInit) {
                initPopulation();
            }
        }

        private void initPopulation() {
            for(int i=0; i<size; i++) {
                individuals[i] = new Individual();
                individuals[i].randAllGenes();
            }
        }

        /* Population controllers */
        public int getSize() {
            return this.size;
        }

        public Individual getIndividual(int index) {
            return this.individuals[index];
        }

        public ArrayList<Individual> getIndividuals() {
            ArrayList<Individual> listOfIndividuals = new ArrayList<>(Arrays.asList(this.individuals));
            return listOfIndividuals;
        }

        public void saveIndividual(int index, Individual indiv) {
            this.individuals[index] = indiv;
        }

        public Individual getFittest() {
            Individual fittest = this.individuals[rand.nextInt(this.size)];
            for (int i=0; i<this.size; i++) {
                if (fittest.getFitnessValue() < this.individuals[i].getFitnessValue()){
                    fittest = this.individuals[i];
                }
            }
            return fittest;
        }

        /* Population Evolution */
        public void evolve() {
            Individual[] newIndividuals = new Individual[this.size];

            // Save best (elitism)
            newIndividuals[0] = this.getFittest();

            // Get crossovers
            for (int i=1; i<this.size; i++) {
                newIndividuals[i] = crossover(this.tournamentSelect(), this.tournamentSelect());
            }

            // Mutate by probability
            for (int i=1; i<this.size; i++) {
                attemptMutation(newIndividuals[i]);
            }

            this.individuals = newIndividuals;
        }

        private Individual crossover(Individual parentA, Individual parentB) {
            Individual child = new Individual();

            for (int i=0; i<Individual.getNumGenes(); i++) {
                double newGene = (rand.nextDouble()<FIFTY_PERCENT) ? parentA.getGene(i) : parentB.getGene(i);
                child.setGene(i, newGene);
            }

            return child;
        }

        private Individual tournamentSelect() {
            // Tournament Population
            Population tournament = new Population(TOURNAMENT_SIZE, false);
            // Place random individuals into tournament
            for (int i=0; i<TOURNAMENT_SIZE; i++) {
                tournament.saveIndividual(i, this.individuals[rand.nextInt(this.size)]);
            }
            // Return the best in the group
            return tournament.getFittest();
        }

        private void attemptMutation(Individual indiv) {
            for (int i=1; i<Individual.getNumGenes(); i++) {
                if (rand.nextDouble() < MUTATION_PROB) {
                    indiv.setGene(i, Individual.randGeneValue());
                }
            }
        }
    }

    private static class Individual {
        private static Random rand = new Random();

        static int numGenes = 10;
        static double geneAllowedMax = 1.0;
        static double geneAllowedMin = -1.0;

        private double[] genes = new double[numGenes];
        private double fitnessValue;

        public Individual() {}

        /* Global control */
        public static void setDefaultValues(int nGenes, double gAllowedMax, double gAllowedMin) {
            numGenes = nGenes + 1;
            geneAllowedMax = gAllowedMax;
            geneAllowedMin = gAllowedMin;
        }
        public static int getNumGenes() {
            return numGenes;
        }
        public static double randGeneValue() {
            return rand.nextDouble()*(geneAllowedMax-geneAllowedMin)+geneAllowedMin;
        }

        /* Instance behaviour */
        public double getFitnessValue() {
            return fitnessValue;
        }
        public void setFitnessValue(double fitnessValue) {
            this.fitnessValue = fitnessValue;
        }

        public double[] getAllGenes() {
            return this.genes;
        }
        public double getGene(int index) {
            return this.genes[index];
        }
        public void setGene(int index, double value) {
            this.genes[index] = value;
        }

        public void randAllGenes() {
            this.setGene(0, 1);
            for(int i=1; i<numGenes; i++) {
                this.setGene(i, randGeneValue());
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(this.genes);
        }
    }

}


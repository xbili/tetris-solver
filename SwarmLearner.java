
import java.util.Vector;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;

public class SwarmLearner {

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

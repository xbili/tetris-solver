import java.util.Vector;
import java.util.Random;
import java.util.Arrays;

public class SwarmLearner extends Learner {

    private static int SWARM_SIZE = 100;
    private static int MAX_ITERATION = 10000;
    private static int FEATURES = 3;

    // Bounds for randomized weight
    private static float WEIGHT_UPPER_BOUND = 30;
    private static float WEIGHT_LOWER_BOUND = -30;

    // Bounds for randomized velocities
    private static float VEL_UPPER_BOUND = 5;
    private static float VEL_LOWER_BOUND = -5;

    // Bounds for velocity updates
    private static float C1 = (float) 1.49618;
    private static float C2 = (float) 1.49618;
    private static float W_UPPER_BOUND = 1;
    private static float W_LOWER_BOUND = 0;

    // Tolerance for error
    private static float ERR_TOLERANCE = (float)1E-20;

    @Override
    protected float[] learn(float[] weights) {
        // TODO: Stubbed
        return new float[10];
    }

    // Test method to run the learner
    public static void main(String[] args) {
        SwarmProcess pso = new SwarmProcess();
        pso.execute();
    }

    private static float evaluate(float[] weights) {
        ExtendedState state = new ExtendedState();
        PlayerSkeleton player = new PlayerSkeleton();

        // This is a hack because of Float vs float.
        Float[] actualWeights = new Float[weights.length];
        for (int i = 0; i < actualWeights.length; i++) {
            actualWeights[i] = weights[i];
        }

        while(!state.hasLost()) {
            state.makeMove(player.pickMove(state, state.legalMoves(), actualWeights));
        }

        return state.getRowsCleared();
    }

    /**
     * Runs the Particle Swarm Optimization.
     */
    private static class SwarmProcess {

        // All particles in the swarm
        private Vector<Particle> swarm = new Vector<Particle>();

        // Personal best fitness value
        private float[] pBest = new float[SWARM_SIZE];

        // Personal best weight values
        private Vector<float[]> pBestWeights = new Vector<float[]>();

        // Global best fitness value
        private float gBest;

        // Global best weight values
        private float[] gBestWeights;

        // List of all fitness values
        private float[] fitnessValueList = new float[SWARM_SIZE];

        // Random number generator for initial positions
        Random generator = new Random();

        public void execute() {
            initializeSwarm();
            updateFitnessList();

            for (int i = 0; i < SWARM_SIZE; i++) {
                pBest[i] = fitnessValueList[i];
                pBestWeights.add(swarm.get(i).getWeights());
            }

            // Main execution loop
            int iter = 0;
            float err = Float.MAX_VALUE;
            while(iter < MAX_ITERATION) {
                // Update personal best
                for (int i = 0; i < SWARM_SIZE; i++) {
                    if (fitnessValueList[i] < pBest[i]) {
                        pBest[i] = fitnessValueList[i];
                        pBestWeights.set(i, swarm.get(i).getWeights());
                    }
                }

                // Update global best
                int bestParticleIndex = getMaxParticleIndex();
                if (iter == 0 || fitnessValueList[bestParticleIndex] > gBest) {
                    gBest = fitnessValueList[bestParticleIndex];
                    gBestWeights = swarm.get(bestParticleIndex).getWeights();
                }

                // Update velocity for each particle
                float w = (float) 0.729844;

                for (int i = 0; i < SWARM_SIZE; i++) {
                    Particle p = swarm.get(i);

                    // Generate random updates
                    float r1 = generator.nextFloat();
                    float r2 = generator.nextFloat();

                    // Update velocities for each set of weights
                    float[] velocities = p.getVelocities();
                    for (int j = 0; j < velocities.length; j++) {
                        velocities[j] =  (w * velocities[j]) +
                            (r1 * C1) * (pBestWeights.get(i)[j] - p.getWeights()[j]) +
                            (r2 * C2) * (gBestWeights[j] - p.getWeights()[j]);
                    }
                    p.setVelocities(velocities);

                    // Update weights
                    float[] weights = p.getWeights();
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] = weights[j] + velocities[j];
                    }
                    p.setWeights(weights);
                }

                // Evaluate with global best weights
                err = evaluate(gBestWeights);

                // Update iteration count
                iter++;

                // Update fitness value list
                updateFitnessList();

                // Log progress
                System.out.println("Iteration #" + iter);

                // Log out weights
                System.out.print("Best weights: ");
                for (int i = 0; i < gBestWeights.length; i++) {
                    System.out.print(gBestWeights[i] + " ");
                }
                System.out.print("\n");

                // Log best number of lines cleared in that iteration
                System.out.println("Best number of lines cleared: " + gBest);
                System.out.println("=======================================");
            }

            System.out.println("\nSolution found at iteration " + (iter - 1));
            for (int i = 0; i < gBestWeights.length; i++) {
                System.out.print(gBestWeights[i] + " ");
            }
        }

        private void initializeSwarm() {
            Particle p;

            for (int i = 0; i < SWARM_SIZE; i++) {
                p = new Particle();

                // Randomize weights
                float[] weights = new float[FEATURES + 1];
                for (int j = 0; j < FEATURES + 1; j++) {
                    weights[j] = generateRandomFloat(
                            WEIGHT_LOWER_BOUND, WEIGHT_UPPER_BOUND);
                }

                // Randomize velocities
                float[] velocities = new float[FEATURES + 1];
                for (int j = 0; j < FEATURES + 1; j++) {
                    velocities[j] = generateRandomFloat(
                            VEL_LOWER_BOUND, VEL_UPPER_BOUND);
                }

                // Set weights and velocities
                p.setWeights(weights);
                p.setVelocities(velocities);

                // Add this particle to the swarm
                swarm.add(p);
            }
        }

        private void updateFitnessList() {
            for (int i = 0; i < SWARM_SIZE; i++) {
                fitnessValueList[i] = swarm.get(i).getFitnessValue();
            }
        }

        private float generateRandomFloat(float lower, float upper) {
            return lower + generator.nextFloat() * (upper - lower);
        }

        private int getMaxParticleIndex() {
            int result = 0;
            float currFitness = fitnessValueList[0];
            for (int i = 1; i < fitnessValueList.length; i++) {
                if (fitnessValueList[i] > currFitness) {
                    result = i;
                }
            }

            return result;
        }

    }

    /**
     * One particle in the Particle Swarm Optimization.
     */
    private static class Particle {

        private float fitnessValue;
        private float[] velocities;
        private float[] weights;

        public Particle() {
            super();
        }

        public Particle(float fitnessValue, float[] velocities, float[] weights) {
            super();
            this.fitnessValue = fitnessValue;
            this.velocities = velocities;
            this.weights = weights;
        }

        public float[] getVelocities() {
            return velocities;
        }

        public void setVelocities(float[] velocities) {
            this.velocities = velocities;
        }

        public float[] getWeights() {
            return weights;
        }

        public void setWeights(float[] weights) {
            assert this.weights.length == weights.length;
            this.weights = weights;
        }

        public float getFitnessValue() {
            return evaluate(getWeights());
        }

    }

}


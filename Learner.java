/**
 * Abstract base learner class to be extended by each of the learning
 * algorithm.
 */
public abstract class Learner {

    private boolean display = false;

    /**
     * Runs the learning algorithm until convergence or max iteration stated
     * is reached.
     *
     * @return optimized weight after learning has taken place
     */
    public int[] start(int[] initialWeights, int maxIterations) {
        int iterations = maxIterations;
        int[] weights = initialWeights;
        while (iterations > 0) {
            weights = learn(weights);

            // TODO: Check for convergence of weights, we can leave this out
            // for now first.

            iterations--;
        }

        return weights;
    }

    /**
     * Set display setting to true if you want to see the game being
     * played in each iteration of the learning.
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * Runs the game using the specified weights.
     *
     * @return number of blocks cleared by the agent
     */
    protected int run(State state, int[] weights) {
        PlayerSkeleton player = new PlayerSkeleton();

        // Create new display frame only if display setting is true
        if (display) new TFrame(state);

        while(!state.hasLost()) {
            state.makeMove(player.pickMove(state, state.legalMoves()));
            if (display) draw(state);
        }

        return state.getRowsCleared();
    }

    /**
     * Calulates the utility value with the specified weights.
     *
     * NOTE: The first element of `features` need to be equal to 1 in order to
     * take into account `w(0)` (the zero-th weight) for our utility function.
     *
     * @return utility value obtained from the weights
     */
    protected int getUtilityValue(int[] weights, int[] features) {
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

    /**
     * The learning is to be implemented by each class.
     *
     * @return updated weight after ONE iteration of the learning algorithm
     */
    protected abstract int[] learn(int[] weights);

    /**
     * Draws updated state onto the TFrame
     */
    private void draw(State state) {
        state.draw();
        state.drawNext(0, 0);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


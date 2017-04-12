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
    public double[] start(double[] initialWeights, int maxIterations) {
        int iterations = maxIterations;
        double[] weights = initialWeights;
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
    protected int run(ExtendedState state, double[] weights) {
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
     * XLearner class should, and needs to override this method.
     *
     * @return updated weight after ONE iteration of the learning algorithm
     */
    protected abstract double[] learn(double[] weights);

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

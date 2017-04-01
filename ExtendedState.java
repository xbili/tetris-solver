/**
 * State class with additional functionalities.
 */
public class ExtendedState extends State {
    /**
     * @return a copy of ExtendedState.
     */
    public ExtendedState clone() {
        // TODO: Stubbed
        return null;
    }

    /**
     * @return extended state of what will happen if a certain move is made.
     */
    public ExtendedState test(int move) {
        // TODO: Stubbed
        return null;
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
    public int[] getFeatures() {
        // TODO: Stubbed
        return new int[10];
    }

    /**
     * Calculated feature
     * @return calculated features
     */
    public Float[] getFeatures() {
        ArrayList<Float> features = new ArrayList<>(21);
        int[] heights = getColumnHeights();
        for (int height : heights) {
            features.add((float)height);
        }
            int[] adjColHiDiffs = getAdjacentColumnHeightAbsoluteDifferences();
            for (int adjColHiDiff : adjColHiDiffs) {
                features.add((float)adjColHiDiff);
            }
        features.add((float)getMaximumColumnHeight());
        features.add((float)getNumberOfHoles());


        return features.stream().map(i -> (float)i).toArray(Float[]::new);

    }
}


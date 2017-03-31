import java.util.Random;
import java.util.Arrays;

public class GeneticLearner extends Learner {

  private int numWeights;
  private int populationSize;
  // TODO: Use an Individual Class
  private float[][] population;
  private float[] learnedWeights;

  private Random rand;

  public GeneticLearner(int populationSize, int numWeights) {
    this.numWeights = numWeights;
    this.populationSize = populationSize;
    this.population = new float[populationSize][numWeights];

    this.rand = new Random();
    initPopulation();
  }

  // Note: initialWeights do not apply for GA.
  @Override
  public float[] start(int iterations) {
    // Do learn() for <iteration> times.
    // Population is kept.
  }

  private float learn() {
    // Play a game with each Individual
    // Update its fitness value
    // Get the best, save it and return it
  }

  private void initPopulation() {
    for(int i=0; i<populationSize; i++) {
      for(int j=0; j<numWeights; j++) {
        this.population[i][j] = getRandWeight();
      }
    }
  }

  private float getRandWeight() {
    return rand.nextInt(RAND_MAX) / (float) RAND_DIV;
  }

  private int playGame(float[] weights) {
    // TODO: Complete
    State s = new State();
    new TFrame(s);
    PlayerSkeleton p = new PlayerSkeleton();
    while(!s.hasLost()) {
        s.makeMove(p.pickMove(s,s.legalMoves()));
        s.draw();
        s.drawNext(0,0);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    return s.getRowsCleared();
  }

  public static void main() {
    int nPopulation=100, nWeights=10, nIterations=100;

    GeneticLearner gl = new GeneticLearner(nPopulation,nWeights);
    float[] res = gl.learn(nIterations);
    System.out.println("Learned weights: " + Arrays.toString(res));
  }
}

class Individual {
  static int numGenes = 10;
  static int geneAllowedMax = 1;
  static int geneAllowedMin = -1;

  private float[] genes = new float[numGenes];
  private int fitnessValue;

  private static Random rand = new Random();

  public Individual() {}

  /* Global control */
  public static void setDefaultValues(int numGenes, float geneAllowedMax, float geneAllowedMin) {
    this.numGenes = numGenes;
    this.geneAllowedMax = geneAllowedMax;
    this.geneAllowedMin = geneAllowedMin;
  }

  public int getFitnessValue() {
    return fitnessValue;
  }
  public void setFitnessValue(int fitnessValue) {
    this.fitnessValue = fitnessValue;
  }

  public float[] getAllGenes() {
    return this.genes;
  }
  public float getGene(int index) {
    assert index>=0;
    assert index<numGenes;
    return this.genes[index];
  }
  public void setGene(int index, float value) {
    assert index>=0;
    assert index<numGenes;
    this.genes[index] = value;
  }

  public void randGeneValue() {
    return randFloat()*(geneAllowedMax-geneAllowedMin)+geneAllowedMin;
  }
  public void randAllGenes() {
    for(int i=0; i<numGenes; i++) {
      this.setGene(i, randGeneValue());
    }
  }

  // TODO: MOVE to evolve segment
  public void mutate() {
    this.setGene(rand.nextInt(numGenes), randGeneValue());
  }

  @Override
  public String toString() {
    return Array.toString(this.genes);
  }

}

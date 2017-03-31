import java.util.Random;
import java.util.Arrays;

public class GeneticLearner extends Learner {
  private Population pop;

  public GeneticLearner(int popSize, int numWeights, float maxWeightValue, float minWeightValue) {
    Individual.setDefaultValues(numWeights, maxWeightValue, minWeightValue);
    pop = new Population(popSize, true);
  }

  // TODO:: initialWeights does not apply for GA.
  @Override
  public float[] start(float[] initialWeights, int iterations) {
    float[] res = new float[0];
    for (int i=0; i<iterations; i++) {
      res = learn(initialWeights);
      // System.out.println("Fitness: " + pop.getFittest().getFitnessValue());
      pop.evolve();
    }
    return res;
  }

  // TODO: weights[] does not apply
  protected float[] learn(float[] weights) {
    // Play a game with each Individual and update its fitness value
    for(int i=0; i<this.pop.getSize(); i++) {
      // TODO: Test whether playing multiple games and taking average/minimum matters
      int currFitness = this.run(new State(), this.pop.getIndividual(i).getAllGenes());
      this.pop.getIndividual(i).setFitnessValue(currFitness);
    }
    // Return the weights from the best Individual
    return this.pop.getFittest().getAllGenes();
  }

  public static void main() {
    int nPopulation=100, nWeights=10, nIterations=100;
    float maxWeightValue=10.0f, minWeightValue=-5.0f;

    GeneticLearner gl = new GeneticLearner(nPopulation,nWeights,maxWeightValue,minWeightValue);
    float[] res = gl.start(new float[1], nIterations);
    System.out.println("Learned weights: " + Arrays.toString(res));

    // Test the final result
    gl.setDisplay(true);
    System.out.println(gl.run(new State(), res));
  }
}

class Population {
  private static final float MUTATION_PROB = 0.05f;  // Probability of mutation
  private static final float FIFTY_PERCENT = 0.5f;   // For selecting parent in crossover
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
  public void saveIndividual(int index, Individual indiv) {
    this.individuals[index] = indiv;
  }

  public Individual getFittest() {
    Individual fittest = this.individuals[0];
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
      float newGene = (rand.nextFloat()<FIFTY_PERCENT) ? parentA.getGene(i) : parentB.getGene(i);
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

  private void attemptMutation(Individual i) {
    if (rand.nextFloat() < MUTATION_PROB) {
      i.setGene(rand.nextInt(Individual.getNumGenes()), Individual.randGeneValue());
    }
  }
}

class Individual {
  private static Random rand = new Random();

  static int numGenes = 10;
  static float geneAllowedMax = 1.0f;
  static float geneAllowedMin = -1.0f;

  private float[] genes = new float[numGenes];
  private int fitnessValue;

  public Individual() {}

  /* Global control */
  public static void setDefaultValues(int nGenes, float gAllowedMax, float gAllowedMin) {
    numGenes = nGenes;
    geneAllowedMax = gAllowedMax;
    geneAllowedMin = gAllowedMin;
  }
  public static int getNumGenes() {
    return numGenes;
  }
  public static float randGeneValue() {
    return rand.nextFloat()*(geneAllowedMax-geneAllowedMin)+geneAllowedMin;
  }

  /* Instance behaviour */
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
    return this.genes[index];
  }
  public void setGene(int index, float value) {
    this.genes[index] = value;
  }

  public void randAllGenes() {
    for(int i=0; i<numGenes; i++) {
      this.setGene(i, randGeneValue());
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(this.genes);
  }
}

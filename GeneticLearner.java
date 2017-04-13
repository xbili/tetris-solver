import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

/** References:
    http://www.theprojectspot.com/tutorial-post/creating-a-genetic-algorithm-for-beginners/3
    http://stackoverflow.com/questions/1575061/ga-written-in-java */

public class GeneticLearner extends Learner {
  private static final int NUM_RUNS = 3;
  private Population pop;

  public GeneticLearner(int popSize, int numWeights, double maxWeightValue, double minWeightValue) {
    Individual.setDefaultValues(numWeights, maxWeightValue, minWeightValue);
    pop = new Population(popSize, true);
  }

  // TODO:: initialWeights does not apply for GA.
  @Override
  public double[] start(double[] initialWeights, int iterations) {
    double[] res = new double[0];
    for (int i=0; i<iterations; i++) {
      res = learn(initialWeights);
      System.out.println("Fitness: " + pop.getFittest().getFitnessValue());
      System.out.println("Fittest's weight: " + Arrays.toString(pop.getFittest().getAllGenes()));
      pop.evolve();
    }
    return res;
  }

  // TODO: weights[] does not apply
  protected double[] learn(double[] weights) {
    // Play a game with each Individual and update its fitness value
    // for(int i=0; i<this.pop.getSize(); i++) {
    //   // TODO: Test whether playing multiple games and taking average/minimum matters
    //   double currFitness = this.run(new ExtendedState(), this.pop.getIndividual(i).getAllGenes());
    //   this.pop.getIndividual(i).setFitnessValue(currFitness);
    // }

    ArrayList<Individual> individuals = this.pop.getIndividuals();
    individuals.parallelStream().forEach(individual -> updateIndividualFitness(individual));
    // Return the weights from the best Individual
    return this.pop.getFittest().getAllGenes();
  }

  private void updateIndividualFitness(Individual individual) {
    double currFitness = this.run(new ExtendedState(), individual.getAllGenes());
    for(int i=1; i<NUM_RUNS; i++) {
    	currFitness += this.run(new ExtendedState(), individual.getAllGenes());
    }
    currFitness /= NUM_RUNS;
    individual.setFitnessValue(currFitness);
  }

  public static void main(String[] args) {
    int nPopulation=500, nIterations=100;
    double maxWeightValue=10.0, minWeightValue=-10.0;
    int nWeights = ExtendedState.NUM_FEATURES;

    System.out.println("New GeneticLearner");
    GeneticLearner gl = new GeneticLearner(nPopulation,nWeights,maxWeightValue,minWeightValue);
    double[] res = gl.start(new double[1], nIterations);
    System.out.println("Learned weights: " + Arrays.toString(res));

    // Test the final result
    gl.setDisplay(true);
    System.out.println("Final score: " + Integer.toString(gl.run(new ExtendedState(), res)));
  }
}

class Population {
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
    for (int i=0; i<Individual.getNumGenes(); i++) {
      if (rand.nextDouble() < MUTATION_PROB) {
        indiv.setGene(i, Individual.randGeneValue());
      }
    }
  }
}

class Individual {
  private static Random rand = new Random();

  static int numGenes = 10;
  static double geneAllowedMax = 1.0;
  static double geneAllowedMin = -1.0;

  private double[] genes = new double[numGenes];
  private double fitnessValue;

  public Individual() {}

  /* Global control */
  public static void setDefaultValues(int nGenes, double gAllowedMax, double gAllowedMin) {
    numGenes = nGenes;
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
    for(int i=0; i<numGenes; i++) {
      this.setGene(i, randGeneValue());
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(this.genes);
  }
}

package ga;

import java.util.ArrayList;
import mdmtsp.Individual;
import mdmtsp.MDMTSP;
import mdmtsp.Random;

public class GeneticAlgorithm {

    //GA parameters
    private final int MAX_GENERATION;
    private int populationSize;
    private SelectionOperationType selection = SelectionOperationType.tournament;
    private int numberOfIndividualsSelected;
    private double mutationRate = 0.5;

    // variables
    private MDMTSP mdmtsp = null;
    private Individual bestSolution = null;
    private double bestFitness = 0;

    public GeneticAlgorithm(MDMTSP mdmtsp, int populationSize, int MAX_GENERATION, SelectionOperationType selection, int numberOfIndividualsSelected, double mutationRate) {
        this.mdmtsp = mdmtsp;
        this.populationSize = populationSize;
        this.MAX_GENERATION = MAX_GENERATION;
        this.selection = selection;
        this.numberOfIndividualsSelected = numberOfIndividualsSelected;
        this.mutationRate = mutationRate;
    }

    public boolean init() {
        boolean status = false;
        if (this.mdmtsp != null
                && this.populationSize > 0
                && this.MAX_GENERATION >= 1
                && this.mutationRate >= 0) {
            if (this.populationSize < 2) {
                this.populationSize = 2;
            }
            if (numberOfIndividualsSelected <= 0) {
                numberOfIndividualsSelected = 2;
            } else if (numberOfIndividualsSelected > populationSize) {
                numberOfIndividualsSelected = (int) Math.ceil(populationSize / 2.0);
            }
            status = true;
        }
        return status;
    }

    public Individual getBestSolution() {
        return this.bestSolution;
    }

    public void process() {
        if (init()) {
            Individual[] population = new Individual[populationSize];

            //Initialize random population
            for (int i = 0; i < population.length; i++) {
                population[i] = new Individual();
                population[i].setMDMTSP(mdmtsp);
                population[i].generateRandomChromosome();
                population[i].calculateFitness();
                // ELITISM
                if (population[i].getFitness() > bestFitness) {
                    bestSolution = population[i];
                    bestFitness = bestSolution.getFitness();
                }
            }

            // Evolution Process
            for (int g = 1; g <= this.MAX_GENERATION; g++) {
                Individual[] newPopulation = new Individual[populationSize];

                // SELECTION----------------------------------------------------
                if (selection == SelectionOperationType.tournament) {
                    // Tournament Selection
                    // sort population base on their fitness values
                    double[][] fitness = new double[populationSize][2];//[index | fitness]
                    for (int i = 0; i < populationSize; i++) {
                        fitness[i][0] = i;
                        fitness[i][1] = population[i].getFitness();
                    }
                    // sort
                    for (int i = 0; i < fitness.length - 1; i++) {
                        int iMAX = i;
                        double MAX_FITNESS = fitness[i][1];
                        for (int j = i + 1; j < fitness.length; j++) {
                            if (fitness[j][1] > MAX_FITNESS) {
                                MAX_FITNESS = fitness[j][1];
                                iMAX = j;
                            }
                        }
                        if (iMAX > i) {
                            // swap
                            double temp0 = fitness[i][0];
                            double temp1 = fitness[i][1];
                            fitness[i][0] = fitness[iMAX][0];
                            fitness[i][1] = fitness[iMAX][1];
                            fitness[iMAX][0] = temp0;
                            fitness[iMAX][1] = temp1;
                        }
                    }
                    // save selected individual
                    for (int i = 0; i < numberOfIndividualsSelected; i++) {
                        int index = (int) fitness[i][0];
                        newPopulation[i] = population[index].clone();
                    }

                } else if (selection == SelectionOperationType.roulette_wheel) {
                    // Roulette Wheel Selection
                    double[][] fitness = new double[populationSize][2];//[index | fitness]
                    double totalFitness = 0;
                    for (int i = 0; i < populationSize; i++) {
                        fitness[i][0] = i;
                        fitness[i][1] = population[i].getFitness();
                        totalFitness += fitness[i][1];
                    }
                    // cumulative probability
                    double top = 0;
                    double[] cumulativeProbability = new double[populationSize];
                    for (int i = 0; i < populationSize; i++) {
                        double relativeFitness = fitness[i][1] / totalFitness;
                        top += relativeFitness;
                        cumulativeProbability[i] = top;
                    }

                    // save selected individual
                    for (int i = 0; i < numberOfIndividualsSelected; i++) {
                        // random
                        double rs = Random.getRandomUniform();
                        for (int j = 0; j < populationSize; j++) {
                            if (cumulativeProbability[j] >= rs) {
                                newPopulation[i] = population[j].clone();
                                break;
                            }
                        }

                    }

                }

                // CROSSOVER----------------------------------------------------
                // The Operation for crossover is Partially Mapped Crossover (PMX)
                int k = numberOfIndividualsSelected;
                while (k < populationSize) {
                    // select random parents
                    int indexParent1 = Random.getRandomBetween(0, numberOfIndividualsSelected - 1);
                    int indexParent2 = indexParent1;
                    while (indexParent2 == indexParent1) {
                        indexParent2 = Random.getRandomBetween(0, numberOfIndividualsSelected - 1);
                    }
                    int[] parent1 = newPopulation[indexParent1].getChromosome()[0];
                    int[] parent2 = newPopulation[indexParent2].getChromosome()[0];
                    // random two crossover points
                    int genSize = parent1.length;
                    int point1 = Random.getRandomBetween(0, genSize - 1);
                    int point2 = point1;
                    while (point2 == point1) {
                        point2 = Random.getRandomBetween(0, genSize - 1);
                    }
                    if (point1 > point2) {
                        int temp = point1;
                        point1 = point2;
                        point2 = temp;
                    }
                    // prepare offspring
                    int[] offspring1 = new int[genSize];
                    int[] offspring2 = new int[genSize];
                    for (int i = 0; i < genSize; i++) {
                        offspring1[i] = parent1[i];
                        offspring2[i] = parent2[i];
                    }
                    // do PMX
                    for (int i = point1; i <= point2; i++) {
                        int value1 = offspring1[i];
                        int value2 = offspring2[i];
                        for (int j = 0; j < genSize; j++) {
                            if (offspring1[j] == value2) {
                                offspring1[j] = value1;
                            }
                            if (offspring2[j] == value1) {
                                offspring2[j] = value2;
                            }
                        }
                        // crossover
                        offspring1[i] = value2;
                        offspring2[i] = value1;
                    }
                    //end of PMX
                    // set new individu as ofspring
                    // offspring_1
                    if (k < populationSize) {
                        newPopulation[k] = new Individual();
                        newPopulation[k].setMDMTSP(mdmtsp);
                        int[][] chromosome = new int[2][genSize];
                        chromosome[0] = offspring1;
                        chromosome[1] = newPopulation[indexParent1].getChromosome()[1];
                        newPopulation[k].setChromosome(chromosome);
                        newPopulation[k].calculateFitness();
                        k++;
                    }
                    // offspring_2
                    if (k < populationSize) {
                        newPopulation[k] = new Individual();
                        newPopulation[k].setMDMTSP(mdmtsp);
                        int[][] chromosome = new int[2][genSize];
                        chromosome[0] = offspring2;
                        chromosome[1] = newPopulation[indexParent2].getChromosome()[1];
                        newPopulation[k].setChromosome(chromosome);
                        newPopulation[k].calculateFitness();
                        k++;
                    }
                }

                // MUTATION-----------------------------------------------------
                for (int i = 0; i < populationSize; i++) {
                    double rm = Random.getRandomUniform();
                    if (rm > mutationRate) {
                        Individual mutant = newPopulation[i].clone();
                        // customerMutation
                        int index = Random.getRandomBetween(0, mutant.getChromosome()[0].length - 1);
                        mutant.customerMutation(index);
                        mutant.calculateFitness();
                        if (mutant.getFitness() > newPopulation[i].getFitness()) {
                            newPopulation[i] = mutant.clone();
                        }
                        // salesmanMutation
                        index = Random.getRandomBetween(0, mutant.getOriginDepot().length - 1);
                        mutant.salesmanMutation(index);
                        mutant.calculateFitness();
                        if (mutant.getFitness() > newPopulation[i].getFitness()) {
                            newPopulation[i] = mutant.clone();
                        }
                    }
                }

                // SET NEW POPULATION
                for (int i = 0; i < populationSize; i++) {
                    population[i] = newPopulation[i].clone();
                    // ELITISM
                    if (population[i].getFitness() > bestFitness) {
                        bestSolution = population[i];
                        bestFitness = bestSolution.getFitness();
                    }
                }
            }//end of evolution
        }
    }

}

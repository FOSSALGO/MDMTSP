package aco;

import java.util.ArrayList;
import mdmtsp.Individual;
import mdmtsp.MDMTSP;
import mdmtsp.Random;

public class AntColonyOptimization {

    //ACO parameters
    double[][] adjacency = null;
    double[][] pheromone = null;//Tau
    double[][] visibility = null;//Eta
    int S = 0;//number of ants
    int NCMAX;//maxIterations (maximum ant cycle)
    double alpha;//pheromone importance (Konstanta pengendali pheromone (α), nilai α ≥ 0.) 
    double beta;//distance priority (Konstanta pengendali intensitas visibilitas (β), nilai β ≥ 0.)
    double rho;//Evaporation (Konstanta penguapan pheromone)
    double Q = 1;//pheromone left on train per ant (Konstanta Siklus Semut)

    // variables
    private MDMTSP mdmtsp = null;
    private Individual bestSolution = null;
    private ArrayList<ArrayList<Integer>> bestPath = null;
    private double bestFitness = 0;

    public AntColonyOptimization(MDMTSP mdmtsp, int numberOfAnts, int maxIterations, double alpha, double beta, double evaporation) {
        this.mdmtsp = mdmtsp;
        this.S = numberOfAnts;
        this.NCMAX = maxIterations;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = evaporation;
    }

    public boolean init() {
        boolean status = false;
        if (this.mdmtsp != null
                && this.mdmtsp.getAdjacency() != null
                && this.S > 0
                && NCMAX > 0) {
            this.adjacency = this.mdmtsp.getAdjacency();
            this.visibility = new double[this.adjacency.length][];
            this.pheromone = new double[this.adjacency.length][];
            for (int i = 0; i < this.visibility.length; i++) {
                this.visibility[i] = new double[this.adjacency[i].length];
                this.pheromone[i] = new double[this.adjacency[i].length];
                for (int j = 0; j < this.visibility[i].length; j++) {
                    this.visibility[i][j] = 0;
                    this.pheromone[i][j] = 0;
                    if (this.adjacency[i][j] > 0) {
                        this.visibility[i][j] = 1.0 / this.adjacency[i][j];
                        this.pheromone[i][j] = 1;
                    }
                }
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
            //ant life cycle
            int c = 0;
            while (c < NCMAX) {
                // initialize delta Tau
                double[][] deltaTau = new double[this.adjacency.length][];
                for (int i = 0; i < deltaTau.length; i++) {
                    deltaTau[i] = new double[this.adjacency[i].length];
                    for (int j = 0; j < deltaTau[i].length; j++) {
                        deltaTau[i][j] = 0;
                    }
                }
                int numberOfSalesmans = mdmtsp.getNumberOfSalesmans();//NUMBER_OF_SALESMANS
                ArrayList<Integer> depots = mdmtsp.getDepots();
                ArrayList<Integer> customers = mdmtsp.getCustomers();
                // ant looking for a path
                for (int ant = 1; ant <= S; ant++) {
                    ArrayList<ArrayList<Integer>> path = new ArrayList<>();
                    ArrayList<Integer> visitedCustomers = new ArrayList<>();
                    while (visitedCustomers.size() < customers.size()) {
                        ArrayList<Candidate> candidates = new ArrayList<>();
                        double denominator = 0;
                        // check all depots
                        if (path.size() < numberOfSalesmans) {
                            for (int d = 0; d < depots.size(); d++) {
                                // check all unvisited customers which adjacent to depot-d
                                int depotIndex = depots.get(d);
                                for (int j = 0; j < this.adjacency[depotIndex].length; j++) {
                                    if (j != depotIndex && customers.contains(j) && !visitedCustomers.contains(j) && this.adjacency[depotIndex][j] > 0) {
                                        int salesmanIndex = -1;
                                        int originIndex = depotIndex;
                                        int destinationIndex = j;
                                        double eta = visibility[originIndex][destinationIndex];
                                        double tau = pheromone[originIndex][destinationIndex];
                                        Candidate candidate = new Candidate(salesmanIndex, originIndex, destinationIndex, eta, tau, alpha, beta);
                                        denominator += candidate.tau_eta;
                                        candidates.add(candidate);
                                    }
                                }
                            }
                        }
                        if (!path.isEmpty()) {
                            for (int p = 0; p < path.size(); p++) {
                                int salesmanIndex = p;
                                ArrayList<Integer> salesmanPath = path.get(p);
                                if (!salesmanPath.isEmpty()) {
                                    int tail = salesmanPath.get(salesmanPath.size() - 1);
                                    // check all unvisited customers which adjacent to tail
                                    for (int j = 0; j < this.adjacency[tail].length; j++) {
                                        if (j != tail && customers.contains(j) && !visitedCustomers.contains(j) && this.adjacency[tail][j] > 0) {
                                            int originIndex = tail;
                                            int destinationIndex = j;
                                            double eta = visibility[originIndex][destinationIndex];
                                            double tau = pheromone[originIndex][destinationIndex];
                                            Candidate candidate = new Candidate(salesmanIndex, originIndex, destinationIndex, eta, tau, alpha, beta);
                                            denominator += candidate.tau_eta;
                                            candidates.add(candidate);
                                        }
                                    }
                                }

                            }
                        }
                        // calculate the ants probability
                        if (!candidates.isEmpty() && denominator > 0) {
                            double[] cumulativeProbability = new double[candidates.size()];
                            double totalProbability = 0;
                            for (int i = 0; i < candidates.size(); i++) {
                                double numerator = candidates.get(i).tau_eta;
                                double probability = numerator / denominator;
                                totalProbability += probability;
                                cumulativeProbability[i] = totalProbability;
                            }

                            double randomProbability = Random.getRandomUniform() * totalProbability;
                            int selectedIndex = -1;
                            for (int i = 0; i < cumulativeProbability.length; i++) {
                                if (cumulativeProbability[i] >= randomProbability) {
                                    selectedIndex = i;
                                    break;
                                }
                            }
                            // if selectedIndex > -1 then set new edge
                            if (selectedIndex > -1) {
                                Candidate selected = candidates.get(selectedIndex);
                                int salesmanIndex = selected.salesmanIndex;
                                if (salesmanIndex == -1) {
                                    ArrayList<Integer> salesmanPath = new ArrayList<>();
                                    int origin = selected.originIndex;
                                    int destination = selected.destinationIndex;
                                    salesmanPath.add(origin);
                                    salesmanPath.add(destination);
                                    path.add(salesmanPath);
                                    visitedCustomers.add(destination);
                                } else {
                                    int destination = selected.destinationIndex;
                                    path.get(salesmanIndex).add(destination);
                                    visitedCustomers.add(destination);
                                }
                            }
                        }
                    }// end of while (visitedCustomers.size() < customers.size())

                    // calculate ant tour
                    double distance = 0;
                    if (!path.isEmpty()) {
                        for (int p = 0; p < path.size(); p++) {
                            ArrayList<Integer> salesmanPath = path.get(p);
                            double d = 0;
                            for (int i = 1; i < salesmanPath.size(); i++) {
                                int origin = salesmanPath.get(i - 1);
                                int destination = salesmanPath.get(i);
                                d += this.adjacency[origin][destination];
                            }
                            // go home
                            int origin = salesmanPath.get(salesmanPath.size() - 1);
                            int destination = salesmanPath.get(0);
                            d += this.adjacency[origin][destination];
                            distance += d;
                        }
                    }

                    //calculate fitness
                    double fitness = 0;
                    if (distance > 0) {
                        fitness = Q / distance;
                    }

                    //SAVE BEST SOLUTION
                    if (fitness > bestFitness) {
                        bestFitness = fitness;
                        bestPath = path;
                    }

                    // Update deltaTau for ants
                    if (!path.isEmpty()) {
                        for (int p = 0; p < path.size(); p++) {
                            ArrayList<Integer> salesmanPath = path.get(p);
                            for (int i = 1; i < salesmanPath.size(); i++) {
                                int origin = salesmanPath.get(i - 1);
                                int destination = salesmanPath.get(i);
                                deltaTau[origin][destination] += fitness;//update SIGMA delta Tau xy
                                deltaTau[destination][origin] += fitness;
                            }
                            // go home
                            int origin = salesmanPath.get(salesmanPath.size() - 1);
                            int destination = salesmanPath.get(0);
                            deltaTau[origin][destination] += fitness;//update SIGMA delta Tau xy
                            deltaTau[destination][origin] += fitness;
                        }
                    }
                }// end of for (int ant = 1; ant <= S; ant++) 

                //UPDATE PHEROMONE (Tau)
                for (int i = 0; i < pheromone.length; i++) {
                    for (int j = 0; j < pheromone[i].length; j++) {
                        pheromone[i][j] = (1.0 - rho) * pheromone[i][j] + deltaTau[i][j];
                    }
                }

                c++; // increment cycle

            }//end of while(c<NCMAX)
        }

        // Construct Ant Solution
        if (bestFitness > 0 && bestPath != null) {
            bestSolution = new Individual(mdmtsp);
            int size = mdmtsp.getCustomers().size();
            int[][] chromosome = new int[2][size];
            int k = 0;
            if (!bestPath.isEmpty()) {
                for (int p = 0; p < bestPath.size(); p++) {
                    ArrayList<Integer> salesmanPath = bestPath.get(p);
                    if (salesmanPath != null && !salesmanPath.isEmpty() && salesmanPath.size() > 1) {
                        chromosome[0][k] = salesmanPath.get(1);
                        chromosome[1][k] = salesmanPath.get(0);
                        k++;
                        for (int i = 2; i < salesmanPath.size(); i++) {
                            chromosome[0][k] = salesmanPath.get(i);
                            chromosome[1][k] = -1;
                            k++;
                        }

                    }
                }
                bestSolution.setChromosome(chromosome);
                bestSolution.calculateFitness();
            }

        }
    }
}

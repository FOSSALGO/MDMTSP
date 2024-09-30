package abc;

import smo.*;
import mdmtsp.Individual;
import mdmtsp.MDMTSP;
import mdmtsp.Random;

public class ArtificialBeeColony {

    // SMO Parameters
    //INPUT
    private int I;      //Total Number of Iterations
    private int N;      //Total Number of Spider Monkey
    private int MG;     //Allowed Maximum Group
    private double pr;  //Perturbation Rate
    private int LLL;    //Local Leader Limit
    private int GLL;    //Global Leader Limit

    double mutationRate = 0.9;

    // variables
    private MDMTSP mdmtsp = null;
    private Individual bestSolution = null;

    //VARIABLES
    private int t = 0;//iteration counter
    private int g = 0;//Current Number of Group
    private int groupSize = 1;//banyaknya spider monkey di setiap group
    private Individual[] spiderMonkey = null;//SM = Population of Spider Monkey
    private Individual globalLeader = null;
    private int globalLeaderLimitCounter = 0;//GLLc = Global Leader Limit Counter
    private Individual[] localLeader = null;//LL = List of Local Leader
    private int[] localLeaderLimitCounter = new int[g];//LLLc = Local Leader Limit Counter of kth Group

    public ArtificialBeeColony(MDMTSP mdmtsp, int totalNumberOfIterations, int totalNumberOfSpiderMonkey, int allowedMaximumGroup, double perturbationRate, int localLeaderLimit, int globalLeaderLimit) {
        this.mdmtsp = mdmtsp;
        this.I = totalNumberOfIterations;
        this.N = totalNumberOfSpiderMonkey;
        this.MG = allowedMaximumGroup;
        this.pr = perturbationRate;
        this.LLL = localLeaderLimit;
        this.GLL = globalLeaderLimit;
    }

    public boolean init() {
        boolean status = false;
        if (this.mdmtsp != null
                && I > 0
                && N > 0) {
            // Initialization---------------------------------------------------
            if (this.MG < (N / 2)) {
                this.MG = N / 2;
            }
            if (this.MG <= 0) {
                this.MG = 1;
            }
            this.t = 1;//(1) t ← 1
            this.spiderMonkey = new Individual[N];//(2) create N spider moneys and append them to SM

            //(3)Assign each SMi in SM with a random solution
            int indexGlobalLeader = -1;
            double globalFitness = 0;
            for (int i = 0; i < N; i++) {
                spiderMonkey[i] = new Individual(this.mdmtsp);
                spiderMonkey[i].generateRandomChromosome();
                spiderMonkey[i].calculateFitness();
                //System.out.println(spiderMonkey[i]);
                if (globalFitness < spiderMonkey[i].getFitness()) {
                    globalFitness = spiderMonkey[i].getFitness();
                    indexGlobalLeader = i;
                }
            }

            //(4) g = 1 initially consider all spiderMonkey into one group
            this.g = 1;
            this.groupSize = (int) Math.floor((double) N / (double) g);

            //(5) Select Local Leader and Global Leader // Both leaders are same due to single group
            //GLOBAL LEADER
            this.globalLeader = spiderMonkey[indexGlobalLeader].clone();
            this.globalLeaderLimitCounter = 0;//GLLc

            //LOCAL LEADER
            this.localLeader = new Individual[g];
            this.localLeader[0] = this.globalLeader.clone();
            this.localLeaderLimitCounter = new int[g];//LLLc
            status = true;
        }
        return status;
    }

    public Individual getBestSolution() {
        return this.bestSolution;
    }

    public void process() {
        if (init()) {
            Operation op = new Operation();
            while (t <= I) {
                //==================================================================
                //[1] Update of Spider Monkeys
                //==================================================================
                //[1.1] UPDATE Spider Monkeys base on local Leader 
                for (int k = 0; k < g; k++) {
                    //set lower and upper bound
                    int lowerBound = k * groupSize;
                    int upperBound = lowerBound + groupSize - 1;
                    if (k == g - 1) {
                        upperBound = N - 1;
                    }

                    //update spider monkey
                    for (int i = lowerBound; i <= upperBound; i++) {
                        double u = Random.getRandomUniform();//U(0,1)
                        if (u >= pr) {
                            int[] chromosomeLLk = localLeader[k].getCustomerChromosome();
                            int[] chromosomeSMi = spiderMonkey[i].getCustomerChromosome();
                            int r = Random.getRandomBetween(lowerBound, upperBound);
                            int[] chromosomeRSMr = spiderMonkey[r].getCustomerChromosome();

                            int[][] ss1 = op.subtraction(chromosomeSMi, chromosomeLLk);
                            int[][] ss2 = op.subtraction(chromosomeSMi, chromosomeRSMr);
                            int[][] ss = op.merge(ss1, ss2);

                            // Apply SS into SMi to calculate newSM
                            // Tentative SO
                            Individual tentative = spiderMonkey[i].clone();
                            tentative.swapSequence(ss);
                            tentative.calculateFitness();
                            
                            if (tentative.getFitness() > spiderMonkey[i].getFitness()) {
                                spiderMonkey[i] = tentative;
                            } else {
                                //Mutate
                                double rm = Random.getRandomUniform();
                                if (rm > mutationRate) {
                                    Individual mutant = spiderMonkey[i].clone();
                                    // customerMutation
                                    int index = Random.getRandomBetween(0, mutant.getChromosome()[0].length - 1);
                                    mutant.customerMutation(index);
                                    mutant.calculateFitness();
                                    if (mutant.getFitness() >= spiderMonkey[i].getFitness()) {
                                        spiderMonkey[i] = mutant.clone();
                                    }

                                    // salesmanMutation
                                    index = Random.getRandomBetween(0, mutant.getOriginDepot().length - 1);
                                    mutant.salesmanMutation(index);
                                    mutant.calculateFitness();
                                    if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                                        spiderMonkey[i] = mutant.clone();
                                    }
                                }
                            }//end of else

                        }//end of if (u >= pr)                     
                    }//end of for (int i = lowerBound; i <= upperBound; i++)
                }//end of for (int k = 0; k < g; k++)

                //[1.2] UPDATE Spider Monkeys base on global Leader 
                for (int k = 0; k < g; k++) {
                    //set lower and upper bound
                    int lowerBound = k * groupSize;
                    int upperBound = lowerBound + groupSize - 1;
                    if (k == g - 1) {
                        upperBound = N - 1;
                    }

                    //update spider monkey
                    for (int i = lowerBound; i <= upperBound; i++) {
                        double u = Random.getRandomUniform();//U(0,1)
                        double prob = 0.9 * ((double) globalLeader.getDistance() / (double) spiderMonkey[i].getDistance()) + 0.1;//prob(i)
                        if (u <= prob) {
                            int[] chromosomeGl = globalLeader.getCustomerChromosome();
                            int[] chromosomeSMi = spiderMonkey[i].getCustomerChromosome();
                            int r = Random.getRandomBetween(0, N - 1);
                            int[] chromosomeRSMr = spiderMonkey[r].getCustomerChromosome();

                            int[][] ss1 = op.subtraction(chromosomeSMi, chromosomeGl);
                            int[][] ss2 = op.subtraction(chromosomeSMi, chromosomeRSMr);
                            int[][] ss = op.merge(ss1, ss2);

                            //Apply SS into SMi to calculate newSM
                            // Tentative SO
                            Individual tentative = spiderMonkey[i].clone();
                            tentative.swapSequence(ss);
                            if (tentative.getFitness() > spiderMonkey[i].getFitness()) {
                                spiderMonkey[i] = tentative;
                            } else {
                                //Mutate
                                double rm = Random.getRandomUniform();
                                if (rm > mutationRate) {
                                    Individual mutant = spiderMonkey[i].clone();
                                    // customerMutation
                                    int index = Random.getRandomBetween(0, mutant.getChromosome()[0].length - 1);
                                    mutant.customerMutation(index);
                                    mutant.calculateFitness();
                                    if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                                        spiderMonkey[i] = mutant.clone();
                                    }

                                    // salesmanMutation
                                    index = Random.getRandomBetween(0, mutant.getOriginDepot().length - 1);
                                    mutant.salesmanMutation(index);
                                    mutant.calculateFitness();
                                    if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                                        spiderMonkey[i] = mutant.clone();
                                    }
                                }
                            }//end of else

                        }//end of if (u >= pr)                     
                    }//end of for (int i = lowerBound; i <= upperBound; i++)
                }//end of for (int k = 0; k < g; k++)

                //////MUTATION
                // MUTATION-----------------------------------------------------
                /*
                for (int i = 0; i < spiderMonkey.length; i++) {
                    double rm = Random.getRandomUniform();
                    double mutationRate = 0.2;
                    if (rm > mutationRate) {
                        Individual mutant = spiderMonkey[i].clone();
                        // customerMutation
                        
                        int index = Random.getRandomBetween(0, mutant.getChromosome()[0].length - 1);
                        
                        mutant.customerMutation(index);
                        mutant.calculateFitness();
                        if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                            spiderMonkey[i] = mutant.clone();
                        }
                        
                        // salesmanMutation
                        index = Random.getRandomBetween(0, mutant.getOriginDepot().length - 1);
                        mutant.salesmanMutation(index);
                        mutant.calculateFitness();
                        if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                            spiderMonkey[i] = mutant.clone();
                        }
                    }
                }
                 */
                //==================================================================
                //[2] Update of Local Leaders and Global Leader
                //==================================================================
                //[2.1] check new local leader
                Individual newGlobalLeader = globalLeader.clone();
                for (int k = 0; k < g; k++) {
                    //set lower and upper bound
                    int lowerBound = k * groupSize;
                    int upperBound = lowerBound + groupSize - 1;
                    if (k == g - 1) {
                        upperBound = N - 1;
                    }

                    Individual newLocalLeader = localLeader[k].clone();
                    for (int i = lowerBound; i <= upperBound; i++) {
                        if (spiderMonkey[i].getFitness() > newLocalLeader.getFitness()) {
                            newLocalLeader = spiderMonkey[i];
                        }
                    }

                    if (newLocalLeader.getFitness() > localLeader[k].getFitness()) {
                        localLeader[k] = newLocalLeader.clone();
                        localLeaderLimitCounter[k] = 0;
                    } else {
                        localLeaderLimitCounter[k]++;//localLeaderLimitCounter[k] = localLeaderLimitCounter[k] + 1;
                    }

                    if (localLeader[k].getFitness() > newGlobalLeader.getFitness()) {
                        newGlobalLeader = localLeader[k];
                    }

                }//end of for (int k = 0; k < g; k++)

                //check new global leader
                if (newGlobalLeader.getFitness() > globalLeader.getFitness()) {
                    globalLeader = newGlobalLeader.clone();
                    globalLeaderLimitCounter = 0;
                } else {
                    globalLeaderLimitCounter++;
                }

                //==================================================================
                //[3] Decision Phase of Local Leader and Global Leader
                //==================================================================
                //[3.1] Decision Phase of Local Leader
                for (int k = 0; k < g; k++) {
                    if (localLeaderLimitCounter[k] > LLL) {
                        localLeaderLimitCounter[k] = 0;//LLLk ← 0

                        //set lower and upper bound
                        int lowerBound = k * groupSize;
                        int upperBound = lowerBound + groupSize - 1;
                        if (k == g - 1) {
                            upperBound = N - 1;
                        }

                        for (int i = lowerBound; i <= upperBound; i++) {
                            double u = Random.getRandomUniform();//U(0,1)
                            if (u >= pr) {
                                //Initialize SMi randomly
                                Individual newSM = new Individual(this.mdmtsp);
                                newSM.generateRandomChromosome();
                                newSM.calculateFitness();
                                spiderMonkey[i] = newSM;
                            } else {
                                //Initialize SMi by interacting with the GL and LL
                                int[] chromosomeGL = globalLeader.getCustomerChromosome();
                                int[] chromosomeLLk = localLeader[k].getCustomerChromosome();
                                int[] chromosomeSMi = spiderMonkey[i].getCustomerChromosome();

                                int[][] ss1 = op.subtraction(chromosomeSMi, chromosomeGL);
                                int[][] ss2 = op.subtraction(chromosomeSMi, chromosomeLLk);
                                int[][] ss = op.merge(ss1, ss2);

                                //Apply SS into SMi to calculate newSM
                                // Tentative SO
                                Individual tentative = spiderMonkey[i].clone();
                                tentative.swapSequence(ss);
                                if (tentative.getFitness() > spiderMonkey[i].getFitness()) {
                                    spiderMonkey[i] = tentative;
                                } else {
                                    //Mutate
                                    double rm = Random.getRandomUniform();
                                    if (rm > mutationRate) {
                                        Individual mutant = spiderMonkey[i].clone();
                                        // customerMutation
                                        int index = Random.getRandomBetween(0, mutant.getChromosome()[0].length - 1);
                                        mutant.customerMutation(index);
                                        mutant.calculateFitness();
                                        if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                                            spiderMonkey[i] = mutant.clone();
                                        }

                                        // salesmanMutation
                                        index = Random.getRandomBetween(0, mutant.getOriginDepot().length - 1);
                                        mutant.salesmanMutation(index);
                                        mutant.calculateFitness();
                                        if (mutant.getFitness() > spiderMonkey[i].getFitness()) {
                                            spiderMonkey[i] = mutant.clone();
                                        }
                                    }
                                }//end of else

                            }
                        }
                    }
                }//end of for (int k = 0; k < g; k++)

                //[3.1] Decision Phase of Global Leader
                if (globalLeaderLimitCounter > GLL) {
                    globalLeaderLimitCounter = 0;
                    if (g < MG) {
                        //Divide the spider monkeys into g + 1 number of groups
                        g++;// g = g + 1
                        groupSize = (int) Math.floor((double) N / (double) g);
                        this.localLeader = new Individual[g];
                        this.localLeaderLimitCounter = new int[g];//LLLc

                        int indexGlobalLeader = -1;
                        double globalFitness = 0;

                        //check new local leader and global leader
                        for (int k = 0; k < g; k++) {
                            //set lower and upper bound
                            int lowerBound = k * groupSize;
                            int upperBound = lowerBound + groupSize - 1;
                            if (k == g - 1) {
                                upperBound = N - 1;
                            }

                            //find new local leader
                            Individual newLocalLeader = spiderMonkey[lowerBound];
                            for (int i = lowerBound + 1; i <= upperBound; i++) {
                                if (spiderMonkey[i].getFitness() > newLocalLeader.getFitness()) {
                                    newLocalLeader = spiderMonkey[i];
                                }

                            }
                            this.localLeader[k] = newLocalLeader.clone();

                            //find new global leader
                            if (globalFitness < this.localLeader[k].getFitness()) {
                                globalFitness = this.localLeader[k].getFitness();
                                indexGlobalLeader = k;
                            }
                        }//end of for (int k = 0; k < g; k++)

                        //update GLOBAL LEADER
                        if (this.localLeader[indexGlobalLeader].getFitness() > this.globalLeader.getFitness()) {
                            this.globalLeader = this.localLeader[indexGlobalLeader].clone();
                        }

                    } else {
                        //Disband all the groups and Form a single group.
                        System.out.println("DISBAND");
                        g = 1;
                        groupSize = (int) Math.floor((double) N / (double) g);
                        this.localLeader = new Individual[g];
                        this.localLeader[0] = this.globalLeader.clone();
                        this.localLeaderLimitCounter = new int[g];//LLLc
                    }
                }

                //INCREMENT of t====================================================
                t++;//increment t = t+1
            }//end of while while(t<=I);
            bestSolution = this.globalLeader.clone();
        }
    }

}

package test;

import ga.GeneticAlgorithm;
import ga.SelectionOperationType;
import mdmtsp.Individual;
import mdmtsp.MDMTSP;

public class Test009_testAllDepots {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/burma14.tsp";//"src/dataset/kro124p.atsp";//
        double min_distance = Double.MAX_VALUE;
        for (int i = 0; i < 14; i++) {
            System.out.println("Test-" + (i));
            int[] depots = {i};
            int numberOfSalesmans = 2;
            MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);
            int populationSize = 1000;
            int maxGeneration = 100;
            SelectionOperationType selection = SelectionOperationType.tournament;
            int numberOfIndividualsSelected = 500;
            double mutationRate = 0.1;
            GeneticAlgorithm ga = new GeneticAlgorithm(mdmtsp, populationSize, maxGeneration, selection, numberOfIndividualsSelected, mutationRate);
            ga.process();
            if (ga.getBestSolution().getDistance() < min_distance) {
                min_distance = ga.getBestSolution().getDistance();
            }
            System.out.println(ga.getBestSolution());
            System.out.println("Distance: "+ga.getBestSolution().getDistance());
            System.out.println("Fitness : "+ga.getBestSolution().getFitness());
            System.out.println("--------------------------------------");
        }
        System.out.println("MIN: " + min_distance);

        System.out.println("BEST KNOWN");
        int[][] chromosome = {
            {1, 13, 2, 3, 4, 5, 11, 6, 12, 7, 10, 8, 9},
            {0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
        };
        Individual indv = new Individual();
        int[] depots = {0};
        int numberOfSalesmans = 1;
        MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);
        indv.setMDMTSP(mdmtsp);
        indv.setChromosome(chromosome);
        indv.calculateFitness();
        System.out.println("indv");
        System.out.println(indv.getDistance());
        System.out.println(indv.getFitness());
    }
}

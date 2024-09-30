package test;

import ga.GeneticAlgorithm;
import ga.SelectionOperationType;
import mdmtsp.MDMTSP;

public class Test006GA {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/kro124p.atsp";
        int[] depots = {10, 27, 38, 43, 89, 1};
        int numberOfSalesmans = 10;

        MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);
        int populationSize = 10000;
        int maxGeneration = 1000;
        SelectionOperationType selection = SelectionOperationType.tournament;
        int numberOfIndividualsSelected = 5000;
        double mutationRate = 0.1;
        GeneticAlgorithm ga = new GeneticAlgorithm(mdmtsp, populationSize, maxGeneration, selection, numberOfIndividualsSelected, mutationRate);
        ga.process();
        
    }
}

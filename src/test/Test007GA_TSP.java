package test;

import ga.GeneticAlgorithm;
import ga.SelectionOperationType;
import java.util.Arrays;
import mdmtsp.MDMTSP;

public class Test007GA_TSP {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/burma14.tsp";//"src/dataset/kro124p.atsp";//
        int[] depots = {0};
        int numberOfSalesmans = 1;

        MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);
        //System.out.println("Customers: "+mdmtsp.getCustomers());
        //System.out.println("-------------------------------------------------------");
//        for (int i = 0; i < mdmtsp.getAdjacency().length; i++) {
//            System.out.println(Arrays.toString(mdmtsp.getAdjacency()[i]));
//        }
//        System.out.println("row: "+mdmtsp.getAdjacency().length);
//        System.out.println("col: "+mdmtsp.getAdjacency()[0].length);
        System.out.println("-------------------------------------------------------");
        int populationSize = 1000;
        int maxGeneration = 1000;
        SelectionOperationType selection = SelectionOperationType.tournament;
        int numberOfIndividualsSelected = 500;
        double mutationRate = 0.5;
        GeneticAlgorithm ga = new GeneticAlgorithm(mdmtsp, populationSize, maxGeneration, selection, numberOfIndividualsSelected, mutationRate);
        ga.process();        
    }
}

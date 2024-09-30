package test;

import mdmtsp.Individual;
import mdmtsp.MDMTSP;

public class Test008Individu {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/burma14.tsp";
        int[] depots = {0};
        int numberOfSalesmans = 1;

        MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);

        Individual individu = new Individual();
        individu.setMDMTSP(mdmtsp);
        individu.generateRandomChromosome();
        individu.calculateFitness();
        System.out.println(individu);
        System.out.println("Distance: " + individu.getDistance());
        System.out.println("Fitness : " + individu.getFitness());
        System.out.println("-------------------------------------------");

        //individu.shiftChange(1, 80);
        //individu.swapSalesman(5, 8);
        individu.salesmanMutation(0);
        individu.calculateFitness();
        System.out.println(individu);
        System.out.println("Distance: " + individu.getDistance());
        System.out.println("Fitness : " + individu.getFitness());
        System.out.println("-------------------------------------------");
        
        System.out.println("CLONE");
        Individual clone = individu.clone();
        System.out.println(clone);
        System.out.println("Distance: " + clone.getDistance());
        System.out.println("Fitness : " + clone.getFitness());
        System.out.println("-------------------------------------------");
    }
}

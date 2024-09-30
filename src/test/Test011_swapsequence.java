package test;

import mdmtsp.Individual;
import mdmtsp.MDMTSP;

public class Test011_swapsequence {

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
        System.out.println("d: "+individu.getDistance());
        System.out.println("f: "+individu.getFitness());
        System.out.println("----------------------------------------");
        
        int[][]ss={
            {1,5},
            {3,2},
            {1,4},
            {7,3},
        };
        individu.swapSequence(ss);
        System.out.println(individu);
        System.out.println("d: "+individu.getDistance());
        System.out.println("f: "+individu.getFitness());
        System.out.println("----------------------------------------");
    }
}

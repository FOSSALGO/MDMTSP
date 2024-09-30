package test;

import mdmtsp.Individual;
import mdmtsp.MDMTSP;

public class Test003Individu {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/kro124p.atsp";
        int[] depots = {10, 27, 38, 43, 89, 1};
        int numberOfSalesmans = 10;

        MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);

        Individual individu = new Individual();
        individu.setMDMTSP(mdmtsp);
        individu.generateRandomChromosome();
        individu.calculateFitness();
        System.out.println(individu);
        System.out.println("Distance: " + individu.getDistance());
        System.out.println("Fitness : " + individu.getFitness());
        System.out.println("-------------------------------------------");

        individu.resetCustomers();
        individu.calculateFitness();
        System.out.println(individu);
        System.out.println("Distance: " + individu.getDistance());
        System.out.println("Fitness : " + individu.getFitness());
        System.out.println("-------------------------------------------");

        individu.shiftChange(2, 30);
        individu.calculateFitness();
        System.out.println(individu);
        System.out.println("Distance: " + individu.getDistance());
        System.out.println("Fitness : " + individu.getFitness());
        System.out.println("-------------------------------------------");
    }
}

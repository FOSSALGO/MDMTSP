package test;

import java.util.Arrays;
import mdmtsp.Individual;
import mdmtsp.MDMTSP;
import smo.Operation;

public class Test012_SwapOperation {

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
        Individual T1 = individu.clone();
        int[][]ss={
            {1,5},
            {2,4},
            {7,9}
        };
        individu.swapSequence(ss);
        System.out.println(individu);
        System.out.println("d: "+individu.getDistance());
        System.out.println("f: "+individu.getFitness());
        System.out.println("----------------------------------------");
        
        Individual T2 = individu.clone();
        
        Operation op = new Operation();
        int[][]sss = op.subtraction(T1.getChromosome()[0], T2.getChromosome()[0]);
        for (int i = 0; i < sss.length; i++) {
            System.out.println(Arrays.toString(sss[i]));
        }
        
                
        
        
    }
}

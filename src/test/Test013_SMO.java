package test;

import mdmtsp.Individual;
import mdmtsp.MDMTSP;
import smo.SpiderMonkeyOptimization;

public class Test013_SMO {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/burma14.tsp";//"src/dataset/kro124p.atsp";//
        double min_distance = Double.MAX_VALUE;
        for (int i = 0; i < 14; i++) {
            System.out.println("Test-" + (i));
            int[] depots = {i};
            int numberOfSalesmans = 1;
            MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);
            int totalNumberOfIterations = 100;
            int totalNumberOfSpiderMonkey = 1000;
            int allowedMaximumGroup = 4;
            double perturbationRate = 0.2;
            int localLeaderLimit = 4;
            int globalLeaderLimit = 4;
            SpiderMonkeyOptimization smo = new SpiderMonkeyOptimization(mdmtsp, totalNumberOfIterations, totalNumberOfSpiderMonkey, allowedMaximumGroup, perturbationRate, localLeaderLimit, globalLeaderLimit);
            smo.process();
            if (smo.getBestSolution().getDistance() < min_distance) {
                min_distance = smo.getBestSolution().getDistance();
            }
            System.out.println(smo.getBestSolution());
            System.out.println("Distance: " + smo.getBestSolution().getDistance());
            System.out.println("Fitness : " + smo.getBestSolution().getFitness());
            System.out.println("--------------------------------------");
        }
        System.out.println("MIN: " + min_distance);

        
    }
}

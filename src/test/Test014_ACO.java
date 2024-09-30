package test;

import aco.AntColonyOptimization;
import mdmtsp.MDMTSP;

public class Test014_ACO {

    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/burma14.tsp";//"src/dataset/kro124p.atsp";//
        double min_distance = Double.MAX_VALUE;
        for (int i = 0; i < 14; i++) {
            System.out.println("Test-" + (i));
            int[] depots = {i,(i+7)%14};
            int numberOfSalesmans = 4;
            MDMTSP mdmtsp = new MDMTSP(filename, depots, numberOfSalesmans);
            int numberOfAnts = 1000;
            int maxIterations = 1000;
            double alpha = 0.5;
            double beta = 0.5;
            double evaporation = 0.5;
            AntColonyOptimization aco = new AntColonyOptimization(mdmtsp, numberOfAnts, maxIterations, alpha, beta, evaporation);
            aco.process();
            if (aco.getBestSolution().getDistance() < min_distance) {
                min_distance = aco.getBestSolution().getDistance();
            }
            System.out.println(aco.getBestSolution());
            System.out.println("Distance: " + aco.getBestSolution().getDistance());
            System.out.println("Fitness : " + aco.getBestSolution().getFitness());
            System.out.println("--------------------------------------");
        }
        System.out.println("MIN: " + min_distance);

        
    }
}

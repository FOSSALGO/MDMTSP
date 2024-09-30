package mdmtsp;

import java.util.ArrayList;

public class Individual {

    private MDMTSP mdmtsp = null;
    private int[][] chromosome = null;
    private int[][] originDepot = null;
    private double distance = Double.MAX_VALUE;
    private double fitness = 0;
    private final double DEPOT_CHANGE_THRESHOLD = 0.5;

    public Individual() {
    }
    
    public Individual(MDMTSP mdmtsp) {
        this.mdmtsp =mdmtsp;
    }

    public Individual clone() {
        Individual cloneIndividu = null;
        if (chromosome != null) {
            cloneIndividu = new Individual();
            cloneIndividu.setMDMTSP(mdmtsp);
            cloneIndividu.chromosome = null;
            cloneIndividu.chromosome = new int[chromosome.length][];
            for (int h = 0; h < chromosome.length; h++) {
                cloneIndividu.chromosome[h] = new int[chromosome[h].length];
                for (int i = 0; i < chromosome[h].length; i++) {
                    cloneIndividu.chromosome[h][i] = chromosome[h][i];
                }
            }
            cloneIndividu.findOriginDepot();
            cloneIndividu.calculateFitness();
        }
        return cloneIndividu;
    }

    public void setMDMTSP(MDMTSP mdmtsp) {
        this.mdmtsp = mdmtsp;
    }

    public int[][] generateRandomChromosome() {
        chromosome = null;
        if (mdmtsp != null) {
            ArrayList<Integer> depots = mdmtsp.getDepots();
            ArrayList<Integer> customers = mdmtsp.getCustomers();
            if (!depots.isEmpty() && !customers.isEmpty()) {
                ArrayList<Integer> unsetcustomers = new ArrayList<>();
                for (int i = 0; i < customers.size(); i++) {
                    unsetcustomers.add(customers.get(i));
                }
                chromosome = new int[2][customers.size()];
                //random unique position for salesman
                ArrayList<Integer> salesmanPosition = new ArrayList<>();
                salesmanPosition.add(0);// the first salesman should be placed in first index
                int m = 1;
                while (m < mdmtsp.getNumberOfSalesmans() && m < customers.size()) {
                    int r = Random.getRandomBetween(1, customers.size() - 1);
                    while (salesmanPosition.contains(r)) {
                        r = Random.getRandomBetween(1, customers.size() - 1);
                    }
                    salesmanPosition.add(r);
                    m++;
                }
                //set gens for chromosome
                for (int i = 0; i < customers.size(); i++) {
                    //random customers
                    int randomIndex = Random.getRandomBetween(0, unsetcustomers.size() - 1);
                    int alele = unsetcustomers.get(randomIndex);
                    chromosome[0][i] = alele;
                    unsetcustomers.remove(randomIndex);
                    //random depots as salesman origin depot
                    chromosome[1][i] = -1;
                    if (salesmanPosition.contains(i)) {
                        int randomDepot = Random.getRandomBetween(0, depots.size() - 1);
                        int depot = depots.get(randomDepot);// set origin depot for salesman
                        chromosome[1][i] = depot;
                    }
                }
            }
        }
        return chromosome;
    }

    public boolean resetCustomers() {
        boolean status = false;
        if (chromosome != null && mdmtsp != null) {
            ArrayList<Integer> customers = mdmtsp.getCustomers();
            if (!customers.isEmpty()) {
                ArrayList<Integer> unsetcustomers = new ArrayList<>();
                for (int i = 0; i < customers.size(); i++) {
                    unsetcustomers.add(customers.get(i));
                }
                // set new customers
                for (int i = 0; i < chromosome[0].length; i++) {
                    //random customers
                    int randomIndex = Random.getRandomBetween(0, unsetcustomers.size() - 1);
                    int alele = unsetcustomers.get(randomIndex);
                    chromosome[0][i] = alele;
                    unsetcustomers.remove(randomIndex);
                }
                status = true;
            }
        }
        return status;
    }

    public boolean resetSalesmans() {
        boolean status = false;
        if (chromosome != null && mdmtsp != null) {
            ArrayList<Integer> depots = mdmtsp.getDepots();
            if (!depots.isEmpty()) {
                //random unique position for salesman
                ArrayList<Integer> salesmanPosition = new ArrayList<>();
                salesmanPosition.add(0);// the first salesman should be placed in first index
                int m = 1;
                while (m < mdmtsp.getNumberOfSalesmans() && m < chromosome[1].length) {
                    int r = Random.getRandomBetween(1, chromosome[1].length - 1);
                    while (salesmanPosition.contains(r)) {
                        r = Random.getRandomBetween(1, chromosome[1].length - 1);
                    }
                    salesmanPosition.add(r);
                    m++;
                }
                // set new salesmans
                for (int i = 0; i < chromosome[1].length; i++) {
                    //random depots as salesman origin depot
                    chromosome[1][i] = -1;
                    if (salesmanPosition.contains(i)) {
                        int randomDepot = Random.getRandomBetween(0, depots.size() - 1);
                        int depot = depots.get(randomDepot);// set origin depot for salesman
                        chromosome[1][i] = depot;
                    }
                }
                status = true;
            }
        }
        return status;
    }

    public int[][] findOriginDepot() {
        originDepot = null;
        if (mdmtsp != null && chromosome != null) {
            int N = mdmtsp.getNumberOfSalesmans();
            originDepot = new int[N][2];//originDepot[i][0]=position; originDepot[i][1]=origin depot;
            //initialize originDepot with -1
            for (int i = 0; i < originDepot.length; i++) {
                originDepot[i][0] = -1;//position
                originDepot[i][1] = -1;//origin depot
            }
            //find salesman position and salesman's origin depot
            int n = 0;
            for (int i = 0; i < chromosome[1].length; i++) {
                if (chromosome[1][i] >= 0 && n < originDepot.length) {
                    originDepot[n][0] = i;//position
                    originDepot[n][1] = chromosome[1][i];//origin depot
                    n++;
                }
            }
        }
        return originDepot;
    }

    public boolean swapCustomers(int index1, int index2) {
        boolean status = false;
        if (chromosome != null
                && index1 >= 0
                && index2 >= 0
                && index1 < chromosome[0].length
                && index2 < chromosome[0].length
                && index1 != index2) {
            int temp = chromosome[0][index1];
            chromosome[0][index1] = chromosome[0][index2];
            chromosome[0][index2] = temp;
            status = true;
        }
        return status;
    }

    public boolean swapSalesman(int salesmanIndex1, int salesmanIndex2) {
        boolean status = false;
        findOriginDepot();
        if (chromosome != null
                && originDepot != null
                && salesmanIndex1 >= 0
                && salesmanIndex2 >= 0
                && salesmanIndex1 < originDepot.length
                && salesmanIndex2 < originDepot.length) {
            int position1 = originDepot[salesmanIndex1][0];
            int depot1 = originDepot[salesmanIndex1][1];
            int position2 = originDepot[salesmanIndex2][0];
            int depot2 = originDepot[salesmanIndex2][1];
            boolean swap = true;
            //The first salesman cannot be empty
            //never swap with depot = -1
            if ((position1 == 0 && depot2 == -1)
                    || (position2 == 0 && depot1 == -1)
                    || position1 < 0
                    || position2 < 0
                    || depot1 < 0
                    || depot2 < 0
                    || position1 >= chromosome[1].length
                    || position2 >= chromosome[1].length) {
                swap = false;
            }
            if (swap) {
                chromosome[1][position1] = depot2;
                chromosome[1][position2] = depot1;
                findOriginDepot();
                status = true;
            }
        }
        return status;
    }

    public boolean swapOperation(int index1, int index2) {
        return swapCustomers(index1, index2);
    }

    public int[][] swapSequence(int[][] swapOperations) {
        int[][] newChromosome = null;
        if (swapOperations != null && chromosome != null) {
            calculateFitness();
            Individual clone = clone();
            for (int i = 0; i < swapOperations.length; i++) {
                int index1 = swapOperations[i][0];
                int index2 = swapOperations[i][1];
                clone.swapOperation(index1, index2);
                clone.calculateFitness();
                if (clone.getFitness() > this.getFitness()) {
                    setChromosome(clone.getChromosome());
                    calculateFitness();
                }else{
                    System.out.println("NOX");
                }
            }
            newChromosome = chromosome;
        }
        return newChromosome;
    }

    public boolean shiftChange(int salesmanIndex, int destinationIndex) {
        // slesmanIndex: 0 to (number of salesman - 1)
        // destinationIndex; 0 to (chromosome length - 1)
        boolean status = false;
        findOriginDepot();
        if (chromosome != null
                && originDepot != null
                && salesmanIndex >= 0
                && salesmanIndex < originDepot.length
                && destinationIndex >= 0
                && destinationIndex < chromosome[1].length) {
            int position1 = originDepot[salesmanIndex][0];
            int depot1 = originDepot[salesmanIndex][1];
            int position2 = destinationIndex;
            int depot2 = chromosome[1][destinationIndex];
            boolean swap = true;
            //The first salesman cannot be empty
            //never swap with depot = -1
            if ((position1 == 0 && depot2 == -1)
                    || (position2 == 0 && depot1 == -1)
                    || position1 < 0
                    || position2 < 0
                    || position1 >= chromosome[1].length
                    || position2 >= chromosome[1].length) {
                swap = false;
            }
            if (swap) {
                chromosome[1][position1] = depot2;
                chromosome[1][position2] = depot1;
                findOriginDepot();
                status = true;
            }

        }
        return status;
    }

    public boolean customerMutation(int index) {
        boolean status = false;
        if (chromosome != null && index >= 0 && index < chromosome[0].length) {
            int index1 = index;
            int index2 = index;
            while (index2 == index1) {
                index2 = Random.getRandomBetween(0, chromosome[0].length - 1);
            }
            status = swapCustomers(index1, index2);
        }
        return status;
    }

    public boolean salesmanMutation(int salesmanIndex) {
        boolean status = false;
        findOriginDepot();
        if (mdmtsp != null
                && chromosome != null
                && originDepot != null
                && salesmanIndex >= 0
                && salesmanIndex < originDepot.length) {
            ArrayList<Integer> depots = mdmtsp.getDepots();
            int position = originDepot[salesmanIndex][0];//position
            int depot1 = originDepot[salesmanIndex][1];//old depot
            if (position >= 0 && position < chromosome[1].length) {
                int depot2 = depot1;//new depot
                if (depots.size() == 1) {
                    double rd = Random.getRandomUniform();
                    if (rd > DEPOT_CHANGE_THRESHOLD && position > 0) {
                        depot2 = -1;
                    }
                } else {
                    while (depot2 == depot1) {
                        depot2 = -1;
                        int randomDepot = -1;
                        if (position == 0) {
                            randomDepot = Random.getRandomBetween(0, depots.size() - 1);
                        } else {
                            double rd = Random.getRandomUniform();
                            if (rd > DEPOT_CHANGE_THRESHOLD) {
                                depot2 = -1;
                            } else {
                                randomDepot = Random.getRandomBetween(-1, depots.size() - 1);
                            }
                        }
                        if (randomDepot >= 0 && randomDepot < depots.size()) {
                            depot2 = depots.get(randomDepot);
                        }
                    }
                }
                //set new depot
                chromosome[1][position] = depot2;
                findOriginDepot();
                status = true;
            }
        }
        return status;
    }

    public double calculateDistance() {
        distance = Double.MAX_VALUE;
        double[][] adjacency = mdmtsp.getAdjacency();
        if (chromosome != null && adjacency != null) {
            distance = 0;
            int depot = chromosome[1][0];
            int nodeOrigin = depot;
            int nodeDestination = chromosome[0][0];
            distance += adjacency[nodeOrigin][nodeDestination];
            for (int i = 1; i < chromosome[0].length; i++) {
                int customer = chromosome[0][i];
                if (chromosome[1][i] != -1) {
                    //close the previous route
                    nodeOrigin = nodeDestination;
                    nodeDestination = depot;
                    distance += adjacency[nodeOrigin][nodeDestination];
                    //open new route
                    depot = chromosome[1][i];
                    nodeOrigin = depot;
                    nodeDestination = chromosome[0][i];
                    distance += adjacency[nodeOrigin][nodeDestination];
                } else {
                    nodeOrigin = nodeDestination;
                    nodeDestination = customer;
                    distance += adjacency[nodeOrigin][nodeDestination];
                }

                //check last customer                       
                if (i == chromosome[0].length - 1) {
                    // close the last route
                    nodeOrigin = nodeDestination;
                    nodeDestination = depot;
                    distance += adjacency[nodeOrigin][nodeDestination];
                    break;
                }
            }
        }
        return distance;
    }

    public double calculateFitness() {
        fitness = 0;
        calculateDistance();
        if (distance > 0) {
            fitness = 1.0 / distance;
        }
        return fitness;
    }

    public void setChromosome(int[][] chromosome) {
        this.chromosome = chromosome;
    }

    public int[][] getChromosome() {
        return this.chromosome;
    }
    
    public int[] getCustomerChromosome(){
        int[]customerChromosome = null;
        if(chromosome!=null){
            customerChromosome=new int[chromosome[0].length];
            for (int i = 0; i < customerChromosome.length; i++) {
                customerChromosome[i]=chromosome[0][i];
            }
        }
        return customerChromosome;
    }
    
    public void setCustomerChromosome(int[]customerChromosome){
        if(customerChromosome!=null){
            chromosome[0]=new int[customerChromosome.length];
            for (int i = 0; i < customerChromosome.length; i++) {
                chromosome[0][i]=customerChromosome[i];
            }
        }
    }

    public int[][] getOriginDepot() {
        return this.originDepot;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getFitness() {
        return this.fitness;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (chromosome != null) {
            int depot = chromosome[1][0];
            int nodeOrigin = depot;
            int nodeDestination = chromosome[0][0];
            int n = 1;
            sb.append("route-" + n + ": " + nodeOrigin + " - " + nodeDestination);
            for (int i = 1; i < chromosome[0].length; i++) {
                int customer = chromosome[0][i];
                if (chromosome[1][i] != -1) {
                    //close the previous route
                    sb.append(" - " + depot + "\n");
                    //open new route
                    depot = chromosome[1][i];
                    nodeOrigin = depot;
                    nodeDestination = chromosome[0][i];
                    n++;
                    sb.append("route-" + n + ": " + nodeOrigin + " - " + nodeDestination);
                } else {
                    nodeOrigin = nodeDestination;
                    nodeDestination = customer;
                    sb.append(" - " + nodeDestination);
                }
                //check last customer
                if (i == chromosome[0].length - 1) {
                    //close the last route
                    sb.append(" - " + depot);
                }
            }
        }
        return sb.toString();
    }

}

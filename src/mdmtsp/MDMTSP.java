package mdmtsp;

import java.util.ArrayList;

public class MDMTSP {

    private double[][] adjacency = null;
    private int M = 0;//NUMBER_OF_SALESMANS
    private ArrayList<Integer> depots = null;
    private ArrayList<Integer> customers = null;

    public MDMTSP() {
    }

    public MDMTSP(String filename, int[] depots, int numberOfSalesmans) {
        readDataset(filename);
        setDepots(depots);
        setCustomers();
        setNumberOfSalesmans(numberOfSalesmans);
    }

    public void readDataset(String filename) {
        this.adjacency = new DataReader().read(filename);
    }

    public void setDepots(int[] depots) {
        if (depots != null && depots.length > 0 && adjacency != null && adjacency.length > 0) {
            this.depots = new ArrayList<>();
            for (int i = 0; i < depots.length; i++) {
                if (depots[i] >= 0 && depots[i] < adjacency.length) {
                    this.depots.add(depots[i]);
                }
            }
        }
    }

    public void setCustomers() {
        if (adjacency != null && adjacency.length > 0 && this.depots != null) {
            customers = new ArrayList<>();
            for (int i = 0; i < adjacency.length; i++) {
                if (!depots.contains(i)) {
                    customers.add(i);
                }
            }
        }
    }

    public void setNumberOfSalesmans(int M) {
        if (M > 0) {
            this.M = M;
        }
    }
    
    public double[][] getAdjacency(){
        return this.adjacency;
    }

    public ArrayList<Integer> getDepots() {
        return this.depots;
    }

    public ArrayList<Integer> getCustomers() {
        return this.customers;
    }

    public int getNumberOfSalesmans() {
        return this.M;
    }

}


package test;

import mdmtsp.MDMTSP;

public class Test002MDMTSP {
    public static void main(String[] args) {
        //parameters
        String filename = "src/dataset/kro124p.atsp";
        int[]depots = {10, 27, 38, 43};
        
        MDMTSP mdmtsp = new MDMTSP();
        mdmtsp.readDataset(filename);
        mdmtsp.setDepots(depots);
        mdmtsp.setNumberOfSalesmans(10);
        
    }
}

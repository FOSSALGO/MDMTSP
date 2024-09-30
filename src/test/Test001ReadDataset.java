
package test;

import java.util.Arrays;
import mdmtsp.DataReader;

public class Test001ReadDataset {
    public static void main(String[] args) {
        DataReader dr = new DataReader();
        double[][]adjacency = dr.read("src/dataset/kro124p.atsp");
        
        //double[][]adjacency = dr.adjacency;
        if(adjacency!=null){
            for (int i = 0; i < adjacency.length; i++) {
                System.out.println(Arrays.toString(adjacency[i]));
            }
        }
    }
}

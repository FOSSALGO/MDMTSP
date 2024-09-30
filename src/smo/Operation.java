package smo;

import java.util.ArrayList;

public class Operation {

    public int[] swap(int[] chromosome, int index1, int index2) {
        if (chromosome != null
                && index1 >= 0 && index1 < chromosome.length
                && index2 >= 0 && index2 < chromosome.length
                && chromosome[index1] != chromosome[index2]) {
            int temp = chromosome[index1];
            chromosome[index1] = chromosome[index2];
            chromosome[index2] = temp;
        }
        return chromosome;
    }

    public int[] add(int[] chromosome, SO so) {
        return swap(chromosome, so.index1, so.index2);
    }

    public int[][] subtraction(int[] chromosome1, int[] chromosome2) {
        int[][] swapsequence = null;
        if (chromosome1 != null && chromosome2 != null && chromosome1.length == chromosome2.length) {
            ArrayList<SO> listSwapOperation = new ArrayList<>();
            int[] chromosomeOperation = cloneChromosome(chromosome1);
            for (int i = 0; i < chromosome2.length; i++) {
                int value = chromosome2[i];
                for (int j = i; j < chromosomeOperation.length; j++) {
                    if (value == chromosomeOperation[j] && i != j) {
                        SO so = new SO(i, j);
                        chromosomeOperation = add(chromosomeOperation, so);
                        listSwapOperation.add(so);
                        break;
                    }
                }

                /*
                if (chromosomeOperation[i] != value) {
                    for (int j = i + 1; j < chromosomeOperation.length; j++) {
                        if (value == chromosomeOperation[j]) {
                            SO so = new SO(i, j);
                            chromosomeOperation = add(chromosomeOperation, so);
                            listSwapOperation.add(so);
                            break;
                        }
                    }
                }
                 */
            }
            if (!listSwapOperation.isEmpty()) {
                swapsequence = new int[listSwapOperation.size()][2];
                for (int i = 0; i < swapsequence.length; i++) {
                    SO so = listSwapOperation.get(i);
                    swapsequence[i][0] = so.index1;
                    swapsequence[i][1] = so.index2;
                }
            }
        }
        return swapsequence;
    }

    public int[][] merge(int[][] swapsequence1, int[][] swapsequence2) {
        int[][] result = null;
        if (swapsequence1 != null && swapsequence2 != null) {
            result = new int[swapsequence1.length + swapsequence2.length][2];
            int k = 0;
            for (int i = 0; i < swapsequence1.length; i++) {
                result[k][0] = swapsequence1[i][0];
                result[k][1] = swapsequence1[i][1];
                k++;
            }
            for (int i = 0; i < swapsequence2.length; i++) {
                result[k][0] = swapsequence2[i][0];
                result[k][1] = swapsequence2[i][1];
                k++;
            }
        } else if (swapsequence1 != null) {
            result = new int[swapsequence1.length][2];
            for (int i = 0; i < swapsequence1.length; i++) {
                result[i][0] = swapsequence1[i][0];
                result[i][1] = swapsequence1[i][1];
            }
        } else if (swapsequence2 != null) {
            result = new int[swapsequence2.length][2];
            for (int i = 0; i < swapsequence2.length; i++) {
                result[i][0] = swapsequence2[i][0];
                result[i][1] = swapsequence2[i][1];
            }
        }
        return result;
    }

    public int[] cloneChromosome(int[] chromosome) {
        int[] newChromosome = null;
        if (chromosome != null) {
            newChromosome = new int[chromosome.length];
            for (int i = 0; i < chromosome.length; i++) {
                newChromosome[i] = chromosome[i];
            }
        }
        return newChromosome;
    }

    public class SO {

        int index1;
        int index2;

        public SO(int index1, int index2) {
            this.index1 = index1;
            this.index2 = index2;
        }

    }
}

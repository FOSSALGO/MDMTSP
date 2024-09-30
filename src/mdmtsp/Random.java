package mdmtsp;

public class Random {
    private static java.util.Random random = new java.util.Random();
    
    public static int getRandomBetween(int min, int max) { 
        if(min>max){
            int temp = min;
            min = max;
            max = temp;
        }
        return random.nextInt(1 + max - min) + min;
    }
    
    public static double getRandomUniform(){
        return random.nextDouble();
    }
}

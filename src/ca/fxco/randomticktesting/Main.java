package ca.fxco.randomticktesting;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    public static int TOTAL_TICKS = ((60 * 20) * 60) * 64; // 64 hours in ticks
    public static int TOTAL_CHUNKS = 47750;
    public static int RAND_TICK_SPEED = 3;

    private static final TotallyChunk[] testingChunks = new TotallyChunk[TOTAL_CHUNKS];

    protected static int lcgBlockSeed = new Random().nextInt();


    public static void main(String[] args) {
        for (int i = 0; i < TOTAL_CHUNKS; i++)
            testingChunks[i] = new TotallyChunk(i, new TotallySugarCaneBlock());
        for (int tick = 0; tick < TOTAL_TICKS; tick++) {
            for (int randTickSpeed = 0; randTickSpeed < RAND_TICK_SPEED; randTickSpeed++) {
                int linearPos = getRandomLinearPosInChunk(); // Need to take Y into account for actual rates
                for (TotallyChunk totallyChunk : testingChunks)
                    totallyChunk.randomTickChunk(linearPos);
            }
            if (tick % (TOTAL_TICKS/40) == 0)
                System.out.println((((float)tick/(float)TOTAL_TICKS)*100.0F)+"% done");
        }
        int largest = 0;
        int largestTime = 0;
        int secondLargest = 0;
        int secondLargestTime = 0;
        HashMap<Integer,Integer> data = new HashMap<>();
        for (int i = 0; i < TOTAL_CHUNKS; i++) {
            int count = testingChunks[i].getCount();
            if (count != 0) {
                if (count > largest) {
                    secondLargest = largest;
                    secondLargestTime = largestTime;
                    largest = count;
                    largestTime = i;
                }
                data.put(i,count);
                System.out.println(i + "," + count);
            }
        }
        System.out.println("==========");
        System.out.println("Best Time: "+largestTime);
        System.out.println("Real Time: "+(largestTime/20.0)/60.0);
        System.out.println("Amount: "+largest);
        System.out.println("==========");
        System.out.println("Second Best Time: "+secondLargestTime);
        System.out.println("Second Real Time: "+(secondLargestTime/20.0)/60.0);
        System.out.println("Amount: "+secondLargest);
        System.out.println("==========");
        System.out.println("Performance increase: "+((float)secondLargest/(float)largest));
        System.out.println("==========");
        writeToCsv(data);
    }

    public static int getRandomLinearPosInChunk() {
        lcgBlockSeed = lcgBlockSeed * 3 + 1013904223;
        int j = lcgBlockSeed >> 2;
        return j & 15 + (j >> 8 & 15) * 16;
    }

    public static void writeToCsv(HashMap<Integer,Integer> data) {
        try {
            FileWriter resultWriter = new FileWriter("results.csv");
            for (Map.Entry<Integer,Integer> value : data.entrySet())
                resultWriter.write(value.getKey()+","+value.getValue()+"\n");
            resultWriter.close();
            System.out.println("Successfully wrote results to the file");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

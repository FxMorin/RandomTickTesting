package ca.fxco.randomticktesting;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final int TOTAL_TICKS = ((60 * 20) * 60) * 80; // 32 hours in ticks
    public static final int TOTAL_SIMULATIONS = 12500; // Basically how many ticks
    public static final int CHUNKS_PER_TICK = 1; // How many chunks to include per simulation. Usually 1
    public static final int DELAY_PER_CHUNKS = 5; // How many delay is there between chunks. Should be >= 1
    public static final int TOTAL_CHUNKS = TOTAL_SIMULATIONS * CHUNKS_PER_TICK; // Basically how many ticks
    public static final int START_AT = 0;
    public static final int RAND_TICK_SPEED = 3;
    public static final int TOTAL_THREADS = 30; // 1592 per thread (Math.ceil(TOTAL_CHUNKS/TOTAL_THREADS))
    public static final int FEEDBACK_STEPS = 100; // How many times it should tell you its current progress
    public static final int TICKS_PER_FEEDBACK_STEP = TOTAL_TICKS / FEEDBACK_STEPS;
    public static final int TOTAL_CHUNKS_PER_THREAD = (int)Math.ceil((float)(TOTAL_CHUNKS) / (float)TOTAL_THREADS);
    public static final boolean USE_REAL_RATES = true;

    private static final TotallyChunk[] testingChunks = new TotallyChunk[TOTAL_CHUNKS];
    private static final int[] reachedTarget = new int[FEEDBACK_STEPS];
    private static final TotallyBlockPos[][][] preComputerRandoms = new TotallyBlockPos[TOTAL_TICKS][RAND_TICK_SPEED][CHUNKS_PER_TICK];

    private static long timeAverage = 0;
    private static long lastTime;

    protected static int lcgBlockSeed = new Random().nextInt();

    public static void initializeChunks() {
        for (int i = 0; i < TOTAL_CHUNKS; i++)
            testingChunks[i] = new TotallyChunk(START_AT+i*DELAY_PER_CHUNKS, new TotallySugarCaneBlock());
    }

    public static TotallyBlockPos getRandomLinearPosInChunk() {
        lcgBlockSeed = lcgBlockSeed * 3 + 1013904223;
        int j = lcgBlockSeed >> 2;
        return new TotallyBlockPos(j & 15 + (j >> 8 & 15) * 16, USE_REAL_RATES ? -1 : (short)(j >> 16 & 15));
    }

    public static void initializeRandoms() {
        System.out.println("Initializing Randoms");
        for (int tick = 0; tick < TOTAL_TICKS; tick++)
            for (int speed = 0; speed < RAND_TICK_SPEED; speed++)
                for (int chunks = 0; chunks < CHUNKS_PER_TICK; chunks++)
                    preComputerRandoms[tick][speed][chunks] = getRandomLinearPosInChunk();
    }

    public static Runnable simulateTicksForChunks(int start, int amount) {
        return () -> {
            int finish = Math.min(start + amount, TOTAL_CHUNKS);
            for (int tick = 0; tick < TOTAL_TICKS; tick++) {
                for (int randTickSpeed = 0; randTickSpeed < RAND_TICK_SPEED; randTickSpeed++)
                    for (int i = start; i < finish; i += CHUNKS_PER_TICK)
                        for (int c = 0; c < CHUNKS_PER_TICK; c++)
                            testingChunks[i+c].randomTickChunk(preComputerRandoms[tick][randTickSpeed][c]);
                if (tick % TICKS_PER_FEEDBACK_STEP == 0 && tick != 0) {
                    int step = (int) ((float) tick / (float) TICKS_PER_FEEDBACK_STEP)-1;
                    if (++reachedTarget[step] == TOTAL_THREADS) {
                        long timeDiff = System.currentTimeMillis()-lastTime;
                        timeAverage += timeDiff;
                        long timeDiff2 = timeDiff/1000;
                        long average = (timeAverage/(step+1))/1000;
                        long estimate = average * (FEEDBACK_STEPS-(step+1));
                        System.out.println((((float) tick / (float) TOTAL_TICKS) * 100.0F) + "% - "+(timeDiff2/60)+"m "+(timeDiff2%60)+"s - (est. "+(estimate/60)+"m "+(estimate%60)+"s) - (avg. "+(average/60)+"m "+(average%60)+"s)");
                        lastTime = System.currentTimeMillis();
                    }
                }
            }
        };
    }

    public static void simulateTicksInParallel() {
        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS);
        System.out.println("Initializing Threads");
        lastTime = System.currentTimeMillis();
        for (int i = 0; i < TOTAL_THREADS; i++)
            executor.execute(simulateTicksForChunks(i * TOTAL_CHUNKS_PER_THREAD, TOTAL_CHUNKS_PER_THREAD));
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    public static void main(String[] args) {
        Arrays.fill(reachedTarget, 0, FEEDBACK_STEPS, 0);
        initializeChunks();
        initializeRandoms();
        simulateTicksInParallel();
        getResults();
    }

    public static void getResults() {
        HashMap<Integer, Integer> data = new HashMap<>();
        int largest = 0;
        int largestTime = 0;
        for (int i = 0; i < TOTAL_CHUNKS; i++) {
            int count = testingChunks[i].getHarvests();
            if (count > largest) {
                largest = count;
                largestTime = i;
                System.out.println(START_AT+i*DELAY_PER_CHUNKS + "," + count);
            }
            data.put(START_AT+i*DELAY_PER_CHUNKS, count);
        }
        System.out.println("==========");
        int time = START_AT+largestTime*DELAY_PER_CHUNKS;
        System.out.println("Best Time: "+time);
        System.out.println("Real Time: "+(time/20)/60+"m "+(time%60)+"s");
        System.out.println("Amount: "+largest);
        System.out.println("==========");
        writeToCsv(data);
    }

    public static void writeToCsv(HashMap<Integer,Integer> data) {
        try {
            FileWriter resultWriter = new FileWriter("results.csv");
            resultWriter.write("tick,harvested\n");
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

package ca.fxco.randomticktesting;

public class TotallyChunk {

    // Chunks are 100% just a single row of blocks. Yup

    private final TotallyBlock[] blocks = new TotallyBlock[256];
    private final int delayLength;

    private int currentDelay = 0;
    private int harvests = 0;

    public TotallyChunk(int delay, TotallyBlock block) {
        this.delayLength = delay;
        for (int i = 0; i < 256; i++)
            blocks[i] = block.copy();
    }

    public void randomTickChunk(int linearPos) {
        blocks[linearPos].randomTick();
        currentDelay++;
        if (currentDelay >= delayLength) {
            for (TotallyBlock block : blocks)
                this.harvests += block.harvest();
            currentDelay = 0;
        }
    }

    public int getCount() {
        return this.harvests;
    }
}

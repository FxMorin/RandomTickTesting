package ca.fxco.randomticktesting;

public class TotallySugarCaneBlock implements TotallyBlock {

    private int height;
    private int age;

    public TotallySugarCaneBlock() {
        this.height = 1;
        this.age = 0;
    }

    public TotallySugarCaneBlock(int height, int age) {
        this.height = height;
        this.age = age;
    }

    public void randomTick() {
        if (height < 3) {
            if (age == 15) {
                height++;
                age = 0;
            } else {
                age++;
            }
        } else {
            reset();
        }
    }

    public int harvest() {
        if (height > 1) {
            age = 0;
            return height-1;
        }
        return 0;
    }

    public void reset() {
        this.height = 1;
        this.age = 0;
    }

    public TotallyBlock copy() {
        return new TotallySugarCaneBlock(this.height, this.age);
    }
}

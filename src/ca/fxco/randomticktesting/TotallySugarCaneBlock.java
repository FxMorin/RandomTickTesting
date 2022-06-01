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

    public void randomTick(short height) {
        if (height == -1 || this.height == height) {
            if (this.height < 3) {
                if (this.age == 15) {
                    this.height++;
                    this.age = 0;
                } else {
                    this.age++;
                }
            }
        }
    }

    public int harvest() {
        if (height > 1) {
            age = 0;
            int h = height-1;
            this.height = 1;
            return h;
        }
        return 0;
    }

    public TotallyBlock copy() {
        return new TotallySugarCaneBlock(this.height, this.age);
    }
}

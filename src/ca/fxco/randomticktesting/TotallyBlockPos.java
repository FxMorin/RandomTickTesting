package ca.fxco.randomticktesting;

public class TotallyBlockPos {

    private final int linearPos;
    private final short y;

    public TotallyBlockPos(int linearPos, short y) {
        this.linearPos = linearPos;
        this.y = y;
    }

    public int getLinearPos() {
        return this.linearPos;
    }

    public short getY() {
        return this.y;
    }
}

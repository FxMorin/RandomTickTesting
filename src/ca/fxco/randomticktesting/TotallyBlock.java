package ca.fxco.randomticktesting;

public interface TotallyBlock {

    void randomTick(short height);
    int harvest();

    TotallyBlock copy();
}

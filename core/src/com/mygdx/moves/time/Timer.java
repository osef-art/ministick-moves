package com.mygdx.moves.time;

public class Timer {
    private final com.mygdx.moves.time.Chrono chrono;
    private double limit;

    public Timer(double limit) {
        chrono = new com.mygdx.moves.time.Chrono();
        this.limit = limit;
    }

    public boolean isExceeded() {
        return chrono.exceeds(limit);
    }

    public boolean resetIfExceeds() {
        if (isExceeded()) {
            chrono.reset();
            return true;
        }
        return false;
    }

    public void reset() {
        chrono.reset();
    }

    public void updateLimit(int newLimit) {
        limit = newLimit;
    }

    public double ratio() {
        return chrono.value() / limit;
    }

}

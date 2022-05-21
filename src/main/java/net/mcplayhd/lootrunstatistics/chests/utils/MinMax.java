package net.mcplayhd.lootrunstatistics.chests.utils;

public class MinMax {

    private final int min;
    private final int max;

    public MinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}

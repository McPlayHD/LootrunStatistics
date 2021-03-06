package net.mcplayhd.lootrunstatistics.chests.utils;

public class MinMax implements Cloneable {
    private int min;
    private int max;

    public MinMax() {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
    }

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

    public int getDifference() {
        return max - min;
    }

    public boolean isEmpty() {
        return min == Integer.MAX_VALUE && max == Integer.MIN_VALUE;
    }

    public void updateMin(int potentialMin) {
        min = Math.min(min, potentialMin);
    }

    public void updateMax(int potentialMax) {
        max = Math.max(max, potentialMax);
    }

    public boolean isInRange(int number) {
        return min <= number && max >= number;
    }

    @Override
    public MinMax clone() {
        try {
            MinMax clone = (MinMax) super.clone();
            clone.min = min;
            clone.max = max;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

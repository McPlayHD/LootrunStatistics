package net.mcplayhd.lootrunstatistics.utils;

import java.util.Objects;

public class Loc {
    private int x, y, z;

    public Loc(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public double dist(Loc loc) {
        return Math.sqrt(distSquared(loc));
    }

    public int distSquared(Loc loc) {
        int dx = loc.x - x;
        int dy = loc.y - y;
        int dz = loc.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loc loc = (Loc) o;
        return Double.compare(loc.x, x) == 0 &&
                Double.compare(loc.y, y) == 0 &&
                Double.compare(loc.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

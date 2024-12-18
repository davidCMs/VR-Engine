package org.davidCMs.engine.utils;

public class LogList {

    private final int size;
    private final double[] values;
    int index = 0;
    private boolean saturated = false;

    public LogList(int size) {
        this.size = size;
        values = new double[this.size];
    }

    public synchronized void add(double d) {
        values[index] = d;
        if (index == size-1)
            index = 0;
        else index++;
    }

    public int size() {
        return size;
    }

    public int getCurrentIndex() {
        return index;
    }

    public double get(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(index);
        else return values[index];
    }

    public double getAverage() {
        double sum = 0;
        for (double d : values)
            sum += d;
        return sum/size();
    }

    public double[] getArray() {
        return values;
    }
}

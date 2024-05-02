package com.example.riskslab2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public record RangeProperty(DoubleProperty left, DoubleProperty right) {

    public static RangeProperty createDoubleRange(double defaultLeft, double defaultRight) {
        return new RangeProperty(new SimpleDoubleProperty(defaultLeft), new SimpleDoubleProperty(defaultRight));
    }

    public double getLeftValue() {
        return left.getValue();
    }

    public double getRightValue() {
        return right.getValue();
    }

    public double getRandomBetween() {
        return RandomUtils.getRandomNumberInRange(left.get(), right.get());
    }

    public Pair<Double, Double> getValuesAtIteration(int i, int numIterations) {
        double avg = (getLeftValue() + getRightValue()) / 2D;
        double step = (getRightValue() - avg) / Math.max(1, numIterations);

        return new Pair<>(avg - i * step, avg + i * step);
    }

}

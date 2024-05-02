package com.example.riskslab2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

public record ResultRow(
        IntegerProperty iteration,
        DoubleProperty l,
        DoubleProperty A,
        DoubleProperty R,
        DoubleProperty B,
        DoubleProperty C,
        DoubleProperty C1,
        DoubleProperty C2,
        DoubleProperty C3,
        DoubleProperty C4,
        DoubleProperty C5,
        DoubleProperty Alpha,
        DoubleProperty Q,
        DoubleProperty S1,
        DoubleProperty S2,
        DoubleProperty S3,
        DoubleProperty C_NPV
) {
}

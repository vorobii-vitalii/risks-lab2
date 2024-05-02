package com.example.riskslab2;

import javafx.beans.property.DoubleProperty;

public record NPVCalculationRow(
        DoubleProperty alpha,
        RangeProperty r,
        RangeProperty b,
        RangeProperty ac,
        RangeProperty c1,
        RangeProperty c2,
        RangeProperty c3,
        RangeProperty npv
) {
}

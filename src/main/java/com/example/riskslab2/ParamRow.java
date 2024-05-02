package com.example.riskslab2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

public record ParamRow(
        StringProperty name,
        DoubleProperty value
) {
}

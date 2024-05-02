package com.example.riskslab2;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private IntegerProperty n = new SimpleIntegerProperty(22);
    private RangeProperty A = RangeProperty.createDoubleRange(0.92, 1.2);
    private RangeProperty R = RangeProperty.createDoubleRange(0.11, 0.14);
    private RangeProperty B = RangeProperty.createDoubleRange(24.1, 31.3);
    private RangeProperty C = RangeProperty.createDoubleRange(2.7, 3.5);
    private RangeProperty L = RangeProperty.createDoubleRange(3, 5);
    private RangeProperty C1 = RangeProperty.createDoubleRange(6.7, 8.7);
    private RangeProperty C2 = RangeProperty.createDoubleRange(5.5, 7.2);
    private RangeProperty C3 = RangeProperty.createDoubleRange(3.9, 5.1);
    private RangeProperty C4 = RangeProperty.createDoubleRange(4.1, 5.3);
    private RangeProperty C5 = RangeProperty.createDoubleRange(2.7, 3.5);
    private RangeProperty ALPHA = RangeProperty.createDoubleRange(0.65, 0.85);
    private RangeProperty Q = RangeProperty.createDoubleRange(0.94, 1.22);


    private final Form formInstance;

    public Model() {
        List<Group> groups = new ArrayList<>();
        groups.add(Group.of(
                Field.ofIntegerType(n)
                        .label("N")
                        .placeholder("N")
                        .required("Введість N")
                        .validate(IntegerRangeValidator.atLeast(1, "Має бути додатнім"))
        ));
        addRangeFields(groups, A, "A");
        addRangeFields(groups, R, "Ri");
        addRangeFields(groups, B, "B");
        addRangeFields(groups, C, "C");
        addRangeFields(groups, L, "l");
        addRangeFields(groups, C1, "C1");
        addRangeFields(groups, C2, "C2");
        addRangeFields(groups, C3, "C3");
        addRangeFields(groups, C4, "C4");
        addRangeFields(groups, C5, "C5");
        addRangeFields(groups, ALPHA, "Alpha");
        addRangeFields(groups, Q, "Q");
        formInstance = Form.of(groups.toArray(Group[]::new));
    }

    private void addRangeFields(
            List<Group> formElements,
            RangeProperty rangeProperty,
            String propertyName
    ) {
        formElements.add(Group.of(
                Field.ofDoubleType(rangeProperty.left())
                        .label(propertyName + " min")
                        .placeholder(propertyName + " min")
                        .required("Введість " + propertyName + " min"),
                Field.ofDoubleType(rangeProperty.right())
                        .label(propertyName + " max")
                        .placeholder(propertyName + " max")
                        .required("Введість " + propertyName + " max")
        ));
    }


    public Form getFormInstance() {
        return formInstance;
    }

    public IntegerProperty nProperty() {
        return n;
    }

    public RangeProperty getA() {
        return A;
    }

    public RangeProperty getR() {
        return R;
    }

    public RangeProperty getB() {
        return B;
    }

    public RangeProperty getC() {
        return C;
    }

    public RangeProperty getL() {
        return L;
    }

    public RangeProperty getC1() {
        return C1;
    }

    public RangeProperty getC2() {
        return C2;
    }

    public RangeProperty getC3() {
        return C3;
    }

    public RangeProperty getC4() {
        return C4;
    }

    public RangeProperty getC5() {
        return C5;
    }

    public RangeProperty getALPHA() {
        return ALPHA;
    }

    public RangeProperty getQ() {
        return Q;
    }
}

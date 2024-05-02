package com.example.riskslab2;

import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.JFXChartUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class RootPane extends VBox implements ViewMixin {
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.CANADA);
    public static final int N = 10;
    private ScrollPane scrollContent;
    private final Model model;
    private FormRenderer displayForm;
    private Button calculateButton;
    private TableView<ResultRow> resultsTable;
    private TableView<NPVCalculationRow> npvTable;
    private TableView<ParamRow> parametersTable;
    private LineChart<Number, Number> scatterChart;

    /**
     * The constructor to create the nodes and regions.
     *
     * @param model The model that holds the data.
     */
    public RootPane(Model model) {
        this.model = model;
        init();
    }

    /**
     * This method initializes all nodes and regions.
     */
    @Override
    public void initializeParts() {
        calculateButton = new Button("Обрахувати");
        calculateButton.setPadding(new Insets(10));
        resultsTable = new TableView<>();
        resultsTable.setEditable(false);
        resultsTable.getColumns().addAll(
                List.of(
                        createColumn("Ітерація", ResultRow::iteration),
                        createColumn("l", formatted(ResultRow::l)),
                        createColumn("A", formatted(ResultRow::A)),
                        createColumn("R", formatted(ResultRow::R)),
                        createColumn("B", formatted(ResultRow::B)),
                        createColumn("C", formatted(ResultRow::C)),
                        createColumn("C1", formatted(ResultRow::C1)),
                        createColumn("C2", formatted(ResultRow::C2)),
                        createColumn("C3", formatted(ResultRow::C3)),
                        createColumn("C4", formatted(ResultRow::C4)),
                        createColumn("C5", formatted(ResultRow::C5)),
                        createColumn("Alpha", formatted(ResultRow::Alpha)),
                        createColumn("Q", formatted(ResultRow::Q)),
                        createColumn("S1", formatted(ResultRow::S1)),
                        createColumn("S2", formatted(ResultRow::S2)),
                        createColumn("S3", formatted(ResultRow::S3)),
                        createColumn("C NPV", formatted(ResultRow::C_NPV))
                ));
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        resultsTable.setVisible(false);

        npvTable = new TableView<>();
        npvTable.setEditable(false);
        npvTable.getColumns().addAll(
                List.of(
                        createColumn("α", formatted(NPVCalculationRow::alpha)),
                        createColumn("r", formattedRange(NPVCalculationRow::r)),
                        createColumn("B", formattedRange(NPVCalculationRow::b)),
                        createColumn("AC", formattedRange(NPVCalculationRow::ac)),
                        createColumn("C1", formattedRange(NPVCalculationRow::c1)),
                        createColumn("C2", formattedRange(NPVCalculationRow::c2)),
                        createColumn("C3", formattedRange(NPVCalculationRow::c3)),
                        createColumn("NPV", formattedRange(NPVCalculationRow::npv))
                ));
        npvTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        npvTable.setVisible(false);

        parametersTable = new TableView<>();
        parametersTable.setEditable(false);
        parametersTable.getColumns().addAll(
                List.of(
                        createColumn("Параметр", ParamRow::name),
                        createColumn("Значення параметру", formatted(ParamRow::value))
                ));
        parametersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        parametersTable.setVisible(false);

        var xAxis = new NumberAxis(-1, 80, 0.2);
        xAxis.setLabel("NPV");

        var yAxis = new NumberAxis(-1.1, 1.1, 0.01);
        yAxis.setLabel("α");

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        scatterChart = new LineChart<>(xAxis, yAxis);
        scatterChart.setVisible(false);
        scatterChart.setMinHeight(800D);

        scrollContent = new ScrollPane();
//        Platform.runLater(() -> {
//            ScrollBar verticalScrollbar = (ScrollBar) scrollContent.lookup(".scroll-bar:vertical");
//            double defaultUnitIncrement = verticalScrollbar.getUnitIncrement();
//            verticalScrollbar.setUnitIncrement(defaultUnitIncrement * 3);
//        });
        displayForm = new FormRenderer(model.getFormInstance());

        //Panning works via either secondary (right) mouse or primary with ctrl held down
        ChartPanManager panner = new ChartPanManager(scatterChart);
        panner.setMouseFilter(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY ||
                    (mouseEvent.getButton() == MouseButton.PRIMARY &&
                            mouseEvent.isShortcutDown())) {
            } else {
                mouseEvent.consume();
            }
        });
        panner.start();

        //Zooming works only via primary mouse button without ctrl held down
        JFXChartUtil.setupZooming(scatterChart, mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.PRIMARY ||
                    mouseEvent.isShortcutDown())
                mouseEvent.consume();
        });

        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(scatterChart);
    }

    private Pair<Double, Double> calculateCnvpMinAndMaxAfterNIterations() {
        int n = model.nProperty().get();
        double s1Min = 0;
        double s2Min = 0;
        double s3Min = 0;
        double s1Max = 0;
        double s2Max = 0;
        double s3Max = 0;
        double cNvpMin = 0D;
        double cNvpMax = 0D;
        for (var i = 0; i < n; i++) {
            var a = model.getA().getValuesAtIteration(i, n);
            var l = model.getL().getValuesAtIteration(i, n);
            var r = model.getR().getValuesAtIteration(i, n);
            var b = model.getB().getValuesAtIteration(i, n);
            var c = model.getC().getValuesAtIteration(i, n);
            var alpha = model.getALPHA().getValuesAtIteration(i, n);
            var q = model.getQ().getValuesAtIteration(i, n);
            var cArray = getCArray(i, n);

            var mMin = Math.pow(1 + r.first(), i + 1);
            s1Min += (b.first() - a.first() * c.first()) / mMin;
            s2Min += sumWithIndexLower(cArray[0], l.first().intValue(), v -> v) / mMin;
            s3Min += sumWithIndexLower(cArray[0], l.first().intValue(), v -> Math.pow(Math.E, -alpha.first() * q.first() * v)) / mMin;

            var mMax = Math.pow(1 + r.second(), i + 1);
            s1Max += (b.second() - a.second() * c.second()) / mMax;
            s2Max += sumWithIndexLower(cArray[1], l.second().intValue(), v -> v) / mMax;
            s3Max += sumWithIndexLower(cArray[1], l.second().intValue(), v -> Math.pow(Math.E, -alpha.second() * q.second() * v)) / mMax;

            cNvpMin = s1Min - s2Min - s3Min;
            cNvpMax = s1Max - s2Max - s3Max;
        }
        return new Pair<>(cNvpMax, cNvpMin);
    }

    private <S> Function<S, ObservableValue<String>> formattedRange(
            Function<S, RangeProperty> function
    ) {
        return v -> {
            var rangeProperty = function.apply(v);
            var stringProperty = new SimpleStringProperty(
                    NUMBER_FORMAT.format(rangeProperty.getLeftValue()) + " - " + NUMBER_FORMAT.format(rangeProperty.getRightValue())
            );
            rangeProperty.left().addListener((observable, oldValue, newValue) ->
                    stringProperty.setValue(NUMBER_FORMAT.format(rangeProperty.getLeftValue()) + " - " + NUMBER_FORMAT.format(rangeProperty.getRightValue())));
            rangeProperty.right().addListener((observable, oldValue, newValue) ->
                    stringProperty.setValue(NUMBER_FORMAT.format(rangeProperty.getLeftValue()) + " - " + NUMBER_FORMAT.format(rangeProperty.getRightValue())));
            return stringProperty;
        };
    }

    private <S, T extends Number> Function<S, ObservableValue<String>> formatted(
            Function<S, ObservableValue<T>> function
    ) {
        return v -> {
            var observableValue = function.apply(v);
            var stringProperty = new SimpleStringProperty(NUMBER_FORMAT.format(observableValue.getValue()));
            observableValue.addListener((observable, oldValue, newValue) ->
                    stringProperty.setValue(NUMBER_FORMAT.format(newValue)));
            return stringProperty;
        };
    }

    private <S, T> TableColumn<S, T> createColumn(String headerName, Function<S, ObservableValue<T>> function) {
        TableColumn<S, T> tableColumn = new TableColumn<>(headerName);
        tableColumn.setCellValueFactory(param -> function.apply(param.getValue()));
        return tableColumn;
    }

    /**
     * This method sets up the necessary bindings for the logic of the
     * application.
     */
    @Override
    public void setupBindings() {
        displayForm.prefWidthProperty().bind(scrollContent.prefWidthProperty());
    }

    /**
     * This method sets up listeners and sets the text of the state change
     * labels.
     */
    @Override
    public void setupValueChangedListeners() {
        model.getFormInstance().persistableProperty().addListener((observableValue, aBoolean, newValue) -> calculateButton.setDisable(!Boolean.TRUE.equals(newValue)));
    }

    /**
     * This method sets up the handling for all the button clicks.
     */
    @Override
    public void setupEventHandlers() {
        calculateButton.setOnAction(event -> {
            System.out.println("calculating...");
            model.getFormInstance().persist();
            recalculateIterationResultTable();
            calculateFuzzyNPVGraph();
            resultsTable.setVisible(true);
            scatterChart.setVisible(true);
            npvTable.setVisible(true);
            parametersTable.setVisible(true);
        });
    }

    private void calculateFuzzyNPVGraph() {
        XYChart.Series<Number, Number> minSeries = new XYChart.Series<>();
        minSeries.setName("min");
        XYChart.Series<Number, Number> maxSeries = new XYChart.Series<>();
        maxSeries.setName("max");

        scatterChart.getData().clear();
        npvTable.getItems().clear();

        for (int i = 0; i <= N; i++) {
            var a = model.getA().getValuesAtIteration(i, N);
            var r = model.getR().getValuesAtIteration(i, N);
            var b = model.getB().getValuesAtIteration(i, N);
            var c = model.getC().getValuesAtIteration(i, N);
            var c1 = model.getC1().getValuesAtIteration(i, N);
            var c2 = model.getC2().getValuesAtIteration(i, N);
            var c3 = model.getC3().getValuesAtIteration(i, N);

            double npvMin = calcNpv(r.first(), b.first(), a.first(), c.first(), c1.first(), c2.first(), c3.first());
            double npvMax = calcNpv(r.second(), b.second(), a.second(), c.second(), c1.second(), c2.second(), c3.second());
            double alpha = 1 - 0.1 * i;
            minSeries.getData().add(new XYChart.Data<>(npvMin, alpha));
            maxSeries.getData().add(new XYChart.Data<>(npvMax, alpha));

            this.npvTable.getItems().add(new NPVCalculationRow(
                    new SimpleDoubleProperty(alpha),
                    RangeProperty.createDoubleRange(r.first(), r.second()),
                    RangeProperty.createDoubleRange(b.first(), b.second()),
                    RangeProperty.createDoubleRange(a.first() * c.first(), a.second() * c.second()),
                    RangeProperty.createDoubleRange(c1.first(), c1.second()),
                    RangeProperty.createDoubleRange(c2.first(), c2.second()),
                    RangeProperty.createDoubleRange(c3.first(), c3.second()),
                    RangeProperty.createDoubleRange(npvMin, npvMax)
            ));
        }
        scatterChart.getData().add(minSeries);
        scatterChart.getData().add(maxSeries);

    }

    private void recalculateIterationResultTable() {
        int n = model.nProperty().get();
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;
        resultsTable.getItems().clear();
        double lastCnvp = 0;
        for (var i = 0; i < n; i++) {
            var a = model.getA().getRandomBetween();
            var l = model.getL().getRandomBetween();
            var r = model.getR().getRandomBetween();
            var b = model.getB().getRandomBetween();
            var c = model.getC().getRandomBetween();
            var alpha = model.getALPHA().getRandomBetween();
            var q = model.getQ().getRandomBetween();
            var cArray = getCArray();

            var m = Math.pow(1 + r, i + 1);
            s1 += (b - a * c) / m;
            s2 += sumWithIndexLower(cArray, (int) l, v -> v) / m;
            s3 += sumWithIndexLower(cArray, (int) l, v -> Math.pow(Math.E, -alpha * q * v)) / m;

            double cNvp = s1 - s2 - s3;
            resultsTable.getItems().add(new ResultRow(
                    new SimpleIntegerProperty(i + 1),
                    new SimpleDoubleProperty(l),
                    new SimpleDoubleProperty(a),
                    new SimpleDoubleProperty(r),
                    new SimpleDoubleProperty(b),
                    new SimpleDoubleProperty(c),
                    new SimpleDoubleProperty(cArray[0]),
                    new SimpleDoubleProperty(cArray[1]),
                    new SimpleDoubleProperty(cArray[2]),
                    new SimpleDoubleProperty(cArray[3]),
                    new SimpleDoubleProperty(cArray[4]),
                    new SimpleDoubleProperty(alpha),
                    new SimpleDoubleProperty(q),
                    new SimpleDoubleProperty(s1),
                    new SimpleDoubleProperty(s2),
                    new SimpleDoubleProperty(s3),
                    new SimpleDoubleProperty(cNvp)
            ));
            lastCnvp = cNvp;
        }

        var minMaxCnvp = calculateCnvpMinAndMaxAfterNIterations();

        var cNVPMin = minMaxCnvp.first();
        var cNVPMax = minMaxCnvp.second();
        var cNVPAvg = (cNVPMin + cNVPMax) / 2.0;

        var alpha = calculateAlpha(lastCnvp, cNVPMin, cNVPMax, cNVPAvg);
        var R = calculateR(lastCnvp, cNVPMin, cNVPMax);

        var RNVP = CalcRNVP(lastCnvp, cNVPMin, cNVPMax, cNVPAvg, R, alpha);
        var RL = CalcRL(lastCnvp, cNVPMin, cNVPMax, cNVPAvg);

        parametersTable.getItems().clear();
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("Cnvp"), new SimpleDoubleProperty(lastCnvp)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("Мінімальне Cnvp"), new SimpleDoubleProperty(cNVPMin)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("Максимальне Cnvp"), new SimpleDoubleProperty(cNVPMax)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("Середнє Cnvp"), new SimpleDoubleProperty(cNVPAvg)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("α"), new SimpleDoubleProperty(alpha)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("R"), new SimpleDoubleProperty(R)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("RL"), new SimpleDoubleProperty(RL)
        ));
        parametersTable.getItems().add(new ParamRow(
                new SimpleStringProperty("Rnvp"), new SimpleDoubleProperty(RNVP)
        ));
    }

    private double sumWithIndexLower(double[] arr, int maxIndex, Function<Double, Double> func) {
        double sum = 0D;
        for (int i = 0; i < arr.length; i++) {
            if (i > maxIndex) {
                break;
            }
            sum += func.apply(arr[i]);
        }
        return sum;
    }

    private double calcNpv(double r, double b, double a, double c, double c1, double c2, double c3) {
        return (b - a * c) / (1 + r) - (c1 + c2 + c3) / (1 + r);
    }

    private static double CalcRL(double cNVP, double cNVPMin, double cNVPMax, double cNVPAvg) {
        if (cNVP < cNVPAvg) {
            return cNVPMin * cNVPMin / ((cNVPAvg - cNVPMin) * (cNVPMax - cNVPMin));
        }
        return 1 - cNVPMax * cNVPMax / ((cNVPMax - cNVPAvg) * (cNVPMax - cNVPMin));
    }

    private static double calculateR(double cNVP, double cNVPMin, double cNVPMax) {
        if (cNVP < cNVPMax) {
            return (cNVP - cNVPMin) / (cNVPMax - cNVPMin);
        }
        return 1;
    }

    private static double CalcRNVP(
            double cNVP,
            double cNVPMin,
            double cNVPMax,
            double cNVPAvg,
            double r,
            double alpha
    ) {
        double RNVP;

        if (cNVP < cNVPMin) {
            RNVP = 0;
        } else {
            double v = (1 - alpha) / alpha * Math.log(1 - alpha);
            if (cNVP < cNVPAvg) {
                RNVP = r * (1 + v);
            } else if (cNVP <= cNVPMax) {
                RNVP = 1 - (1 - r) * (1 + v);
            } else {
                RNVP = 1;
            }
        }
        return RNVP;
    }

    private static double calculateAlpha(double cNVP, double cNVPMin, double cNVPMax, double cNVPAvg) {
        double alpha;
        if (cNVP < cNVPMin) {
            alpha = 0;
        } else if (cNVP < cNVPAvg) {
            alpha = (cNVP - cNVPMin) / (cNVPAvg - cNVPMin);
        } else if (cNVP <= cNVPMax) {
            alpha = (cNVPMax - cNVP) / (cNVPMax - cNVPAvg);
        } else {
            alpha = 1;
        }
        return alpha;
    }

    private double[] getCArray() {
        return new double[]{
                model.getC1().getRandomBetween(),
                model.getC2().getRandomBetween(),
                model.getC3().getRandomBetween(),
                model.getC4().getRandomBetween(),
                model.getC5().getRandomBetween()
        };
    }

    private double[][] getCArray(int iteration, int numIterations) {
        var pairs = List.of(
                model.getC1().getValuesAtIteration(iteration, numIterations),
                model.getC2().getValuesAtIteration(iteration, numIterations),
                model.getC3().getValuesAtIteration(iteration, numIterations),
                model.getC4().getValuesAtIteration(iteration, numIterations),
                model.getC5().getValuesAtIteration(iteration, numIterations)
        );
        var res = new double[2][pairs.size()];
        for (var i = 0; i < pairs.size(); i++) {
            res[0][i] = pairs.get(i).first();
            res[1][i] = pairs.get(i).second();
        }
        return res;
    }

    @Override
    public void layoutParts() {
        var vBox = new VBox();
        vBox.getChildren().add(displayForm);
        vBox.getChildren().add(calculateButton);
        vBox.getChildren().add(resultsTable);
        GridPane npvBox = new GridPane();
        vBox.getChildren().add(parametersTable);
        vBox.getChildren().add(scatterChart);
        vBox.getChildren().add(npvTable);
        npvBox.setMaxWidth(Double.MAX_VALUE);
        scrollContent.setContent(vBox);
        scrollContent.setFitToWidth(true);
        getChildren().add(scrollContent);
    }

}

package javafxmlapplication.CompararMeses;

import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafxmlapplication.PantallaDeInicioController;
import model.Acount;
import model.AcountDAOException;
import model.Charge;

public class CompararMesesController implements Initializable {

    @FXML
    private Button return_pantalla_principal;
    @FXML
    private ComboBox<String> monthComboBox1;
    @FXML
    private ComboBox<Integer> yearComboBox1;
    @FXML
    private ComboBox<String> monthComboBox2;
    @FXML
    private ComboBox<Integer> yearComboBox2;
    @FXML
    private Button compare_button;
    @FXML
    private TableView<ChargeSummary> table_view_resumen;
    @FXML
    private TableColumn<ChargeSummary, String> categoria_column;
    @FXML
    private TableColumn<ChargeSummary, Double> gasto_column;
    @FXML
    private BarChart<String, Number> barChart;

    private Stage stage;
    private Stage loginStage;
    private Map<String, String> colorMap;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureComboBoxes();
        return_pantalla_principal.setOnAction(this::handleReturnPantallaPrincipal);
        compare_button.setOnAction(event -> {
            try {
                handleCompareButton(event);
            } catch (AcountDAOException | IOException ex) {
                Logger.getLogger(CompararMesesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        compare_button.setDisable(true);

        colorMap = new HashMap<>();
        colorMap.put("Month 1", "#FF6E0D");
        colorMap.put("Month 2", "darkgray");

        categoria_column.setCellValueFactory(new PropertyValueFactory<>("category"));
        gasto_column.setCellValueFactory(new PropertyValueFactory<>("cost"));

        Callback<TableColumn<ChargeSummary, String>, TableCell<ChargeSummary, String>> cellFactory = column -> new TableCell<ChargeSummary, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    ChargeSummary chargeSummary = getTableView().getItems().get(getIndex());
                    String color = colorMap.get(chargeSummary.getMonth());
                    if (color != null) {
                        setStyle("-fx-background-color: " + color);
                    }
                }
            }
        };

        categoria_column.setCellFactory(cellFactory);
        gasto_column.setCellFactory(column -> new TableCell<ChargeSummary, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    ChargeSummary chargeSummary = getTableView().getItems().get(getIndex());
                    String color = colorMap.get(chargeSummary.getMonth());
                    if (color != null) {
                        setStyle("-fx-background-color: " + color);
                    }
                }
            }
        });
    }

    private void configureComboBoxes() {
        ObservableList<String> months = FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        monthComboBox1.setItems(months);
        monthComboBox2.setItems(months);

        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int year = 2000; year <= 2030; years.add(year++));

        yearComboBox1.setItems(years);
        yearComboBox2.setItems(years);

        yearComboBox1.setValue(2024);
        yearComboBox2.setValue(2024);

        ChangeListener<Object> changeListener = (obs, oldValue, newValue) -> {
            compare_button.setDisable(
                monthComboBox1.getValue() == null || yearComboBox1.getValue() == null ||
                monthComboBox2.getValue() == null || yearComboBox2.getValue() == null
            );
        };

        monthComboBox1.valueProperty().addListener(changeListener);
        yearComboBox1.valueProperty().addListener(changeListener);
        monthComboBox2.valueProperty().addListener(changeListener);
        yearComboBox2.valueProperty().addListener(changeListener);
    }

    private void handleReturnPantallaPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/PantallaDeInicio.fxml"));
            Parent newSceneParent = loader.load();

            PantallaDeInicioController controller = loader.getController();
            controller.setStage(stage);
            controller.setStageLogin(loginStage); // Asegurarse de pasar loginStage al nuevo controlador

            Scene newScene = new Scene(newSceneParent);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(CompararMesesController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void handleCompareButton(ActionEvent event) throws AcountDAOException, IOException {
        YearMonth month1 = YearMonth.of(yearComboBox1.getValue(), monthComboBox1.getSelectionModel().getSelectedIndex() + 1);
        YearMonth month2 = YearMonth.of(yearComboBox2.getValue(), monthComboBox2.getSelectionModel().getSelectedIndex() + 1);

        List<Charge> charges1 = Acount.getInstance().getUserCharges().stream()
                .filter(charge -> YearMonth.from(charge.getDate()).equals(month1))
                .collect(Collectors.toList());
        List<Charge> charges2 = Acount.getInstance().getUserCharges().stream()
                .filter(charge -> YearMonth.from(charge.getDate()).equals(month2))
                .collect(Collectors.toList());

        ObservableList<ChargeSummary> tableData = FXCollections.observableArrayList();

        charges1.forEach((charge) -> {
            tableData.add(new ChargeSummary(charge.getCategory().getName(), charge.getCost(), "Month 1"));
        });
        charges2.forEach((charge) -> {
            tableData.add(new ChargeSummary(charge.getCategory().getName(), charge.getCost(), "Month 2"));
        });

        table_view_resumen.setItems(tableData);

        updateBarChart(charges1, charges2);

        double total1 = charges1.stream().mapToDouble(Charge::getCost).sum();
        double total2 = charges2.stream().mapToDouble(Charge::getCost).sum();

        String resumen;
        if (total1 > total2) {
            resumen = String.format("Has gastado más en el primer mes seleccionado: %.2f€ frente a %.2f€ en el segundo mes.", total1, total2);
        } else if (total2 > total1) {
            resumen = String.format("Has gastado más en el segundo mes seleccionado: %.2f€ frente a %.2f€ en el primer mes.", total2, total1);
        } else {
            resumen = String.format("Has gastado lo mismo en ambos meses: %.2f€.", total1);
        }
    }

    private void updateBarChart(List<Charge> charges1, List<Charge> charges2) {
        if (barChart == null) {
            Logger.getLogger(CompararMesesController.class.getName()).log(Level.SEVERE, "BarChart is not initialized.");
            return;
        }

        barChart.getData().clear();

        Map<String, Double> categoryTotals1 = charges1.stream()
                .collect(Collectors.groupingBy(charge -> charge.getCategory().getName(),
                        Collectors.summingDouble(Charge::getCost)));
        Map<String, Double> categoryTotals2 = charges2.stream()
                .collect(Collectors.groupingBy(charge -> charge.getCategory().getName(),
                        Collectors.summingDouble(Charge::getCost)));

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Month 1");
        for (Map.Entry<String, Double> entry : categoryTotals1.entrySet()) {
            series1.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Month 2");
        for (Map.Entry<String, Double> entry : categoryTotals2.entrySet()) {
            series2.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().addAll(series1, series2);

        for (XYChart.Data<String, Number> data : series1.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #FF6E0D;");
        }
        for (XYChart.Data<String, Number> data : series2.getData()) {
            data.getNode().setStyle("-fx-bar-fill: darkgray;");
        }
    }

    public static class ChargeSummary {
        private final String category;
        private final double cost;
        private final String month;

        public ChargeSummary(String category, double cost, String month) {
            this.category = category;
            this.cost = cost;
            this.month = month;
        }

        public String getCategory() {
            return category;
        }

        public double getCost() {
            return cost;
        }

        public String getMonth() {
            return month;
        }
    }
}

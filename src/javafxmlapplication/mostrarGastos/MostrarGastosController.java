package javafxmlapplication.mostrarGastos;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxmlapplication.CompararMeses.CompararMesesController;
import javafxmlapplication.PantallaDeInicioController;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;
import model.User;

public class MostrarGastosController implements Initializable {

    @FXML
    private Button volver_inicio_buton;
    @FXML
    private Button imprimir_buton;
    @FXML
    private Button volver_buton;
    @FXML
    private Label usuario;
    @FXML
    private Label nombre_apellidos_label;
    @FXML
    private Label fecha_label;
    @FXML
    private TableView<Charge> tableview;
    @FXML
    private TableColumn<Charge, String> NombreGasto_column;
    @FXML
    private TableColumn<Charge, Category> categoria_column;
    @FXML
    private TableColumn<Charge, LocalDate> fecha_column;
    @FXML
    private TableColumn<Charge, Double> coste_column;
    @FXML
    private TableColumn<Charge, Integer> unidades_column;
    @FXML
    private Label gasto_total;
    @FXML
    private PieChart pieChart;

    private ObservableList<Charge> chargesList;
    private Stage stage;
    private Stage loginStage;
    @FXML
    private ScrollPane scroll_pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        volver_inicio_buton.setOnAction(event -> handleReturnPantallaPrincipal(event));
        try {
            User user = Acount.getInstance().getLoggedUser();
            usuario.setText(user.getNickName());
            nombre_apellidos_label.setText(user.getName() + " " + user.getSurname());

            LocalDate now = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(now);
            fecha_label.setText(currentMonth.toString());

            chargesList = FXCollections.observableArrayList(getChargesForCurrentMonth(currentMonth));
            setupTableView();
            setupPieChart();
            calculateTotalCost();
        } catch (AcountDAOException | IOException ex) {
        }
    }

    public void setStage(Stage stage, double width, double height) {
        this.stage = stage;
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    private void setupTableView() {
        tableview.setItems(chargesList);
        NombreGasto_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoria_column.setCellValueFactory(new PropertyValueFactory<>("category"));
        fecha_column.setCellValueFactory(new PropertyValueFactory<>("date"));
        coste_column.setCellValueFactory(new PropertyValueFactory<>("cost"));
        unidades_column.setCellValueFactory(new PropertyValueFactory<>("units"));
    }

    private void setupPieChart() {
        pieChart.getData().clear();
        chargesList.stream()
                .collect(Collectors.groupingBy(Charge::getCategory, Collectors.summingDouble(c -> c.getCost() * c.getUnits())))
                .forEach((category, totalCost) -> pieChart.getData().add(new PieChart.Data(category.getName(), totalCost)));
    }

    private List<Charge> getChargesForCurrentMonth(YearMonth currentMonth) throws AcountDAOException, IOException {
        return Acount.getInstance().getUserCharges().stream()
                .filter(charge -> YearMonth.from(charge.getDate()).equals(currentMonth))
                .collect(Collectors.toList());
    }

    private void calculateTotalCost() {
        double totalCost = chargesList.stream()
                .mapToDouble(charge -> charge.getCost() * charge.getUnits())
                .sum();
        gasto_total.setText(String.format("%.2f", totalCost));
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
}

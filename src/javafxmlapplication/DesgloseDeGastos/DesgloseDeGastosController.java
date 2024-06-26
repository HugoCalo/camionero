// DesgloseDeGastosController.java
package javafxmlapplication.DesgloseDeGastos;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafxmlapplication.CrearGasto.CrearGastoController;
import javafxmlapplication.PantallaDeInicioController;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;

public class DesgloseDeGastosController implements Initializable {

    private Stage stage;
    private Stage loginStage;
    private PantallaDeInicioController pantallaDeInicioController;

    @FXML
    private Button volverainicio_boton;
    @FXML
    private Tooltip volverainico_señal;
    @FXML
    private Button anadir_gasto_boton;
    @FXML
    private Button modificar_gasto_boton;
    @FXML
    private Button eliminar_gasto_boton;
    @FXML
    private ScrollPane pantalla_gastos;
    @FXML
    private TableView<Charge> tableview_gastos_cat;
    @FXML
    private TableColumn<Charge, String> nameColumn;
    @FXML
    private TableColumn<Charge, LocalDate> dateColumn;
    @FXML
    private TableColumn<Charge, Double> costColumn;
    @FXML
    private TableColumn<Charge, String> descriptionColumn;
    @FXML
    private TableColumn<Charge, Integer> unitsColumn;
    @FXML
    private TableColumn<Charge, ImageView> reciboColumn;

    private ObservableList<Charge> chargesList;
    private Category category;
    private Charge selectedCharge;
    @FXML
    private Label nombre_categoria_letra;
    @FXML
    private Label descripcion_categoria_letra;

    public void setStage(Stage stage, double width, double height) {
        this.stage = stage;
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void setPantallaDeInicioController(PantallaDeInicioController pantallaDeInicioController) {
        this.pantallaDeInicioController = pantallaDeInicioController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            category = Acount.getInstance().getUserCategories().get(0); // Assuming the first category is selected by default
            chargesList = FXCollections.observableArrayList(getChargesByCategory(category.getName()));
        } catch (AcountDAOException | IOException ex) {
            Logger.getLogger(DesgloseDeGastosController.class.getName()).log(Level.SEVERE, null, ex);
        }

        setupTableView();

        volverainicio_boton.setDisable(false);
        volverainicio_boton.setVisible(true);
        volverainicio_boton.setOnMouseEntered(event -> Tooltip.install(volverainicio_boton, volverainico_señal));
        volverainicio_boton.setOnMouseExited(event -> Tooltip.uninstall(volverainicio_boton, volverainico_señal));
        volverainicio_boton.setOnAction(event -> switchToPantallaPrincipal(event));

        tableview_gastos_cat.setRowFactory(tv -> {
            TableRow<Charge> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectedCharge = row.getItem();
                    tableview_gastos_cat.getSelectionModel().select(row.getIndex());
                    updateButtonsState(true);
                }
            });
            return row;
        });

        tableview_gastos_cat.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                updateButtonsState(false);
            } else {
                selectedCharge = newSelection;
                updateButtonsState(true);
            }
        });

        eliminar_gasto_boton.setOnAction(event -> {
            try {
                eliminarGasto(selectedCharge);
            } catch (IOException | AcountDAOException e) {
                Logger.getLogger(DesgloseDeGastosController.class.getName()).log(Level.SEVERE, null, e);
            }
        });

        anadir_gasto_boton.setOnAction(event -> anadirGasto());
        modificar_gasto_boton.setOnAction(event -> modificarGasto(selectedCharge));

        updateButtonsState(false);
    }

    private void setupTableView() {
        tableview_gastos_cat.setItems(chargesList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        reciboColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView(cellData.getValue().getImageScan());
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            return new ReadOnlyObjectWrapper<>(imageView);
        });
    }

    private void switchToPantallaPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/PantallaDeInicio.fxml"));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.show();
            PantallaDeInicioController controller = loader.getController();
            controller.setStage(currentStage);
            controller.setStageLogin(loginStage);
        } catch (IOException e) {
            Logger.getLogger(DesgloseDeGastosController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void setCategory(Category category) throws AcountDAOException, IOException {
        this.category = category;
        nombre_categoria_letra.setText(category.getName());
        descripcion_categoria_letra.setText(category.getDescription());
        updateChargesList();
    }

    private void updateChargesList() throws AcountDAOException, IOException {
        chargesList.setAll(getChargesByCategory(category.getName()));
    }

    private List<Charge> getChargesByCategory(String categoryName) throws AcountDAOException, IOException {
        return Acount.getInstance().getUserCharges().stream()
                .filter(charge -> charge.getCategory().getName().equals(categoryName))
                .collect(Collectors.toList());
    }

    private void anadirGasto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CrearGasto/CrearGasto.fxml"));
            Parent root = loader.load();
            CrearGastoController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Añadir Gasto");
            newStage.setScene(new Scene(root));
            controller.setStage(newStage);
            controller.setCategory(category);
            controller.setChargeList(chargesList);
            controller.setPantallaDeInicioController(pantallaDeInicioController); // Pasar el controlador
            newStage.show();
        } catch (IOException e) {
            Logger.getLogger(DesgloseDeGastosController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void modificarGasto(Charge charge) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CrearGasto/CrearGasto.fxml"));
            Parent root = loader.load();
            CrearGastoController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Modificar Gasto");
            newStage.setScene(new Scene(root));
            controller.setStage(newStage);
            controller.setCategory(category);
            controller.setChargeList(chargesList);
            controller.setPantallaDeInicioController(pantallaDeInicioController); // Pasar el controlador
            controller.loadChargeData(charge); // Pass the charge data to the controller
            newStage.show();
        } catch (IOException e) {
            Logger.getLogger(DesgloseDeGastosController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void eliminarGasto(Charge charge) throws AcountDAOException, IOException {
        if (charge != null) {
            boolean success = Acount.getInstance().removeCharge(charge);
            if (success) {
                chargesList.remove(charge);
                tableview_gastos_cat.getSelectionModel().clearSelection();
                tableview_gastos_cat.refresh();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Error al eliminar el gasto");
                errorAlert.setContentText("No se pudo eliminar el gasto. Inténtalo de nuevo.");
                errorAlert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Eliminar Gasto");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un gasto para eliminar.");
            alert.showAndWait();
        }
    }

    private void updateButtonsState(boolean enable) {
        modificar_gasto_boton.setDisable(!enable);
        eliminar_gasto_boton.setDisable(!enable);
    }

    public void refreshCategoryList() throws AcountDAOException, IOException {
        pantallaDeInicioController.refreshCategoryList();
    }
}

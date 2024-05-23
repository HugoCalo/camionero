package javafxmlapplication.CrearGasto;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;

public class CrearGastoController implements Initializable {

    @FXML
    private Button botonAceptar;
    @FXML
    private Button botonCancelar;
    @FXML
    private TextField nombreGasto;
    @FXML
    private TextField costeGasto_campo;
    @FXML
    private TextField descripcionGasto_campo;
    @FXML
    private Button anadirRecibo_boton;
    @FXML
    private ImageView anadirRecibo_imagen;
    @FXML
    private TextField Unidades_campo;
    @FXML
    private Label fecha;

    private Stage stage;
    private Category category;
    private ObservableList<Charge> chargeList;
    private Image receiptImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonAceptar.setDisable(true);
        nombreGasto.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        costeGasto_campo.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        descripcionGasto_campo.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        Unidades_campo.textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        // Set the current date to the fecha label
        fecha.setText(LocalDate.now().toString());

        botonAceptar.setOnAction(event -> {
            try {
                handleAceptar();
            } catch (AcountDAOException | IOException ex) {
                Logger.getLogger(CrearGastoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        botonCancelar.setOnAction(event -> handleCancelar());
        anadirRecibo_boton.setOnAction(this::handleLoadImageAction);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setResizable(false);
        this.stage.initModality(Modality.APPLICATION_MODAL);
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setChargeList(ObservableList<Charge> chargeList) {
        this.chargeList = chargeList;
    }

    private void validateFields() {
        boolean disableButton = nombreGasto.getText().trim().isEmpty()
                || costeGasto_campo.getText().trim().isEmpty()
                || descripcionGasto_campo.getText().trim().isEmpty()
                || Unidades_campo.getText().trim().isEmpty();
        botonAceptar.setDisable(disableButton);
    }

    private void handleAceptar() throws AcountDAOException, IOException {
        String name = nombreGasto.getText();
        String description = descripcionGasto_campo.getText();
        double cost;
        int units;
        try {
            cost = Double.parseDouble(costeGasto_campo.getText());
            units = Integer.parseInt(Unidades_campo.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Formato Incorrecto");
            alert.setHeaderText("El coste y las unidades deben ser números válidos.");
            alert.setContentText("Por favor, introduce números válidos para el coste y las unidades.");
            alert.showAndWait();
            return;
        }
        LocalDate date = LocalDate.now(); // Use the current date

        boolean success = Acount.getInstance().registerCharge(name, description, cost, units, receiptImage, date, category);
        if (success) {
            chargeList.setAll(Acount.getInstance().getUserCharges());
            stage.close(); // Cerrar ventana
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al crear el gasto");
            alert.setContentText("No se pudo crear el gasto. Inténtalo de nuevo.");
            alert.showAndWait();
        }
    }

    private void handleCancelar() {
        stage.close();
    }

    private void handleLoadImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del recibo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(anadirRecibo_imagen.getScene().getWindow());
        if (file != null) {
            receiptImage = new Image(file.toURI().toString());
            anadirRecibo_imagen.setImage(receiptImage);
        }
    }
}

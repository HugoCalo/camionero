package javafxmlapplication.creacionCategoria;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;

public class CreacionCategoriaController implements Initializable {

    @FXML
    private TextField nombreCategoria;
    @FXML
    private TextField descripcionCategoria;
    @FXML
    private Button botonAceptar;
    @FXML
    private Button botonCancelar;

    private Stage stage;
    private ObservableList<Category> categoryList;
    @FXML
    private Label titulo;

    private Category categoryToModify;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonAceptar.setDisable(true);
        nombreCategoria.setOnAction(event -> descripcionCategoria.requestFocus());
        descripcionCategoria.setOnAction(event -> botonAceptar.requestFocus());

        nombreCategoria.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        descripcionCategoria.textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        nombreCategoria.setOnAction(event -> descripcionCategoria.requestFocus());
        botonAceptar.setOnAction(event -> {
            try {
                handleAceptar();
            } catch (AcountDAOException | IOException ex) {
                Logger.getLogger(CreacionCategoriaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        botonCancelar.setOnAction(event -> handleCancelar());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setResizable(false);
        this.stage.initModality(Modality.APPLICATION_MODAL);
    }

    public void setCategoryList(ObservableList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void loadCategoryData(Category category) {
        this.categoryToModify = category;
        nombreCategoria.setText(category.getName());
        descripcionCategoria.setText(category.getDescription());
        titulo.setText("Modificar Categoría");
    }

    private void validateFields() {
        boolean disableButton = nombreCategoria.getText().trim().isEmpty() || descripcionCategoria.getText().trim().isEmpty();
        botonAceptar.setDisable(disableButton);
    }

    private void handleAceptar() throws AcountDAOException, IOException {
        String name = nombreCategoria.getText();
        String description = descripcionCategoria.getText();

        if (categoryToModify != null) {
            // Modifying an existing category
            List<Charge> charges = Acount.getInstance().getUserCharges();
            List<Charge> chargesToModify = charges.stream()
                    .filter(charge -> charge.getCategory().getName().equals(categoryToModify.getName()))
                    .collect(Collectors.toList());

            Acount.getInstance().removeCategory(categoryToModify);

            boolean success = Acount.getInstance().registerCategory(name, description);
            if (success) {
                Category newCategory = Acount.getInstance().getUserCategories().stream()
                        .filter(cat -> cat.getName().equals(name))
                        .findFirst()
                        .orElse(null);

                if (newCategory != null) {
                    for (Charge charge : chargesToModify) {
                        Acount.getInstance().registerCharge(charge.getName(), charge.getDescription(),
                                charge.getCost(), charge.getUnits(), charge.getImageScan(), charge.getDate(), newCategory);
                    }
                }

                categoryList.setAll(Acount.getInstance().getUserCategories());
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al modificar la categoría");
                alert.setContentText("No se pudo modificar la categoría. Inténtalo de nuevo.");
                alert.showAndWait();
            }
        } else {
            // Creating a new category
            if (!name.isEmpty() && !description.isEmpty()) {
                if (isCategoryDuplicate(name)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Categoría duplicada");
                    alert.setHeaderText("La categoría ya existe");
                    alert.setContentText("Por favor, elige un nombre diferente para la categoría.");
                    alert.showAndWait();
                    return;
                }

                boolean success = Acount.getInstance().registerCategory(name, description);
                if (success) {
                    categoryList.setAll(Acount.getInstance().getUserCategories());
                    stage.close(); // Cerrar ventana
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error al crear la categoría");
                    alert.setContentText("No se pudo crear la categoría. Inténtalo de nuevo.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Campos vacíos");
                alert.setHeaderText("Campos requeridos");
                alert.setContentText("Por favor, rellena todos los campos.");
                alert.showAndWait();
            }
        }
    }

    private boolean isCategoryDuplicate(String name) {
        for (Category category : categoryList) {
            if (category.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void handleCancelar() {
        stage.close();
    }
}

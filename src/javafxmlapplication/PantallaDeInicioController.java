/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit este template
 */
package javafxmlapplication;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxmlapplication.CompararMeses.CompararMesesController;
import javafxmlapplication.DesgloseDeGastos.DesgloseDeGastosController;
import javafxmlapplication.creacionCategoria.CreacionCategoriaController;
import model.Acount;
import model.Category;
import model.AcountDAOException;

/**
 * FXML Controller class
 *
 */
public class PantallaDeInicioController implements Initializable {

    @FXML
    private Button logo_button;
    @FXML
    private Button show_mensual_cost;
    @FXML
    private Button compare_month;
    @FXML
    private Button show_profile;
    @FXML
    private Button log_out;
    @FXML
    private Button add_cost;
    @FXML
    private Button show_category;
    @FXML
    private Button add_category;
    @FXML
    private Button modify_category;
    @FXML
    private Button delete_category;
    @FXML
    private TableColumn<Category, String> column_name;
    @FXML
    private TableColumn<Category, String> column_description;
    @FXML
    private TableColumn<Category, Double> column_price;
    @FXML
    private ScrollPane scrollpane_category;
    @FXML
    private TableView<Category> tableview_category;

    private Stage stage;
    private Stage loginStage;
    private ObservableList<Category> categoryList;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setStageLogin(Stage loginStage) {
        this.loginStage = loginStage;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            categoryList = FXCollections.observableArrayList(Acount.getInstance().getUserCategories());
        } catch (AcountDAOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableview_category.setItems(categoryList);

        column_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        column_description.setCellValueFactory(new PropertyValueFactory<>("description"));

        logo_button.setOnAction(event -> handleLogoButton(event));
        show_mensual_cost.setOnAction(event -> handleShowMensualCost(event));
        compare_month.setOnAction(event -> handleCompareMonth(event));
        show_profile.setOnAction(event -> {
            try {
                handleShowProfile(event);
            } catch (AcountDAOException ex) {
                Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        log_out.setOnAction(event -> handleLogOut(event));
        add_cost.setOnAction(event -> handleAddCost(event));
        show_category.setOnAction(event -> handleShowGastosDeCategoria(event));
        add_category.setOnAction(event -> handleAddCategory(event));
        modify_category.setOnAction(event -> handleModifyCategory(event));
        delete_category.setOnAction(event -> handleDeleteCategory(event));
    }

    private void handleLogOut(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Cierre de sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Estas seguro que quieres cerrar la sesion?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Acount.getInstance().logOutUser();
                    if (stage != null) {
                        stage.close();
                    }
                    if (loginStage != null) {
                        loginStage.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AcountDAOException ex) {
                    Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void handleLogoButton(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Logo Button");
        alert.setHeaderText(null);
        alert.setContentText("Logo button clicked!");
        alert.showAndWait();
    }

    private void handleShowMensualCost(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Show Mensual Cost");
        alert.setHeaderText(null);
        alert.setContentText("Showing mensual cost!");
        alert.showAndWait();
    }

private void handleCompareMonth(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CompararMeses/CompararMeses.fxml"));
        Parent newSceneParent = loader.load();
        
        // Obtener el controlador de la nueva escena
        CompararMesesController controller = loader.getController();
        
        // Configurar las propiedades necesarias en el controlador
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        controller.setStage(currentStage);

        // Cambiar a la nueva escena
        Scene newScene = new Scene(newSceneParent);
        currentStage.setScene(newScene);
        currentStage.show();
    } catch (IOException e) {
        Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, e);
    }
}


    private void handleShowProfile(ActionEvent event) throws AcountDAOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CrearCuenta.fxml"));
            Parent root = loader.load();

            CrearCuentaController controller = loader.getController();
            controller.setLoginStage(loginStage);
            controller.setEditProfileMode(); // Método nuevo para configurar la pantalla para editar perfil

            Stage stage = new Stage();
            stage.setTitle("Modificar Perfil");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleAddCost(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Add Cost");
        alert.setHeaderText(null);
        alert.setContentText("Adding a new cost!");
        alert.showAndWait();
    }

    private void handleShowGastosDeCategoria(ActionEvent event) {
        Category selectedCategory = tableview_category.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/DesgloseDeGastos/DesgloseDeGastos.fxml"));
                Parent newSceneParent = loader.load();
                Scene newScene = new Scene(newSceneParent);
                Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                currentStage.setScene(newScene);
                currentStage.show();

                DesgloseDeGastosController controller = loader.getController();
                controller.setStage(currentStage);
                controller.setLoginStage(loginStage);
                controller.setCategory(selectedCategory);

            } catch (IOException | AcountDAOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Visualizar Gastos de Categoría");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una categoría para ver sus gastos.");
            alert.showAndWait();
        }
    }

    private void handleAddCategory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/creacionCategoria/CreacionCategoria.fxml"));
            Parent root = loader.load();

            CreacionCategoriaController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Crear Categoría");
            newStage.setScene(new Scene(root));
            controller.setStage(newStage);
            controller.setCategoryList(categoryList);

            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleModifyCategory(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Modify Category");
        alert.setHeaderText(null);
        alert.setContentText("Modifying category!");
        alert.showAndWait();
    }

    private void handleDeleteCategory(ActionEvent event) {
        Category selectedCategory = tableview_category.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Categoría");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro que quieres eliminar la categoría seleccionada?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        boolean success = Acount.getInstance().removeCategory(selectedCategory);
                        if (success) {
                            categoryList.remove(selectedCategory);
                        } else {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Error al eliminar la categoría");
                            errorAlert.setContentText("No se pudo eliminar la categoría. Inténtalo de nuevo.");
                            errorAlert.showAndWait();
                        }
                    } catch (AcountDAOException | IOException e) {
                        Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            });
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Eliminar Categoría");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una categoría para eliminar.");
            alert.showAndWait();
        }
    }
}

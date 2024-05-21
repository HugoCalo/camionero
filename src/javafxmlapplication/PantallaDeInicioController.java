/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafxmlapplication.JavaFXMLApplication;
import model.Acount;
import model.AcountDAOException;

/**
 * FXML Controller class
 *
 * @author hugoc
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
    private TableColumn<?, ?> column_emoji;
    @FXML
    private TableColumn<?, ?> column_name;
    @FXML
    private TableColumn<?, ?> column_description;
    @FXML
    private TableColumn<?, ?> column_price;

    private Stage stage;
    
    private Stage loginStage;
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
     public void setStageLogin(Stage loginStage){
        this.loginStage = loginStage;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logo_button.setOnAction(event -> handleLogoButton(event));
        show_mensual_cost.setOnAction(event -> handleShowMensualCost(event));
        compare_month.setOnAction(event -> handleCompareMonth(event));
        show_profile.setOnAction(event -> handleShowProfile(event));
        log_out.setOnAction(event -> handleLogOut(event));
        add_cost.setOnAction(event -> handleAddCost(event));
        show_category.setOnAction(event -> handleShowCategory(event));
        add_category.setOnAction(event -> handleAddCategory(event));
        modify_category.setOnAction(event -> handleModifyCategory(event));
        delete_category.setOnAction(event -> handleDeleteCategory(event));

    }    
    private void handleLogOut(ActionEvent event){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Cierre de sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Estas seguro que quieres cerrar la sesion?");    
        
        alert.showAndWait().ifPresent(response ->{
            if(response == ButtonType.OK){
                try{
                    Acount.getInstance().logOutUser();
                    stage.close();
                    loginStage.show();
                }catch(IOException e){
                    e.printStackTrace();
                } catch (AcountDAOException ex) {
                    Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }); 
    }
        private void handleLogoButton(ActionEvent event) {
        // Handle the logo button click event
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Logo Button");
        alert.setHeaderText(null);
        alert.setContentText("Logo button clicked!");
        alert.showAndWait();
    }

    private void handleShowMensualCost(ActionEvent event) {
        // Handle show mensual cost button click event
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Show Mensual Cost");
        alert.setHeaderText(null);
        alert.setContentText("Showing mensual cost!");
        alert.showAndWait();
    }
     private void handleCompareMonth(ActionEvent event) {
        // Handle compare month button click event
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Compare Month");
        alert.setHeaderText(null);
        alert.setContentText("Comparing months!");
        alert.showAndWait();
    }
      private void handleShowProfile(ActionEvent event) {
        // Handle show profile button click event
        // Add your logic to show profile
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Show Profile");
        alert.setHeaderText(null);
        alert.setContentText("Showing profile!");
        alert.showAndWait();
    }

    private void handleAddCost(ActionEvent event) {
        // Handle add cost button click event
        // Add your logic to add a cost
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Add Cost");
        alert.setHeaderText(null);
        alert.setContentText("Adding a new cost!");
        alert.showAndWait();
    }

    private void handleShowCategory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/DesgloseDeGastos/DesgloseDeGastos.fxml"));
            Parent newSceneParent = loader.load();
            Scene newScene = new Scene(newSceneParent);
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    private void handleAddCategory(ActionEvent event) {
        // Handle add category button click event
        // Add your logic to add category
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Add Category");
        alert.setHeaderText(null);
        alert.setContentText("Adding a new category!");
        alert.showAndWait();
    }

    private void handleModifyCategory(ActionEvent event) {
        // Handle modify category button click event
        // Add your logic to modify category
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Modify Category");
        alert.setHeaderText(null);
        alert.setContentText("Modifying category!");
        alert.showAndWait();
    }

    private void handleDeleteCategory(ActionEvent event) {
        // Handle delete category button click event
        // Add your logic to delete category
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText(null);
        alert.setContentText("Deleting category!");
        alert.showAndWait();
    }
}

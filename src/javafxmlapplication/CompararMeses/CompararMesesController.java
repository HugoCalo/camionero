/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication.CompararMeses;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

/**
 * FXML Controller class
 *
 * @author Alex
 */
public class CompararMesesController implements Initializable {

    @FXML
    private Button volvera_pantalla_inicio;
    @FXML
    private DatePicker comp_meses_mes1;
    @FXML
    private DatePicker comp_meses_mes2;
    @FXML
    private DatePicker comp_año_mes;
    @FXML
    private DatePicker comp_año_año;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmlapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jsoler
 */
public class InicioDeSesionController implements Initializable {
    @FXML
    private Button button_init_sesion;
    @FXML
    private Button button_registro;
    
    //=========================================================
    // event handler, fired when button is clicked or 
    //                      when the button has the focus and enter is pressed
        @Override
    public void initialize(URL url, ResourceBundle rb) {
         button_registro.setOnAction(event -> {
            try {
                switchToRegisterScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    
    
    //=========================================================
    // you must initialize here all related with the object 
    
    private void switchToRegisterScene() throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("CrearCuenta.fxml"));
        Parent root = cargador.load();
        JavaFXMLApplication.setRoot(root);
    }
}
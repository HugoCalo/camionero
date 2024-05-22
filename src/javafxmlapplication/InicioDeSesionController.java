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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Acount;
import model.AcountDAOException;

/**
 *
 * @author jsoler
 */
public class InicioDeSesionController implements Initializable {
    @FXML
    private Button button_init_sesion;
    @FXML
    private Button button_registro;
    @FXML
    private Label aviso_usuario_login;
    @FXML
    private Label aviso_password_login;
    
    private Stage stage;
    
    //=========================================================
    // event handler, fired when button is clicked or 
    //                      when the button has the focus and enter is pressed
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    
    
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setResizable(false);  // Evitar que la ventana sea redimensionable
    }
    
    
        @Override
    public void initialize(URL url, ResourceBundle rb) {
        aviso_usuario_login.setVisible(false);
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> button_init_sesion.requestFocus());
        
        button_registro.setOnAction(event -> {
            try {
                switchToRegisterScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
         button_init_sesion.setOnAction(event -> {
             try{
             handleLogin(event);
             }catch (IOException e) {     
                 e.printStackTrace();
             }
         });
    }
    
    
    
    //=========================================================
    // you must initialize here all related with the object 
    
    private void switchToRegisterScene() throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("CrearCuenta.fxml"));
        Parent root = cargador.load();
        CrearCuentaController controller = cargador.getController();
        controller.setLoginStage(stage); // Cambiado: Se pasa el stage al controlador de CrearCuenta
        Scene scene = new Scene(root);
        stage.setScene(scene);      
    }
    
    private void handleLogin(ActionEvent event) throws IOException {
        aviso_usuario_login.setVisible(false);
        String username = usernameField.getText();
        String password = passwordField.getText();
        // Intenta iniciar sesión con las credenciales proporcionadas
      try {
            boolean isLoggedIn = Acount.getInstance().logInUserByCredentials(username, password);
            if (isLoggedIn) {
                switchToMainScene();
            } else {
                mostrarAvisoLogin();
            }
        } catch (AcountDAOException e) {
            mostrarAvisoLogin();
        }
    }

    private void switchToMainScene() throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("PantallaDeInicio.fxml"));
        Parent root = cargador.load();
        PantallaDeInicioController controlador = cargador.getController();
        Stage stageInicio = new Stage();
        stageInicio.setScene(new Scene(root));
        stageInicio.setTitle("CashSplash");
        stageInicio.show();

        controlador.setStageLogin(stage); // Cambiado: Se pasa el stage actual al controlador de PantallaDeInicio
        controlador.setStage(stageInicio);

        stage.hide();
        limpiarCampos();
    }

    private void mostrarAvisoLogin() {
        aviso_usuario_login.setVisible(true);
        limpiarCampos();
    }

    private void limpiarCampos() {
        usernameField.clear();
        passwordField.clear();
    }
}










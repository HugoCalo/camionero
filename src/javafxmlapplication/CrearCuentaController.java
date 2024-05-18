/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import model.Acount;
import model.AcountDAOException;
/**
 * FXML Controller class
 *
 * @author hugoc
 */
public class CrearCuentaController implements Initializable {


    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField nicknameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button registerButton;
    
    private Label messageLabel;
    
    private Image profileImage;
    @FXML
    private Label aviso_campo_nombre;
    @FXML
    private Label aviso_campo_apellidos;
    @FXML
    private Label aviso_campo_usuario;
    @FXML
    private Label aviso_campo_correo;
    @FXML
    private Label aviso_campo_contrasena;
    @FXML
    private PasswordField confirmar_password;
    @FXML
    private Label aviso_campo_rep_contrasena;
    @FXML
    private Button añadir_imagen_perfil;
    @FXML
    private Button cancelar_registro_boton;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         // Set focus traversal policy
         //ponemos todos los avisos invisibles
        ponerAvisosEnBlacnco();
        
        nameField.setOnAction(event -> surnameField.requestFocus());
        surnameField.setOnAction(event -> nicknameField.requestFocus());
        nicknameField.setOnAction(event -> emailField.requestFocus());
        emailField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> confirmar_password.requestFocus());
        confirmar_password.setOnAction(event -> registerButton.requestFocus());
        
        cancelar_registro_boton.setOnAction(event -> {
            try {
                ponerEnBlanco();
                switchToLoginScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        registerButton.setOnAction(evento ->{
            try {
                if(handleRegisterAction(evento)){
                    ponerEnBlanco();
                    switchToLoginScene();
                }
            } catch (IOException | AcountDAOException e) {
                e.printStackTrace();
            }
        });
    };    
    
    private boolean handleRegisterAction(ActionEvent event) throws AcountDAOException, IOException {
        ponerAvisosEnBlacnco();
        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String nickname = nicknameField.getText();
        String password = passwordField.getText();
        boolean isValid = true;
        
            
         if(name.isEmpty()){aviso_campo_nombre.setVisible(true); isValid = false;}    
         
         if(surname.isEmpty()){aviso_campo_apellidos.setVisible(true); isValid = false;}   
         
         if(password.isEmpty()){aviso_campo_contrasena.setVisible(true); isValid = false;}
         
         if(nickname.isEmpty()){aviso_campo_usuario.setVisible(true); isValid = false;}
             
         if (!isValidEmail(email)) {
            aviso_campo_correo.setVisible(true);
            isValid = false;
        }
         
        if(!verificarContrasenas()){
            isValid = false;
        }
        
        if (password.length() < 6) {
            aviso_campo_contrasena.setVisible(true);
            isValid = false;
        }
         if (!isValid) {
            return false;
        }
        
        boolean isRegistered = Acount.getInstance().registerUser(name, surname, email, nickname, password, profileImage, LocalDate.now());
        if (isRegistered) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("¡Usuario registrado exitosamente! Por favor, autentíquese.");
            alert.showAndWait();
            return true;
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error al registrar el usuario");
            alert.setHeaderText(null);
            alert.setContentText(null);
            alert.showAndWait();
            return false;
        }
    }
     
    
     private void handleLoadImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de perfil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            profileImage = new Image(file.toURI().toString());
            profileImageView.setImage(profileImage);
        }    
    }
     
     
    private boolean verificarContrasenas() {
        String password = passwordField.getText();
        String confirmarPassword = confirmar_password.getText();

        if (!password.equals(confirmarPassword)) {
            aviso_campo_rep_contrasena.setVisible(true);
            return false;
        }else{return true;}
        
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }
    
    private void switchToLoginScene() throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("InicioDeSesion.fxml"));
        Parent root = cargador.load();
        JavaFXMLApplication.setRoot(root);
    }
    
    private void ponerEnBlanco(){
        nameField.clear();
        surnameField.clear();
        passwordField.clear();
        confirmar_password.clear();
        emailField.clear();
        profileImage = null;
        ponerAvisosEnBlacnco();
    }
    private void ponerAvisosEnBlacnco(){
        aviso_campo_nombre.setVisible(false);
        aviso_campo_apellidos.setVisible(false);
        aviso_campo_correo.setVisible(false);
        aviso_campo_contrasena.setVisible(false);
        aviso_campo_rep_contrasena.setVisible(false);
    }
}

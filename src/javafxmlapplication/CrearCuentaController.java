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
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         // Set focus traversal policy
        nameField.setOnAction(event -> surnameField.requestFocus());
        surnameField.setOnAction(event -> emailField.requestFocus());
        emailField.setOnAction(event -> nicknameField.requestFocus());
        nicknameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> registerButton.requestFocus());
    }    
    private void handleRegisterAction(ActionEvent event) throws AcountDAOException, IOException {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String nickname = nicknameField.getText();
        String password = passwordField.getText();
        
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || nickname.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Todos los campos excepto la imagen son obligatorios.");
            return;
        }
        
         if (!isValidEmail(email)) {
            messageLabel.setText("Correo electrónico no válido.");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("La contraseña debe tener al menos 6 caracteres.");
            return;
        }
        
         boolean isRegistered = Acount.getInstance().registerUser(name, surname, email, nickname, password, profileImage, LocalDate.now());
        if (isRegistered) {
            messageLabel.setText("Usuario registrado exitosamente. Por favor, autentíquese.");
        } else {
            messageLabel.setText("Error al registrar el usuario. El nickname ya existe.");
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }
}

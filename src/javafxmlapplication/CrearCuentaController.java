package javafxmlapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Acount;
import model.AcountDAOException;
import model.User;

public class CrearCuentaController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField nicknameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private ImageView profileImageView;
    @FXML private Button registerButton;
    private Image profileImage;
    @FXML private Label aviso_campo_nombre;
    @FXML private Label aviso_campo_apellidos;
    @FXML private Label aviso_campo_usuario;
    @FXML private Label aviso_campo_correo;
    @FXML private Label aviso_campo_contrasena;
    @FXML private PasswordField confirmar_password;
    @FXML private Label aviso_campo_rep_contrasena;
    @FXML private Button añadir_imagen_perfil;
    @FXML private Button cancelar_registro_boton;

    private Stage loginStage;
    private Stage stage;
    @FXML
    private Label titulin;

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ponerAvisosEnBlanco();
        setupFocusTraversal();
        setupButtonActions();
    }

    private void setupFocusTraversal() {
        nameField.setOnAction(event -> surnameField.requestFocus());
        surnameField.setOnAction(event -> nicknameField.requestFocus());
        nicknameField.setOnAction(event -> emailField.requestFocus());
        emailField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> confirmar_password.requestFocus());
        confirmar_password.setOnAction(event -> registerButton.requestFocus());
    }

    private void setupButtonActions() {
        cancelar_registro_boton.setOnAction(event -> {
            try {
                ponerEnBlanco();
                switchToLoginScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        registerButton.setOnAction(event -> {
            try {
                if (handleRegisterAction(event)) {
                    ponerEnBlanco();
                    switchToLoginScene();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        añadir_imagen_perfil.setOnAction(this::handleLoadImageAction);
    }

    private boolean handleRegisterAction(ActionEvent event) throws IOException {
        ponerAvisosEnBlanco();
        if (!validarCampos()) return false;

        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String nickname = nicknameField.getText();
        String password = passwordField.getText();

        try {
            Acount.getInstance().registerUser(name, surname, email, nickname, password, profileImage, LocalDate.now());
            mostrarAlerta(AlertType.INFORMATION, "Registro Exitoso", "¡Usuario registrado exitosamente! Por favor, autentíquese.");
            return true;
        } catch (AcountDAOException e) {
            mostrarAlerta(AlertType.ERROR, "Error al registrar el usuario", "El usuario " + nickname + " ya ha sido registrado previamente");
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

    private boolean validarCampos() {
        boolean isValid = true;

        if (nameField.getText().isEmpty()) {
            aviso_campo_nombre.setVisible(true);
            isValid = false;
        }
        if (surnameField.getText().isEmpty()) {
            aviso_campo_apellidos.setVisible(true);
            isValid = false;
        }
        if (nicknameField.getText().isEmpty()) {
            aviso_campo_usuario.setVisible(true);
            isValid = false;
        }
        if (!isValidEmail(emailField.getText())) {
            aviso_campo_correo.setVisible(true);
            isValid = false;
        }
        if (passwordField.getText().length() < 6) {
            aviso_campo_contrasena.setVisible(true);
            isValid = false;
        }
        if (!passwordField.getText().equals(confirmar_password.getText())) {
            aviso_campo_rep_contrasena.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    private void switchToLoginScene() throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("InicioDeSesion.fxml"));
        Parent root = cargador.load();
        InicioDeSesionController controller = cargador.getController();
        controller.setStage(loginStage); // Asegúrate de que el stage se pasa al controlador de InicioDeSesion
        Scene scene = new Scene(root);
        loginStage.setScene(scene);
    }

    private void ponerEnBlanco() {
        nameField.clear();
        surnameField.clear();
        passwordField.clear();
        confirmar_password.clear();
        emailField.clear();
        nicknameField.clear();
        profileImageView.setImage(null);
        ponerAvisosEnBlanco();
    }

    private void ponerAvisosEnBlanco() {
        aviso_campo_nombre.setVisible(false);
        aviso_campo_apellidos.setVisible(false);
        aviso_campo_usuario.setVisible(false);
        aviso_campo_correo.setVisible(false);
        aviso_campo_contrasena.setVisible(false);
        aviso_campo_rep_contrasena.setVisible(false);
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // Método para configurar la pantalla en modo edición de perfil
    public void setEditProfileMode() throws AcountDAOException, IOException {
        User user = Acount.getInstance().getLoggedUser();

        nameField.setText(user.getName());
        surnameField.setText(user.getSurname());
        emailField.setText(user.getEmail());
        nicknameField.setText(user.getNickName());
        nicknameField.setDisable(true); // No se puede modificar el nickname
        passwordField.setText(user.getPassword());
        confirmar_password.setText(user.getPassword());
        profileImageView.setImage(user.getImage());
        
        titulin.setText("Modificar perfil");
        registerButton.setText("Guardar Cambios");
        registerButton.setOnAction(event -> {
            try {
                if (handleUpdateProfileAction(event)) {
                    ponerEnBlanco();
                    stage.close();
                    switchToLoginScene();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean handleUpdateProfileAction(ActionEvent event) throws IOException {
        ponerAvisosEnBlanco();
        if (!validarCampos()) return false;

        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = Acount.getInstance().getLoggedUser();
            user.setName(name);
            user.setSurname(surname);
            user.setEmail(email);
            user.setPassword(password);
            user.setImage(profileImage);

            
            return true;
        } catch (AcountDAOException e) {
            mostrarAlerta(AlertType.ERROR, "Error al actualizar el perfil", "Ocurrió un error al intentar actualizar el perfil");
            return false;
        }
    }
}

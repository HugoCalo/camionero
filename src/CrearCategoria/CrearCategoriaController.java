/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package CrearCategoria;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Acount;
import model.AcountDAOException;
import model.Category;

/**
 * FXML Controller class
 *
 * @author Jordi
 */
public class CrearCategoriaController implements Initializable {

    @FXML
    private TextField categoria_text_field;
    @FXML
    private Label aviso_categoria;
    @FXML
    private ImageView emoticono_image_viewer;
    @FXML
    private Button boton_seleccionar_emoticono;
    @FXML
    private TextField descripcion_text_field;
    @FXML
    private Button cancelar_categoria;
    @FXML
    private Button añadir_boton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        añadir_boton.setOnAction(event -> {
            try {
                if (crearCategoria(event)) {
                    ponerEnBlanco();
                    // switchToLoginScene(); // Descomenta esto si tienes un método para cambiar de escena
                }
            } catch (IOException | AcountDAOException e) {
                e.printStackTrace();
            }
        });
        // TODO
    }    

    @FXML
    private boolean crearCategoria(ActionEvent event) throws IOException, AcountDAOException {
        ponerEnBlanco();
        String nombreCategoria = categoria_text_field.getText();
        String descripcionCategoria = descripcion_text_field.getText();

        if (nombreCategoria.isEmpty()) {
            aviso_categoria.setText("El nombre de la categoría no puede estar vacío.");
            aviso_categoria.setVisible(true);
            return false;
        }

        try {
            // Obtener la instancia de Acount
            Acount acount = Acount.getInstance();

            // Registrar la nueva categoría
            boolean isRegistered = acount.registerCategory(nombreCategoria, descripcionCategoria);

            if (isRegistered) {
                System.out.println("Categoría registrada exitosamente.");
                ponerEnBlanco();
                // switchToPantallaPrincipal(); // Descomenta esto si tienes un método para cambiar de escena
            } else {
                System.out.println("Error al registrar la categoría. Por favor, intente de nuevo.");
                aviso_categoria.setText("Error al registrar la categoría. Por favor, intente de nuevo.");
                aviso_categoria.setVisible(true);
            }
            return isRegistered;
        } catch (IOException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
            aviso_categoria.setText("Error al conectar con la base de datos: " + e.getMessage());
            aviso_categoria.setVisible(true);
            throw e;
        } catch (AcountDAOException e) {
            System.out.println("Error al registrar la categoría: " + e.getMessage());
            aviso_categoria.setText("Error al registrar la categoría: " + e.getMessage());
            aviso_categoria.setVisible(true);
            throw e;
        }
    }
    
    private void modificarCategoria(Category categoria) throws AcountDAOException {
    
    
    }
    
    public void setAdd(){
   
    }
    
    
    private void ponerEnBlanco() {
        categoria_text_field.setText("");
        descripcion_text_field.setText("");
        aviso_categoria.setText("");
        aviso_categoria.setVisible(false);
    }  
    
}
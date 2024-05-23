package javafxmlapplication.CompararMeses;

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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafxmlapplication.PantallaDeInicioController;

/**
 * FXML Controller class
 *
 * @author hugoc
 */
public class CompararMesesController implements Initializable {

    @FXML
    private Button return_pantalla_principal;
    @FXML
    private DatePicker datePicker1;
    @FXML
    private DatePicker datePicker2;
    @FXML
    private Button compare_button;
    @FXML
    private TableView<?> table_view_resumen;
    @FXML
    private TableColumn<?, ?> categoria_column;
    @FXML
    private TableColumn<?, ?> gasto_column;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label label_definicion;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        return_pantalla_principal.setOnAction(event -> handleReturnPantallaPrincipal(event));
    }    

    private void handleReturnPantallaPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/PantallaDeInicio.fxml"));
            Parent newSceneParent = loader.load();
            
            // Obtener el controlador de la nueva escena
            PantallaDeInicioController controller = loader.getController();
            
            // Configurar las propiedades necesarias en el controlador
            controller.setStage(stage);

            // Cambiar a la nueva escena
            Scene newScene = new Scene(newSceneParent);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(CompararMesesController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

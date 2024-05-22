/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication.DesgloseDeGastos;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafxmlapplication.JavaFXMLApplication;
import javafxmlapplication.JavaFXMLApplication;
import javafxmlapplication.PantallaDeInicioController;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.Charge;

/**
 * FXML Controller class
 *
 * @author Alex
 */
public class DesgloseDeGastosController implements Initializable {
    
    private Stage stage;
    private Stage loginStage;
    
    @FXML
    private Button volverainicio_boton;
    @FXML
    private Tooltip volverainico_señal;
    @FXML
    private Label nombre_categoria_letra;
    @FXML
    private Label descripcion_categoria_letra;
    @FXML
    private Button añadir_gasto_boton;
    @FXML
    private Button modificar_gasto_boton;
    @FXML
    private Button eliminar_gasto_boton;
    @FXML
    private ScrollPane pantalla_gastos;
    @FXML
    private TableView<Charge> tableview_gastos_cat;
    @FXML
    private TableColumn<Charge, String> nameColumn;
    @FXML
    private TableColumn<Charge, LocalDate> dateColumn;
    @FXML
    private TableColumn<Charge, Double> costColumn;
    @FXML
    private TableColumn<Charge, String> descriptionColumn;
    @FXML
    private TableColumn<Charge, ImageView> reciboColumn;
    
    private ObservableList<Charge> chargesList;
    
    private String category;
    
    private Charge selectedCharge;

    /**
     * Initializes the controller class.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }
    
    public void initialize(URL url, ResourceBundle rb) {
        modificar_gasto_boton.setDisable(true);
        eliminar_gasto_boton.setDisable(true);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reciboColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView(cellData.getValue().getImageScan());
            imageView.setFitHeight(50);  // Altura de la imagen
            imageView.setPreserveRatio(true);  // Mantener relación de aspecto
            return new ReadOnlyObjectWrapper<>(imageView);
        });
        
        volverainicio_boton.setDisable(false);
        volverainicio_boton.setVisible(true);
        
        volverainicio_boton.setOnMouseEntered(event -> {
            Tooltip.install(volverainicio_boton, volverainico_señal); // Mostrar tooltip
        });

        volverainicio_boton.setOnMouseExited(event -> {
            Tooltip.uninstall(volverainicio_boton, volverainico_señal); // Ocultar tooltip
        });
        
        volverainicio_boton.setOnAction(event ->{
         System.out.println("Cambiando a Pantalla Principal");
            try{
                switchToPantallaPrincipal(event);
            }catch(IOException e){
                 e.printStackTrace();

            }
        });
        
        tableview_gastos_cat.setRowFactory(tv -> {
            TableRow<Charge> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    if (row.isSelected()) {
                        tableview_gastos_cat.getSelectionModel().clearSelection(row.getIndex());
                        selectedCharge = null;
                    } else {
                        tableview_gastos_cat.getSelectionModel().select(row.getIndex());
                        selectedCharge = row.getItem();
                    }
                }
            });
            return row;
        });
        
        // Listener para la selección de filas en la tabla
        tableview_gastos_cat.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                modificar_gasto_boton.setDisable(false);
                eliminar_gasto_boton.setDisable(false);
            } else {
                modificar_gasto_boton.setDisable(true);
                eliminar_gasto_boton.setDisable(true);
                selectedCharge = null;
            }
        });
        
        chargesList = FXCollections.observableArrayList();
        chargesList.addListener((ListChangeListener<Charge>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
                    tableview_gastos_cat.refresh();
                }
            }
        });
        
        eliminar_gasto_boton.setOnAction(event -> {
            try {
                eliminarGasto(selectedCharge);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AcountDAOException ex) {
                Logger.getLogger(DesgloseDeGastosController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        añadir_gasto_boton.setOnAction(event -> {
            añadirGasto();
            
        });
        
        modificar_gasto_boton.setOnAction(event ->{
            modificarGasto(selectedCharge);
        });


    }    
    
    private void switchToPantallaPrincipal(ActionEvent event) throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/javafxmlapplication/PantallaDeInicio.fxml"));
        Parent root = cargador.load();
        Scene newScene = new Scene(root);
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        currentStage.setScene(newScene);
        currentStage.show();

        // Pasa los stages al controlador de PantallaDeInicio
        PantallaDeInicioController controller = cargador.getController();
        controller.setStage(currentStage);
        controller.setStageLogin(loginStage);
    }
    
    public void setCategory(String categoryName) throws AcountDAOException, IOException {
        category = categoryName;
        chargesList = FXCollections.observableArrayList(getChargesByCategory(category));
        tableview_gastos_cat.setItems(chargesList);
    }
    
    private List<Charge> getChargesByCategory(String categoryName) throws AcountDAOException, IOException {
        // Obtener el usuario logueado
        Acount acount = Acount.getInstance();
        // Obtener la lista de gastos del usuario logueado
        List<Charge> allCharges = acount.getUserCharges();
        // Filtrar por categoría
        return allCharges.stream()
                .filter(charge -> charge.getCategory().getName().equals(categoryName))
                .collect(Collectors.toList());
    }
    
    
    
    private void añadirGasto(){/*
         try {
            // Cargar el archivo FXML para la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CrearGasto.fxml"));
            Parent root = loader.load();

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Añadir gasto");
            stage.setScene(new Scene(root));
            //Configurar para que cree gastos
            CrearCategoriaController controlador = loader.getController();
            controlador.setAdd();
            // Mostrar la nueva ventana
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    private void modificarGasto(Charge gastoSelect){/*
         try {
            // Cargar el archivo FXML para la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CrearGasto.fxml"));
            Parent root = loader.load();

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Modificar gasto");
            stage.setScene(new Scene(root));
            //Configurar para que modifique un gasto
            CrearCategoriaController controlador = loader.getController();
            controlador.setStage(stage);
            controlador.setModify(selectedCharge);
            // Mostrar la nueva ventana
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    private void eliminarGasto(Charge gastoSelect) throws AcountDAOException, IOException{
        Acount acount = Acount.getInstance();
        acount.removeCharge(gastoSelect);
    }
        
}

package javafxmlapplication;

import com.itextpdf.io.font.constants.StandardFonts;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Acount;
import model.Category;
import model.Charge;
import model.AcountDAOException;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import javafx.scene.control.ButtonType;
import javafxmlapplication.CompararMeses.CompararMesesController;
import javafxmlapplication.CrearGasto.CrearGastoController;
import javafxmlapplication.DesgloseDeGastos.DesgloseDeGastosController;
import javafxmlapplication.creacionCategoria.CreacionCategoriaController;
import javafxmlapplication.mostrarGastos.MostrarGastosController;

public class PantallaDeInicioController implements Initializable {

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
    private TableColumn<Category, String> column_name;
    @FXML
    private TableColumn<Category, String> column_description;
    @FXML
    private TableColumn<Category, Double> column_price;
    @FXML
    private ScrollPane scrollpane_category;
    @FXML
    private TableView<Category> tableview_category;

    private Stage stage;
    private Stage loginStage;
    private ObservableList<Category> categoryList;
    @FXML
    private ImageView imagendeperfil;
    @FXML
    private Button imprimir_button;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setStageLogin(Stage loginStage) {
        this.loginStage = loginStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshCategoryList();

        show_mensual_cost.setOnAction(event -> handleShowMensualCost(event));
        compare_month.setOnAction(event -> handleCompareMonth(event));
        show_profile.setOnAction(event -> {
            try {
                handleShowProfile(event);
            } catch (AcountDAOException ex) {
                Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        log_out.setOnAction(event -> handleLogOut(event));
        add_cost.setOnAction(event -> handleAddCost(event));
        show_category.setOnAction(event -> handleShowGastosDeCategoria(event));
        add_category.setOnAction(event -> handleAddCategory(event));
        modify_category.setOnAction(event -> handleModifyCategory(event));
        delete_category.setOnAction(event -> handleDeleteCategory(event));
        imprimir_button.setOnAction(event -> handlePrintButton(event));

        // Inicialmente deshabilitar los botones
        updateButtonStates();

        // Añadir listener a la selección de la tabla
        tableview_category.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
    }

    private double calculateTotalPrice(Category category) {
        double totalPrice = 0.0;
        try {
            List<Charge> charges = Acount.getInstance().getUserCharges();
            for (Charge charge : charges) {
                if (charge.getCategory().equals(category)) {
                    totalPrice += charge.getCost() * charge.getUnits();
                }
            }
        } catch (AcountDAOException | IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return totalPrice;
    }

    private void updateButtonStates() {
        boolean categorySelected = tableview_category.getSelectionModel().getSelectedItem() != null;
        add_cost.setDisable(!categorySelected);
        show_category.setDisable(!categorySelected);
        modify_category.setDisable(!categorySelected);
        delete_category.setDisable(!categorySelected);
    }

    private void handleLogOut(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cierre de sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Estas seguro que quieres cerrar la sesion?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Acount.getInstance().logOutUser();
                    if (stage != null) {
                        stage.close();
                    }
                    if (loginStage != null) {
                        loginStage.show();
                    }
                } catch (IOException e) {

                } catch (AcountDAOException ex) {
                    Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void handleShowMensualCost(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/mostrarGastos/mostrarGastos.fxml"));
            Parent newSceneParent = loader.load();

            // Obtener el controlador de la nueva escena
            MostrarGastosController controller = loader.getController();

            // Configurar las propiedades necesarias en el controlador
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            double currentWidth = currentStage.getWidth();
            double currentHeight = currentStage.getHeight();
            controller.setStage(currentStage, currentWidth, currentHeight);
            controller.setLoginStage(loginStage); // Asegurarse de pasar loginStage al nuevo controlador

            // Cambiar a la nueva escena
            Scene newScene = new Scene(newSceneParent);
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void handleCompareMonth(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CompararMeses/CompararMeses.fxml"));
            Parent newSceneParent = loader.load();

            // Obtener el controlador de la nueva escena
            CompararMesesController controller = loader.getController();

            // Configurar las propiedades necesarias en el controlador
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            double currentWidth = currentStage.getWidth();
            double currentHeight = currentStage.getHeight();
            controller.setStage(currentStage, currentWidth, currentHeight);
            controller.setLoginStage(loginStage); // Asegurarse de pasar loginStage al nuevo controlador

            // Cambiar a la nueva escena
            Scene newScene = new Scene(newSceneParent);
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void handleShowProfile(ActionEvent event) throws AcountDAOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CrearCuenta.fxml"));
            Parent root = loader.load();

            CrearCuentaController controller = loader.getController();
            controller.setLoginStage(loginStage);
            controller.setEditProfileMode(); // Método nuevo para configurar la pantalla para editar perfil

            Stage stage = new Stage();
            stage.setTitle("Modificar Perfil");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleAddCost(ActionEvent event) {
        Category selectedCategory = tableview_category.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/CrearGasto/CrearGasto.fxml"));
                Parent root = loader.load();

                CrearGastoController controller = loader.getController();
                controller.setPantallaDeInicioController(this); // Pasar referencia del controlador
                Stage newStage = new Stage();
                newStage.setTitle("Añadir Gasto");
                newStage.setScene(new Scene(root));
                controller.setStage(newStage);
                controller.setCategory(selectedCategory);
                controller.setChargeList(FXCollections.observableArrayList(Acount.getInstance().getUserCharges()));
                newStage.show();
            } catch (IOException | AcountDAOException ex) {
                Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Añadir Gasto");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una categoría para añadir el gasto.");
            alert.showAndWait();
        }
    }

    private void handleShowGastosDeCategoria(ActionEvent event) {
        Category selectedCategory = tableview_category.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/DesgloseDeGastos/DesgloseDeGastos.fxml"));
                Parent newSceneParent = loader.load();
                DesgloseDeGastosController controller = loader.getController();
                Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                double currentWidth = currentStage.getWidth();
                double currentHeight = currentStage.getHeight();
                controller.setStage(currentStage, currentWidth, currentHeight);
                controller.setLoginStage(loginStage);
                controller.setCategory(selectedCategory);
                Scene newScene = new Scene(newSceneParent);
                currentStage.setScene(newScene);
                currentStage.show();

            } catch (IOException | AcountDAOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Visualizar Gastos de Categoría");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una categoría para ver sus gastos.");
            alert.showAndWait();
        }
    }

    private void handleAddCategory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/creacionCategoria/CreacionCategoria.fxml"));
            Parent root = loader.load();

            CreacionCategoriaController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Crear Categoría");
            newStage.setScene(new Scene(root));
            controller.setStage(newStage);
            controller.setCategoryList(categoryList);

            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleModifyCategory(ActionEvent event) {
        Category selectedCategory = tableview_category.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxmlapplication/creacionCategoria/CreacionCategoria.fxml"));
                Parent root = loader.load();

                CreacionCategoriaController controller = loader.getController();
                Stage newStage = new Stage();
                newStage.setTitle("Modificar Categoría");
                newStage.setScene(new Scene(root));
                controller.setStage(newStage);
                controller.setCategoryList(categoryList);
                controller.loadCategoryData(selectedCategory);

                newStage.show();
            } catch (IOException ex) {
                Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modificar Categoría");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una categoría para modificar.");
            alert.showAndWait();
        }
    }

    private void handleDeleteCategory(ActionEvent event) {
        Category selectedCategory = tableview_category.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Categoría");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro que quieres eliminar la categoría seleccionada?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        boolean success = Acount.getInstance().removeCategory(selectedCategory);
                        if (success) {
                            categoryList.remove(selectedCategory);
                        } else {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText("Error al eliminar la categoría");
                            errorAlert.setContentText("No se pudo eliminar la categoría. Inténtalo de nuevo.");
                            errorAlert.showAndWait();
                        }
                    } catch (AcountDAOException | IOException e) {
                        Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Eliminar Categoría");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una categoría para eliminar.");
            alert.showAndWait();
        }
    }

    public void refreshCategoryList() {
        try {
            categoryList = FXCollections.observableArrayList(Acount.getInstance().getUserCategories());
            tableview_category.setItems(categoryList);
        } catch (AcountDAOException | IOException ex) {
            Logger.getLogger(PantallaDeInicioController.class.getName()).log(Level.SEVERE, null, ex);
        }

        column_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        column_description.setCellValueFactory(new PropertyValueFactory<>("description"));

        column_price.setCellFactory((TableColumn<Category, Double> param) -> new TableCell<Category, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Category category = getTableView().getItems().get(getIndex());
                    setText(String.format("%.2f", calculateTotalPrice(category)));
                }
            }
        });
    }

    private void handlePrintButton(ActionEvent event) {
        try {
            List<Charge> charges = Acount.getInstance().getUserCharges();
            String pdfPath = "resumen_gastos.pdf";
            generatePDF(pdfPath, charges);

            // Abrir el PDF en el navegador predeterminado
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().browse(pdfFile.toURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al generar el PDF.");
            alert.showAndWait();
        }
    }

    private void generatePDF(String filePath, List<Charge> charges) {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Fuente Helvetica
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            Paragraph title = new Paragraph("Resumen de Gastos Mensuales")
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(36)
                    .setBold();
            document.add(title);
            document.add(new Paragraph(" "));

            // Table
            float[] columnWidths = {1, 1, 1, 1, 1};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            addTableHeader(table, font);
            addRows(table, charges, font);
            document.add(table);

            // Total Summary
            document.add(new Paragraph(" "));
            Paragraph categorySummaryTitle = new Paragraph("Resumen de Categorías:")
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14);
            document.add(categorySummaryTitle);

            Map<String, Double> categorySums = calculateCategorySums(charges);
            for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
                Paragraph categorySummary = new Paragraph(entry.getKey() + ": " + entry.getValue() + " EUR")
                        .setFont(font)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(categorySummary);
            }

            // Total Expenses
            double totalExpenses = calculateTotalExpenses(charges);
            document.add(new Paragraph(" "));
            Paragraph totalExpensesParagraph = new Paragraph("Gasto Total del Mes: " + totalExpenses + " EUR")
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14);
            document.add(totalExpensesParagraph);

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTableHeader(Table table, PdfFont font) {
        Stream.of("Nombre del Gasto", "Categoría", "Unidad", "Fecha", "Coste")
                .forEach(columnTitle -> {
                    Cell header = new Cell()
                            .add(new Paragraph(columnTitle).setFont(font))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceRgb(211, 211, 211)) // LIGHT_GRAY color
                            .setBold();
                    table.addHeaderCell(header);
                });
    }

    private void addRows(Table table, List<Charge> charges, PdfFont font) {
        for (Charge charge : charges) {
            table.addCell(new Cell().add(new Paragraph(charge.getName()).setFont(font)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(charge.getCategory().getName()).setFont(font)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(charge.getUnits())).setFont(font)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(charge.getDate().toString()).setFont(font)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(charge.getCost())).setFont(font)).setTextAlignment(TextAlignment.CENTER));
        }
    }

    private Map<String, Double> calculateCategorySums(List<Charge> charges) {
        return charges.stream().collect(
                Collectors.groupingBy(c -> c.getCategory().getName(),
                        Collectors.summingDouble(Charge::getCost))
        );
    }

    private double calculateTotalExpenses(List<Charge> charges) {
        return charges.stream().mapToDouble(Charge::getCost).sum();
    }
}

package javafxmlapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXMLApplication extends Application {
    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioDeSesion.fxml"));
        Parent root = loader.load();
        
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        InicioDeSesionController controlador = loader.getController();
        controlador.setStage(stage); // Asegúrate de que el stage se inicializa aquí

        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setRoot(Parent root) {
        scene.setRoot(root);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
package app;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        File fxmlFile = new File("src/view/Login.fxml");
        URL fxmlUrl = fxmlFile.toURI().toURL();

        Parent root = FXMLLoader.load(fxmlUrl);

        Scene scene = new Scene(root);

       scene.getStylesheets().add(
       getClass().getResource("/css/login-style.css").toExternalForm());
        

        stage.setTitle("GHADS");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package org.culpan.railops;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException  {
        primaryStage.setTitle("RailsOps");
        primaryStage.setWidth(607);
        primaryStage.setHeight(461);

        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/main.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            launch();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {
            Datastore.instance.closeDb();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

}


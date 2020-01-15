package org.culpan.railops.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.culpan.railops.dao.RoutesDao;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Route;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private RoutesDao routesDao = new RoutesDao();

    @FXML
    Button btnClick;

    @FXML
    TreeView<String> treeRoutes;

    public void freightCarsClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CarsDialog.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 919, 454);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void locationsClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LocationsDialog.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 397, 391);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void switchList() throws IOException {
        Route route = getSelectedRoute();
        if (route != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SwitchListDialog.fxml"));
            Parent parent = fxmlLoader.load();
            SwitchListDialogController controller = fxmlLoader.getController();
            controller.initialize(route);

            Scene scene = new Scene(parent, 656, 469);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            initialize(null, null);
        }
    }

    public void btnClickClick() throws IOException  {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RailroadsDialog.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 650, 400);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    protected void addOrEditRoute(Route route) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RouteEditDialog.fxml"));
        Parent parent = fxmlLoader.load();
        RouteEditDialogController dialogController = fxmlLoader.<RouteEditDialogController>getController();
        dialogController.initialize(route);

        Scene scene = new Scene(parent, 224, 425);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        initialize(null, null);
    }

    public void addRoute() throws IOException {
        addOrEditRoute(null);
    }

    private Route getSelectedRoute() {
        int index;
        Route route = null;
        TreeItem<String> item = treeRoutes.getSelectionModel().getSelectedItem();
        if (item != null && !item.getValue().isEmpty() && (index = item.getValue().indexOf(" : ")) > -1) {
            String name = item.getValue().substring(0, index);
            return null; // routesDao.find(name);
        }

        return null;
    }

    public void deleteRoute() {
        Route route = getSelectedRoute();
        if (route != null) {
            routesDao.delete(route);
            initialize(null, null);
        }
    }

    public void editRoute() throws IOException {
        Route route = getSelectedRoute();
        if (route != null) addOrEditRoute(route);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
/*        treeRoutes.setShowRoot(false);
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        List<Route> routes = routesDao.load();
        for (Route r : routes) {
            String itemText = r.getName() + " : " + r.getRailroad();
            if (r.getStops().size() > 1) {
                itemText +=  "     ( " + r.getStops().get(0).getName() + " -> " +
                        r.getStops().get(r.getStops().size() - 1).getName() + " )";
                if (r.getSwitchListId() > 0) {
                    itemText += "    [IN PROGRESS]";
                }
            }
            TreeItem<String> routeItem = new TreeItem<>(itemText);

            for (Location l : r.getStops()) {
                routeItem.getChildren().add(new TreeItem<>(l.getName()));
            }

            root.getChildren().add(routeItem);
        }
        treeRoutes.setRoot(root);
        treeRoutes.refresh();*/
    }
}

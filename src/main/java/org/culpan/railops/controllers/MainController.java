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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.culpan.railops.dao.RoutesDao;
import org.culpan.railops.dao.SwitchListDao;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Route;
import org.culpan.railops.model.SwitchList;
import org.culpan.railops.util.ReportGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final RoutesDao routesDao = new RoutesDao();
    private static final SwitchListDao switchListDao = new SwitchListDao();

    @FXML
    Button btnClick;

    @FXML
    TreeView<String> treeRoutes;

    public void reportFreightCars() {
        ReportGenerator reportGenerator = new ReportGenerator();
        ReportGenerator.showReport(new File("test.pdf"), reportGenerator::reportFreightCars);
    }

    public void freightCarsClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CarsDialog.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 919, 454);
        Stage stage = new Stage();
        stage.setTitle("Freight Cars");
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
        stage.setTitle("Locations");
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
            stage.setTitle("Switch List");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            initialize(null, null);
        }
    }

    public void btnRailroads() throws IOException  {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RailroadsDialog.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 747, 400);
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

        Scene scene = new Scene(parent, 224, 470);
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
        Route result = null;
        TreeItem<String> item = treeRoutes.getSelectionModel().getSelectedItem();
        if (item != null && !item.getValue().isEmpty()) {
            int hashIndex = item.getValue().indexOf(" #");
            int colonIndex = item.getValue().indexOf(" : ");
            String name = item.getValue().substring(0, hashIndex);
            String identifier = item.getValue().substring(hashIndex + 2, colonIndex);
            result = routesDao.findByNameAndIdentifier(name, identifier);
            if (result == null) {
                result = routesDao.findByNameAndRailroadName(name, identifier);
            }
        }

        return result;
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
        treeRoutes.setShowRoot(false);
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        List<Route> routes = routesDao.load();
        for (Route r : routes) {
            String itemText = String.format("%s : %s",
                    r.getDisplayName(),
                    r.getRailroad().getMark());
            if (r.getLocations().size() > 1) {
                itemText +=  "     ( " + r.getLocations().get(0).getName() + " -> " +
                        r.getLocations().get(r.getLocations().size() - 1).getName() + " )";
                if (r.getSwitchListId() > 0) {
                    SwitchList switchList = switchListDao.findById(r.getSwitchListId());
                    if (switchList.getStatus() != null && switchList.getStatus().equalsIgnoreCase("started")) {
                        itemText += "    [IN PROGRESS]";
                    }
                }
            }
            TreeItem<String> routeItem = new TreeItem<>(itemText);

            for (Location l : r.getLocations()) {
                routeItem.getChildren().add(new TreeItem<>(l.getName()));
            }

            root.getChildren().add(routeItem);
        }
        treeRoutes.setRoot(root);
        treeRoutes.refresh();
    }
}

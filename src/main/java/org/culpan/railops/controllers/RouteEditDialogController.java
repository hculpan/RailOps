package org.culpan.railops.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.culpan.railops.dao.Datastore;
import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.RoutesDao;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Railroad;
import org.culpan.railops.model.Route;

import java.sql.SQLException;
import java.util.List;

public class RouteEditDialogController {
    private LocationsDao locationsDao = new LocationsDao();
    private RoutesDao routesDao = new RoutesDao();

    private Route route;

    @FXML
    ChoiceBox<String> choiceDestination;

    @FXML
    ListView<String> listStops;

    @FXML
    TextField textName;

    @FXML
    ChoiceBox<String> choiceRailroad;

    ObservableList<String> stops = FXCollections.observableArrayList();

    public void initialize(Route r) {
        this.route = r;
        try {
            List<String> railroads = Datastore.instance.loadRailroadMarks();
            choiceRailroad.setItems(FXCollections.observableList(railroads));
            listStops.setItems(stops);
        } catch (SQLException e) {
            Datastore.instance.logDbError(e);
        }

        choiceRailroad.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String choice = choiceRailroad.getItems().get((Integer) number2);
                List<Location> locations = locationsDao.allLocationsForRailroad(choice);
                if (locations != null) {
                    choiceDestination.getItems().clear();
                    for (Location l : locations) {
                        choiceDestination.getItems().add(l.getName());
                    }
                }
            }
        });

        if (r != null) {
            textName.setText(r.getName());
            choiceRailroad.setValue(r.getRailroad());

            stops.clear();
            for (Location l : r.getStops()) {
                stops.add(l.getName());
            }
        }
    }

    public void addStop() {
        if (choiceDestination.getValue() != null && !choiceDestination.getValue().isEmpty()) {
            stops.add(choiceDestination.getValue());
        }
    }

    public void deleteStop() {
        String item = listStops.getSelectionModel().getSelectedItem();
        if (item != null && !item.isEmpty()) {
            stops.remove(listStops.getSelectionModel().getSelectedIndex());
        }
    }

    public void stopUp() {
        int index = listStops.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            String v1 = stops.get(index - 1);
            String v2 = stops.get(index);
            stops.set(index - 1, v2);
            stops.set(index, v1);
        }
    }

    public void stopDown() {
        int index = listStops.getSelectionModel().getSelectedIndex();
        if (index < listStops.getItems().size() - 1) {
            String v2 = stops.get(index + 1);
            String v1 = stops.get(index);
            stops.set(index + 1, v1);
            stops.set(index, v2);
        }
    }

    public void okClicked(ActionEvent event) {
        if (textName.getText() == null || textName.getText().isEmpty()) return;
        if (choiceRailroad.getValue() == null || choiceRailroad.getValue().isEmpty()) return;

        if (route == null) {
            route = new Route(textName.getText(), choiceRailroad.getValue());
        }

        route.getStops().clear();
        for (String s : stops) {
            Location l = locationsDao.find(s);
            if (!locationsDao.doesRailroadServeLocation(choiceRailroad.getValue(), l)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Stop");
                alert.setHeaderText("You have selected an invalid stop");
                alert.setContentText("Railroad " + choiceRailroad.getValue() + " does not service " + l.getName());
                alert.showAndWait();
                return;
            }
            route.getStops().add(l);
        }

        routesDao.addOrUpdate(route);

        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public void cancelClicked(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }


}
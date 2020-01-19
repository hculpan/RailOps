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
import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.RailroadsDao;
import org.culpan.railops.dao.RoutesDao;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Railroad;
import org.culpan.railops.model.Route;

import java.util.List;
import java.util.stream.Collectors;

public class RouteEditDialogController {
    private final static LocationsDao locationsDao = new LocationsDao();
    private final static RoutesDao routesDao = new RoutesDao();
    private final static RailroadsDao railroadsDao = new RailroadsDao();

    private Route route;

    @FXML
    ChoiceBox<String> choiceDestination;

    @FXML
    ListView<String> listStops;

    @FXML
    TextField textIdentifier;

    @FXML
    TextField textName;

    @FXML
    ChoiceBox<String> choiceRailroad;

    ObservableList<String> stops = FXCollections.observableArrayList();

    public void initialize(Route r) {
        this.route = r;
        List<String> railroads = railroadsDao.load().stream().map(Railroad::getMark).collect(Collectors.toList());
        choiceRailroad.setItems(FXCollections.observableList(railroads));
        listStops.setItems(stops);

        choiceRailroad.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String choice = choiceRailroad.getItems().get((Integer) number2);
                Railroad r = railroadsDao.findByMark(choice);
                if (r == null) return;

                List<Location> locations = locationsDao.loadAllForRailroad(r);
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
            textIdentifier.setText(r.getIdentifier());
            choiceRailroad.setValue(r.getRailroad().getMark());

            stops.clear();
            for (Location l : r.getLocations()) {
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
        if (textIdentifier.getText() == null || textIdentifier.getText().isEmpty()) return;
        if (choiceRailroad.getValue() == null || choiceRailroad.getValue().isEmpty()) return;

        if (route == null) {
            route = new Route();
        }

        route.setName(textName.getText());
        route.setIdentifier(textIdentifier.getText());
        route.setRailroad(railroadsDao.findByMark(choiceRailroad.getValue()));

        route.getLocations().clear();
        for (String s : stops) {
            Location l = locationsDao.findByName(s);
            List<Railroad> railroads = railroadsDao.loadRailroadsByLocation(l);
            if (railroads.stream().filter(r -> r.getMark().equalsIgnoreCase(choiceRailroad.getValue())).count() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Stop");
                alert.setHeaderText("You have selected an invalid stop");
                alert.setContentText("Railroad " + choiceRailroad.getValue() + " does not service " + l.getName());
                alert.showAndWait();
                return;
            }
            route.getLocations().add(l);
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

package org.culpan.railops.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.culpan.railops.dao.CarTypesByLocationDao;
import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.RailroadsDao;
import org.culpan.railops.model.Car;
import org.culpan.railops.model.CarTypesByLocation;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Railroad;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LocationsDialogController  implements Initializable {
    private final static RailroadsDao railroadsDao = new RailroadsDao();
    private final static LocationsDao locationsDao = new LocationsDao();
    private final static CarTypesByLocationDao carTypeByLocationDao = new CarTypesByLocationDao();

    private enum ItemType { itLocation, itCarType, itCarTypeHeader, itRailroad, itRailroadHeader };

    private final static String CARS_SERVICED_HEADER = "Car Types Serviced";

    private final static String RAILROADS_SERVICING = "Railroads Servicing";

    @FXML
    TreeView<String> treeLocations;

    public void closeClicked(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public String getLocationName(TreeItem<String> item) {
        String result = "";
        while (item != null && item.getParent() != null) {
            if (item.getParent() == treeLocations.getRoot()) {
                result = item.getValue();
                break;
            }
            item = item.getParent();
        }
        return result;
    }

    public ItemType getItemType(TreeItem<String> item) {
        if (item != null && item.getParent() != null) {
            if (item.getValue().equals(CARS_SERVICED_HEADER)) {
                return ItemType.itCarTypeHeader;
            } else if (item.getParent().getValue().equals(CARS_SERVICED_HEADER)) {
                return ItemType.itCarType;
            } else if (item.getValue().equals(RAILROADS_SERVICING)) {
                return ItemType.itRailroadHeader;
            } else if (item.getParent().getValue().equals(RAILROADS_SERVICING)) {
                return ItemType.itRailroad;
            }
        }

        return ItemType.itLocation;
    }

    public void add(ActionEvent event) {
        TreeItem<String> item = treeLocations.getSelectionModel().getSelectedItem();

        switch (getItemType(item)) {
            case itLocation:
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("New Location");
                textInputDialog.setHeaderText("Give the name of the new location");
                textInputDialog.setContentText("Location:");
                Optional<String> result = textInputDialog.showAndWait();
                result.ifPresent(name -> {
                        Location location = new Location();
                        location.setName(name);
                        location.setStaging(false);
                        locationsDao.addOrUpdate(location);
                });
                break;
            case itCarTypeHeader:
            case itCarType:
                List<String> carTypes = new ArrayList<>();
                carTypes.addAll(Car.aarCodes);
                carTypes.add("All");
                ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(carTypes.get(0), carTypes);
                choiceDialog.setTitle("New Car Type");
                choiceDialog.setHeaderText("Select a new car type to be serviced at this location");
                choiceDialog.setContentText("Car Type:");
                result = choiceDialog.showAndWait();
                result.ifPresent(aarCode -> {
                    Location l = locationsDao.findByName(getLocationName(item));
                    if (aarCode.equalsIgnoreCase("all")) {
                        for (String carType : Car.aarCodes) {
                            CarTypesByLocation c = new CarTypesByLocation();
                            c.setLocationId(l.getId());
                            c.setAarCode(carType);
                            if (!carTypeByLocationDao.existsForLocation(l, carType)) {
                                carTypeByLocationDao.addOrUpdate(c);
                            }
                        }
                    } else {
                        CarTypesByLocation c = new CarTypesByLocation();
                        c.setLocationId(locationsDao.findByName(getLocationName(item)).getId());
                        c.setAarCode(aarCode);
                        if (!carTypeByLocationDao.existsForLocation(l, aarCode)) {
                            carTypeByLocationDao.addOrUpdate(c);
                        }
                    }
                });
                break;
            case itRailroadHeader:
            case itRailroad:
                List<String> railroads = railroadsDao.load().stream().map(Railroad::getMark).collect(Collectors.toList());
                railroads.add("All");
                choiceDialog = new ChoiceDialog<>((railroads.size() > 0 ? railroads.get(0) : ""), railroads);
                choiceDialog.setTitle("New Railroad Service");
                choiceDialog.setHeaderText("Select a railroad to service this location");
                choiceDialog.setContentText("Railroad:");
                result = choiceDialog.showAndWait();
                String locationName = getLocationName(item);
                result.ifPresent(rr -> {
                    if (rr.equalsIgnoreCase("all")) {
                        for (String railroad : railroads) {
                            if (!railroad.equalsIgnoreCase("all")) {
                                railroadsDao.addRailroadForLocation(railroadsDao.findByMark(railroad), locationsDao.findByName(locationName));
                            }
                        }
                    } else {
                        railroadsDao.addRailroadForLocation(railroadsDao.findByMark(rr), locationsDao.findByName(locationName));
                    }
                });
                break;
        }

        initialize(null, null);
    }

    public void remove(ActionEvent event) {
        TreeItem<String> item = treeLocations.getSelectionModel().getSelectedItem();
        if (item == null) return;

        LocationsDao locationsDao = new LocationsDao();

        switch (getItemType(item)) {
            case itLocation:
                locationsDao.delete(locationsDao.findByName(getLocationName(item)));
                break;
            case itCarType:
                carTypeByLocationDao.deleteForLocation(locationsDao.findByName(getLocationName(item)), item.getValue());
                break;
            case itRailroad:
                railroadsDao.deleteRailroadForlocation(
                        railroadsDao.findByMark(item.getValue()),
                        locationsDao.findByName(getLocationName(item)));
                break;
        }

        initialize(null, null);
    }

    public void treeClicked(MouseEvent event) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        treeLocations.setShowRoot(false);
        TreeItem<String> root = new TreeItem<>("");
        root.setExpanded(true);

        LocationsDao locationsDao = new LocationsDao();
        List<Location> locations = locationsDao.load();
        for (Location location : locations) {
            TreeItem<String> loc = new TreeItem<>(location.getName());
            loc.setExpanded(true);

            TreeItem<String> cars = new TreeItem<>(CARS_SERVICED_HEADER);
            for (CarTypesByLocation s : location.getCarTypesByLocations()) {
                TreeItem<String> i = new TreeItem<>(s.getAarCode());
                cars.getChildren().add(i);
            }
            loc.getChildren().add(cars);

            TreeItem<String> railroads = new TreeItem<>(RAILROADS_SERVICING);
            for (Railroad r : railroadsDao.loadRailroadsByLocation(location)) {
                TreeItem<String> i = new TreeItem<>(r.getMark());
                railroads.getChildren().add(i);
            }
            loc.getChildren().add(railroads);

            root.getChildren().add(loc);
        }
        treeLocations.setRoot(root);

        treeLocations.refresh();
    }
}

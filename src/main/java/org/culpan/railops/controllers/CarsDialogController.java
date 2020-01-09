package org.culpan.railops.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.culpan.railops.dao.CarsDao;
import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.WaybillDao;
import org.culpan.railops.model.Car;
import org.culpan.railops.model.Location;
import org.culpan.railops.model.Waybill;
import org.culpan.railops.model.WaybillStop;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class CarsDialogController implements Initializable {
    private final static ObservableList<String> aarCodesList = FXCollections.observableArrayList(Car.aarCodes);

    private final WaybillDao waybillDao = new WaybillDao();

    @FXML
    TableView<Car> tableCars;

    @FXML
    TextField textKind;

    @FXML
    ChoiceBox<String> aarCodes;

    @FXML
    TextField textRailroad;

    @FXML
    TextField textId;

    @FXML
    TextField textDescription;

    @FXML
    ChoiceBox<String> choiceLocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CarsDao dao = new CarsDao();
        LocationsDao locationsDao = new LocationsDao();

        aarCodes.setItems(aarCodesList);

        ObservableList<Car> data = FXCollections.observableList(dao.load());
        tableCars.setItems(data);
        tableCars.refresh();

        choiceLocation.getItems().clear();
        List<Location> locations = locationsDao.load();
        for (Location l : locations) {
            choiceLocation.getItems().add(l.getName());
        }
        choiceLocation.getItems().add("");
    }

    public void randomLocationsClicked() {
        CarsDao carsDao = new CarsDao();
        LocationsDao locationsDao = new LocationsDao();

        Random rnd = new Random();
        List<Location> locations = locationsDao.load();
        List<Car> cars = carsDao.load();

        for (Car c : cars) {
            Location l;
            do {
                l =  locations.get(rnd.nextInt(locations.size()));
            } while (!validLocation(l, c));
            c.setLocation(l.getName());
            Waybill waybill = waybillDao.findWaybill(c.getId());
            c.setWaybillSequence(-1);
            if (waybill != null && waybill.getStops().size() > 0) {
                WaybillStop stop = waybill.getStops().get(0);
                if (l.getId() == locationsDao.find(stop.getDestination()).getId()) {
                    c.setWaybillSequence(0);
                }
            }
            carsDao.addOrUpdate(c);
        }

        initialize(null, null);
    }

    private boolean validLocation(Location l, Car c) {
        boolean result = true;

        try {
            Waybill waybill = waybillDao.findWaybill(c.getId());
            if (waybill != null && waybill.getStops().size() > 0) {
                String firstRailroad = waybill.getStops().get(0).getRouting();
                List<String> railroads = Datastore.instance.loadLocationRailroads(l.getName());
                result = (railroads.indexOf(firstRailroad) > -1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void closeClicked(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public void waybillResetClicked(ActionEvent event) {
        Car c = tableCars.getSelectionModel().getSelectedItem();
        if (c != null) {
            CarsDao dao = new CarsDao();
            c.setWaybillSequence(-1);
            dao.addOrUpdate(c);
        }
    }

    public void itemSelected(MouseEvent event) {
        Car c = tableCars.getSelectionModel().getSelectedItem();
        if (c != null) {
            textKind.setText(c.getKind());
            aarCodes.setValue(c.getAarCode());
            textRailroad.setText(c.getMark());
            textId.setText(c.getId());
            textDescription.setText(c.getDescription());
            choiceLocation.setValue(c.getLocation());
        }
    }

    public void waybillClicked(MouseEvent event) {
        Car c = tableCars.getSelectionModel().getSelectedItem();
        if (c != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WaybillsEntryDialog.fxml"));
                Parent parent = fxmlLoader.load();
                WaybillEntryDialogController dialogController = fxmlLoader.<WaybillEntryDialogController>getController();
                dialogController.textAarCode.setText(c.getAarCode());
                dialogController.textCarId.setText(c.getId());
                dialogController.initialize();

                Scene scene = new Scene(parent, 600, 429);
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }

            initialize(null, null);
        }
    }

    public void addCar(ActionEvent event) {
        if (textId.getText() != null && !textId.getText().isEmpty()) {
            Car c = new Car(textKind.getText(),
                    textRailroad.getText(),
                    textId.getText(),
                    aarCodes.getValue(),
                    textDescription.getText(),
                    choiceLocation.getValue());
            c.setWaybillSequence(-1);
            CarsDao carsDao = new CarsDao();
            carsDao.addOrUpdate(c);

            initialize(null, null);
        }
    }

    public void removeCar(ActionEvent event) {
        int index = tableCars.getSelectionModel().getSelectedIndex();
        if (index >=0) {
            Car r = tableCars.getItems().get(index);
            CarsDao carsDao = new CarsDao();
            carsDao.delete(r);

            initialize(null, null);
        }
    }
}

package org.culpan.railops.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.culpan.railops.dao.*;
import org.culpan.railops.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class WaybillEntryDialogController {
    private final static WaybillDao waybillDao = new WaybillDao();
    private final static WaybillStopsDao waybillStopsDao = new WaybillStopsDao();
    private final static LocationsDao locationsDao = new LocationsDao();
    private final static RailroadsDao railroadsDao = new RailroadsDao();
    private final static CarTypesByLocationDao carTypesByLocationDao = new CarTypesByLocationDao();
    private final static CarsDao carsDao = new CarsDao();

    @FXML
    public TextField textCarId;

    @FXML
    public TextField textAarCode;

    @FXML TextField textConsignee1;
    @FXML ChoiceBox<String> choiceDestination1;
    @FXML ChoiceBox<String> choiceRouting1;
    @FXML TextField textLading1;
    @FXML TextField textShipper1;
    @FXML TextField textShipperAddress1;

    @FXML TextField textConsignee2;
    @FXML ChoiceBox<String> choiceDestination2;
    @FXML ChoiceBox<String> choiceRouting2;
    @FXML TextField textLading2;
    @FXML TextField textShipper2;
    @FXML TextField textShipperAddress2;

    @FXML TextField textConsignee3;
    @FXML ChoiceBox<String> choiceDestination3;
    @FXML ChoiceBox<String> choiceRouting3;
    @FXML TextField textLading3;
    @FXML TextField textShipper3;
    @FXML TextField textShipperAddress3;

    @FXML TextField textConsignee4;
    @FXML ChoiceBox<String> choiceDestination4;
    @FXML ChoiceBox<String> choiceRouting4;
    @FXML TextField textLading4;
    @FXML TextField textShipper4;
    @FXML TextField textShipperAddress4;

    private List<Railroad> railroads = null;

    private void updateRailroadsChoice(String location, ChoiceBox<String> routing) {
            List<String> servingRailroads = railroadsDao.loadRailroadsByLocation(locationsDao.findByName(location))
                    .stream().map(Railroad::getMark).collect(Collectors.toList());
            routing.getItems().clear();
            routing.getItems().addAll(servingRailroads);
    }

    public void initialize() {
        try {
            if (railroads == null) {
                railroads = railroadsDao.load();
            }

            choiceDestination1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    updateRailroadsChoice(choiceDestination1.getItems().get((Integer) number2), choiceRouting1);
                }
            });

            choiceDestination2.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    updateRailroadsChoice(choiceDestination2.getItems().get((Integer) number2), choiceRouting2);
                }
            });

            choiceDestination3.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    updateRailroadsChoice(choiceDestination3.getItems().get((Integer) number2), choiceRouting3);
                }
            });

            choiceDestination4.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    updateRailroadsChoice(choiceDestination4.getItems().get((Integer) number2), choiceRouting4);
                }
            });

            List<Location> locations = locationsDao.load();
            for (Location l : locations) {
                if (carTypesByLocationDao.existsForLocation(l, textAarCode.getText())) {
                    choiceDestination1.getItems().add(l.getName());
                    choiceDestination2.getItems().add(l.getName());
                    choiceDestination3.getItems().add(l.getName());
                    choiceDestination4.getItems().add(l.getName());
                }
            }

            if (waybillDao.existsForCarRoadId(textCarId.getText())) {
                loadWaybillData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    public void clearAllClicked(ActionEvent event) {
        textConsignee1.setText("");
        choiceDestination1.setValue("");
        choiceRouting1.setValue("");
        textLading1.setText("");
        textShipper1.setText("");
        textShipperAddress1.setText("");

        textConsignee2.setText("");
        choiceDestination2.setValue("");
        choiceRouting2.setValue("");
        textLading2.setText("");
        textShipper2.setText("");
        textShipperAddress2.setText("");

        textConsignee3.setText("");
        choiceDestination3.setValue("");
        choiceRouting3.setValue("");
        textLading3.setText("");
        textShipper3.setText("");
        textShipperAddress3.setText("");

        textConsignee4.setText("");
        choiceDestination4.setValue("");
        choiceRouting4.setValue("");
        textLading4.setText("");
        textShipper4.setText("");
        textShipperAddress4.setText("");
    }

    @SuppressWarnings("Duplicates")
    private void loadWaybillData() throws SQLException {
        Waybill waybill = waybillDao.findByRoadId(textCarId.getText());

        if (waybill.getStops().size() > 0) {
            WaybillStop stop = waybill.getStops().get(0);
            textConsignee1.setText(stop.getConsignee());
            choiceDestination1.setValue((stop.getLocation() != null ? stop.getLocation().getName() : ""));
            updateRailroadsChoice((stop.getLocation() != null ? stop.getLocation().getName() : ""), choiceRouting1);
            choiceRouting1.setValue(stop.getRouting());
            textLading1.setText(stop.getLading());
            textShipper1.setText(stop.getShipper());
            textShipperAddress1.setText(stop.getShipperAddress());
        }

        if (waybill.getStops().size() > 1) {
            WaybillStop stop = waybill.getStops().get(1);
            textConsignee2.setText(stop.getConsignee());
            choiceDestination2.setValue((stop.getLocation() != null ? stop.getLocation().getName() : ""));
            updateRailroadsChoice((stop.getLocation() != null ? stop.getLocation().getName() : ""), choiceRouting2);
            choiceRouting2.setValue(stop.getRouting());
            textLading2.setText(stop.getLading());
            textShipper2.setText(stop.getShipper());
            textShipperAddress2.setText(stop.getShipperAddress());
        }

        if (waybill.getStops().size() > 2) {
            WaybillStop stop = waybill.getStops().get(2);
            textConsignee3.setText(stop.getConsignee());
            choiceDestination3.setValue((stop.getLocation() != null ? stop.getLocation().getName() : ""));
            updateRailroadsChoice((stop.getLocation() != null ? stop.getLocation().getName() : null), choiceRouting3);
            choiceRouting3.setValue(stop.getRouting());
            textLading3.setText(stop.getLading());
            textShipper3.setText(stop.getShipper());
            textShipperAddress3.setText(stop.getShipperAddress());
        }

        if (waybill.getStops().size() > 3) {
            WaybillStop stop = waybill.getStops().get(3);
            textConsignee4.setText(stop.getConsignee());
            choiceDestination4.setValue((stop.getLocation() != null ? stop.getLocation().getName() : ""));
            updateRailroadsChoice((stop.getLocation() != null ? stop.getLocation().getName() : null), choiceRouting4);
            choiceRouting4.setValue(stop.getRouting());
            textLading4.setText(stop.getLading());
            textShipper4.setText(stop.getShipper());
            textShipperAddress4.setText(stop.getShipperAddress());
        }
    }

    private WaybillStop createStop(String consignee, String dest, String routing,
                                   String lading, String shipper, String shipperAddress, int sequence) {
        if (dest != null && !dest.isEmpty() && routing != null && !routing.isEmpty()) {
            LocationsDao locationsDao = new LocationsDao();
            WaybillStop stop1 = new WaybillStop();
            stop1.setConsignee(consignee);
            stop1.setLocation(locationsDao.findByName(dest));
            stop1.setRouting(routing);
            stop1.setLading(lading);
            stop1.setShipper(shipper);
            stop1.setShipperAddress(shipperAddress);
            stop1.setSequence(sequence);
            return stop1;
        }

        return null;
    }

    public void okClicked(ActionEvent event) {
        Waybill waybill = waybillDao.findByRoadId(textCarId.getText());
        Car c = carsDao.findByRoadId(textCarId.getText());

        WaybillStop stop1 = createStop(textConsignee1.getText(),
            choiceDestination1.getValue(),
            choiceRouting1.getValue(),
            textLading1.getText(),
            textShipper1.getText(),
            textShipperAddress1.getText(), 1);

        WaybillStop stop2 = createStop(textConsignee2.getText(),
                choiceDestination2.getValue(),
                choiceRouting2.getValue(),
                textLading2.getText(),
                textShipper2.getText(),
                textShipperAddress2.getText(), 2);

        WaybillStop stop3 = createStop(textConsignee3.getText(),
                choiceDestination3.getValue(),
                choiceRouting3.getValue(),
                textLading3.getText(),
                textShipper3.getText(),
                textShipperAddress3.getText(), 3);

        WaybillStop stop4 = createStop(textConsignee4.getText(),
                choiceDestination4.getValue(),
                choiceRouting4.getValue(),
                textLading4.getText(),
                textShipper4.getText(),
                textShipperAddress4.getText(), 4);

        if (waybill == null) {
            waybill = new Waybill();
            if (c != null) waybill.setCarId(c.getId());
        } else {
            waybillStopsDao.deleteAllForWaybill(waybill);
        }

        waybill.getStops().clear();
        if (stop1 != null) waybill.getStops().add(stop1);
        if (stop2 != null) waybill.getStops().add(stop2);
        if (stop3 != null) waybill.getStops().add(stop3);
        if (stop4 != null) waybill.getStops().add(stop4);

        if (waybill.getStops().size() > 0) {
            waybillDao.addOrUpdate(waybill);
            c.setWaybill(waybill);
            carsDao.addOrUpdate(c);
        } else {
            waybillDao.delete(waybill);
            c.setWaybill(null);
            c.setWaybillId(0);
            carsDao.addOrUpdate(c);
        }

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

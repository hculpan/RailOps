package org.culpan.railops.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.culpan.railops.dao.CarsDao;
import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.RailroadsDao;
import org.culpan.railops.dao.WaybillDao;
import org.culpan.railops.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CarsDialogController implements Initializable {
    interface CheckBoxColumn {
        void action(Boolean newValue, Car c);
    }

    private final static ObservableList<String> aarCodesList = FXCollections.observableArrayList(Car.aarCodes);

    private final static WaybillDao waybillDao = new WaybillDao();
    private final static LocationsDao locationsDao = new LocationsDao();
    private final static CarsDao carsDao = new CarsDao();
    private final static RailroadsDao railroadDao = new RailroadsDao();

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
        aarCodes.setItems(aarCodesList);

        ObservableList<Car> data = FXCollections.observableList(carsDao.load());
        tableCars.setItems(data);
        tableCars.refresh();

        TableColumn<Car, String> col = new TableColumn<>("Location");
        col.setPrefWidth(200);
        tableCars.getColumns().set(4, col);
        col.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLocation() != null) return new SimpleStringProperty(cellData.getValue().getLocation().getName());
            else return null;
        });

        TableColumn<Car, CheckBox> checkBoxColumn = new TableColumn<>("Waybill");
        checkBoxColumn.setPrefWidth(50);
        checkBoxColumn.setStyle("-fx-alignment: CENTER;");
        checkBoxColumn.setCellFactory(column -> {
            CheckBoxTableCell<Car, CheckBox> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(i -> new SimpleBooleanProperty(tableCars.getItems().get(i).hasWaybill()));
            return cell;
        });
        tableCars.getColumns().add(checkBoxColumn);

        choiceLocation.getItems().clear();
        List<Location> locations = locationsDao.load();
        for (Location l : locations) {
            choiceLocation.getItems().add(l.getName());
        }
        choiceLocation.getItems().add("");
    }

    private Callback<TableColumn.CellDataFeatures<Car, CheckBox>, ObservableValue<CheckBox>> getCheckboxCellFactory(
            Function<Car, Boolean> checked,
            CheckBoxColumn action) {
        return arg0 -> {
            Car c = arg0.getValue();

            CheckBox checkBox = new CheckBox();

            checkBox.selectedProperty().setValue(checked.apply(c));

            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> action.action(new_val, c));

            return new SimpleObjectProperty<>(checkBox);
        };
    }

    public void randomLocationsClicked() {
        CarsDao carsDao = new CarsDao();
        LocationsDao locationsDao = new LocationsDao();

        Random rnd = new Random();
        List<Location> locations = locationsDao.load();
        List<Car> cars = carsDao.load();

        for (Car c : cars) {
            if (!c.hasWaybill()) continue;

            Location l;
            do {
                l =  locations.get(rnd.nextInt(locations.size()));
            } while (!validLocation(l, c));

            c.setLocation(locationsDao.findByName(l.getName()));
            Waybill waybill = c.getWaybill();
            c.setWaybillSequence(-1);
            if (waybill != null && waybill.getStops().size() > 0) {
                WaybillStop stop = waybill.getStops().get(0);
                if (l.getId() == locationsDao.findById(stop.getLocation().getId()).getId()) {
                    c.setWaybillSequence(0);
                }
            }
            carsDao.addOrUpdate(c);
        }

        initialize(null, null);
    }

    private boolean validLocation(Location l, Car c) {
        boolean result = true;

        Waybill waybill = c.getWaybill();
        if (waybill != null && waybill.getStops().size() > 0) {
            String firstRailroad = waybill.getStops().get(0).getRouting();
            List<String> railroads = railroadDao.loadRailroadsByLocation(locationsDao.findByName(l.getName()))
                    .stream().map(Railroad::getMark).collect(Collectors.toList());
            result = (railroads.indexOf(firstRailroad) > -1);
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
            textRailroad.setText(c.getRoadMark());
            textId.setText(c.getRoadId());
            textDescription.setText(c.getDescription());
            choiceLocation.setValue((c.getLocation() != null ? c.getLocation().getName() : ""));
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
                dialogController.textCarId.setText(c.getRoadId());
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
            Car c = new Car();
            c.setKind(textKind.getText());
            c.setRoadId(textId.getText());
            c.setAarCode(aarCodes.getValue());
            c.setDescription(textDescription.getText());
            c.setRoadMark(textRailroad.getText());
            c.setLocation(locationsDao.findByName(choiceLocation.getValue()));
            c.setWaybillSequence(-1);
            CarsDao carsDao = new CarsDao();
            carsDao.addOrUpdate(c);

            textKind.setText("");
            textId.setText("");
            aarCodes.setValue(null);
            textDescription.setText("");
            textRailroad.setText("");

            initialize(null, null);
        }
    }

    public void removeCar(ActionEvent event) {
        int index = tableCars.getSelectionModel().getSelectedIndex();
        if (index >=0) {
            Car r = tableCars.getItems().get(index);
            carsDao.delete(r);

            initialize(null, null);
        }
    }
}

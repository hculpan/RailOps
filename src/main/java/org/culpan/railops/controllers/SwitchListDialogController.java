package org.culpan.railops.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.culpan.railops.dao.*;
import org.culpan.railops.model.*;

import java.util.ArrayList;
import java.util.List;

public class SwitchListDialogController {
    private final static CarsDao carsDao = new CarsDao();
    private final static SwitchListDao switchListDao = new SwitchListDao();
    private final static RoutesDao routesDao = new RoutesDao();
    private final static LocationsDao locationsDao = new LocationsDao();
    private final static WaybillDao waybillDao = new WaybillDao();

    public class TableItem {
        private String location;
        private String carType;
        private String railroad;
        private String carNum;
        private String lading;
        private String action;

        public TableItem() {

        }

        public TableItem(Move move, Car c, WaybillStop stop) {
            location = "";
            carType = c.getAarCode();
            carNum = c.getRoadId();
            lading = "";
            // lading = stop.getLading();
            action = move.getAction();
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public String getRailroad() {
            return railroad;
        }

        public void setRailroad(String railroad) {
            this.railroad = railroad;
        }

        public String getCarNum() {
            return carNum;
        }

        public void setCarNum(String carNum) {
            this.carNum = carNum;
        }

        public String getLading() {
            return lading;
        }

        public void setLading(String lading) {
            this.lading = lading;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    private Route route;

    private List<Move> moves;

    @FXML
    TableView<TableItem> tableStops;

    @FXML
    Button btnProgress;

    @FXML
    Button btnRollback;

    public void closeClicked(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public void progressClicked(ActionEvent event) {
        if (btnProgress.getText().equalsIgnoreCase("start")) {
            startSwitchList();
        } else {
            doneSwitchList();
        }
    }

    public void rollbackClicked() {
/*        SwitchList switchList = switchListDao.find(route.getSwitchListId());
        if (switchList != null) {
            switchList.setStatus("ABORTED");
            switchListDao.addOrUpdate(switchList);
            route.setSwitchListId(0);
            routesDao.addOrUpdate(route);
            for (Move m : switchList.getMoves()) {
                carsDao.updateSwitching(m.getCarId(), false);
            }
            initialize(route);
        }*/
    }

    public void startSwitchList() {
        SwitchList switchList = new SwitchList();
        switchList.setRouteId(route.getId());
        switchList.setStatus("STARTED");
        switchList.getMoves().addAll(moves);
        switchListDao.addOrUpdate(switchList);
        route.setSwitchListId(switchList.getId());
        routesDao.addOrUpdate(route);
        for (Move m : switchList.getMoves()) {
//            carsDao.updateSwitching(m.getCarId(), true);
        }
        initialize(route);
    }

    public void doneSwitchList() {
/*        SwitchList switchList = switchListDao.find(route.getSwitchListId());
        if (switchList != null) {
            switchList.setStatus("DONE");
            switchListDao.addOrUpdate(switchList);
            route.setSwitchListId(0);
            routesDao.addOrUpdate(route);
            for (Move m : switchList.getMoves()) {
                if (m.getMoveType() == Move.MoveType.mtSetOut) {
                    carsDao.moveCarTo(m.getCarId(), m.getLocationId(), true);
                }
            }
            initialize(route);
        }*/
    }

    public List<Move> buildListOfMoves(Route route) {
/*        if (route.getStops().size() == 0) return new ArrayList<>();

        List<Move> moves = new ArrayList<>();

        // First move - pick up train
        {
            Move move = new Move();
            move.setRouteName(route.getName());
            move.setLocationId(route.getStops().get(0).getId());
            move.setMove(Move.MoveType.mtPickupLocomotive);
            moves.add(move);
        }

        List<Car> carsInTrain = new ArrayList<>();
        for (int i = 0; i < route.getStops().size(); i++) {
            List<Car> carsToPickup = carsToPickup(route, i);
            List<Car> carsToSetout = carsToSetout(carsInTrain, route.getStops().get(i));
*/
// Re-enable to debug
/*
            System.out.println("----Stop: " + route.getStops().get(i).getName());

            for (Car c : carsInTrain) {
                System.out.println("  Car in train: " + c.getId());
            }

            for (Car c : carsToSetout) {
                System.out.println("  Car to set out: " + c.getId());
            }

            for (Car c : carsToPickup) {
                System.out.println("  Car to pick up: " + c.getId());
            }
*/
/*
            for (Car carToSetout : carsToSetout) {
                int index = carsInTrain.indexOf(carToSetout);
                if (i > -1) {
                    carsInTrain.remove(index);
                    Move move = new Move();
                    move.setRouteName(route.getName());
                    move.setLocationId(route.getStops().get(i).getId());
                    move.setCarId(carToSetout.getRoadId());
                    move.setMove(Move.MoveType.mtSetOut);
                    moves.add(move);
                }
            }

            for (Car c : carsToPickup) {
                Move move = new Move();
                move.setRouteName(route.getName());
                move.setLocationId(route.getStops().get(i).getId());
                move.setCarId(c.getRoadId());
                move.setMove(Move.MoveType.mtPickUp);
                moves.add(move);
            }
            carsInTrain.addAll(carsToPickup);
        }

        if (carsInTrain.size() != 0) {
            for (Car c : carsInTrain) {
                System.out.println("Stray car: " + c.getId());
            }
        }

        // First move - pick up train
        {
            Move move = new Move();
            move.setRouteName(route.getName());
            move.setLocationId(route.getStops().get(route.getStops().size() - 1).getId());
            move.setMove(Move.MoveType.mtDropoffLocmotive);
            moves.add(move);
        }


        return moves;*/

        return null;
    }

    protected int getNextWaybillSequence(Car car, Waybill waybill) {
        int result = -1;
        if (car.getWaybillSequence() < waybill.getStops().size() - 1) {
            result = car.getWaybillSequence() + 1;
        } else if (car.getWaybillSequence() == waybill.getStops().size() - 1) {
            result = 0;
        }
        return result;
    }

    protected List<Car> carsToSetout(List<Car> train, Location l) {
        List<Car> result = new ArrayList<>();

        for (Car c : train) {
/*            Waybill waybill = waybillDao.findWaybill(c.getRoadId());
            int nextWaybillSequence = getNextWaybillSequence(c, waybill);
            WaybillStop stop = waybill.getStops().get(nextWaybillSequence);

            if (l.getId() == stop.getLocation().getId()) {
                result.add(c);
            }*/
        }

        return result;
    }

    protected List<Car> carsToPickup(Route route, int locationIndex) {
/*        List<Car> result = new ArrayList<>();

        Location l = route.getStops().get(locationIndex);
        List<Car> cars = carsDao.carsAtLocation(l);

        for (Car c : cars) {
            if (c.isSwitching()) continue;

            Waybill waybill = waybillDao.findWaybill(c.getRoadId());
            int nextWaybillSequence = getNextWaybillSequence(c, waybill);
            WaybillStop stop = waybill.getStops().get(nextWaybillSequence);

            if (stop != null && stop.getRouting() != null && stop.getRouting().equalsIgnoreCase(route.getRailroad())) {
                for (int i = locationIndex + 1; i < route.getStops().size(); i++) {
                    Location next = route.getStops().get(i);
                    if (stop.getLocation().getId() == next.getId()) {
                        result.add(c);
                        break;
                    }
                }
            }
        }

        return result;*/

        return null;
    }

    public void initialize(Route route) {
        this.route = route;

        if (route.getSwitchListId() > 0) {
            btnProgress.setText("Done");
            btnRollback.setVisible(true);
        } else {
            btnProgress.setText("Start");
            btnRollback.setVisible(false);
        }

        if (moves == null && route.getSwitchListId() <= 0) {
            moves = buildListOfMoves(route);
        } else if (moves == null) {
//            SwitchList switchList = switchListDao.find(route.getSwitchListId());
//            moves = switchList.getMoves();
        }

        int lastLocId = -1;
        for (Move m : moves) {
            Location l = locationsDao.findById(m.getLocationId());
            if (l.getId() != lastLocId) {
                TableItem tableItem = new TableItem();
                tableItem.setLocation(l.getName());
                lastLocId = l.getId();
                tableStops.getItems().add(tableItem);
            }

            if (m.getMoveType() == Move.MoveType.mtPickUp || m.getMoveType() == Move.MoveType.mtSetOut) {
                Car c = carsDao.find(String.format("car_id = '%s'", m.getCarId())).get(0);
                if (l != null && c != null) {
                    TableItem tableItem = new TableItem(m, c, null);
                    tableStops.getItems().add(tableItem);
                }
            } else {
                TableItem tableItem = new TableItem();
                tableItem.setAction(m.getAction());
                tableStops.getItems().add(tableItem);
            }
        }
    }
}

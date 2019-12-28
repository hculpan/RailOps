package org.culpan.railops.model;

import javafx.beans.property.SimpleStringProperty;

public class Move {
    public enum MoveType { mtPickUp, mtSetOut, mtPickupLocomotive, mtDropoffLocmotive };

    private int id;

    private int switchListId;

    private int locationId;

    private final SimpleStringProperty routeName = new SimpleStringProperty();
    private final SimpleStringProperty carId = new SimpleStringProperty();
    private final SimpleStringProperty move = new SimpleStringProperty();
    private final SimpleStringProperty lading = new SimpleStringProperty();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName.get();
    }

    public int getSwitchListId() {
        return switchListId;
    }

    public void setSwitchListId(int switchListId) {
        this.switchListId = switchListId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public SimpleStringProperty routeNameProperty() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName.set(routeName);
    }

    public String getCarId() {
        return carId.get();
    }

    public SimpleStringProperty carIdProperty() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId.set(carId);
    }

    public String getMove() {
        return move.get();
    }

    public MoveType getMoveType() {
        if (getMove().equalsIgnoreCase("pick up")) {
            return MoveType.mtPickUp;
        } else if (getMove().equalsIgnoreCase("set out")) {
            return MoveType.mtSetOut;
        } else if (getMove().equalsIgnoreCase("start")) {
            return MoveType.mtPickupLocomotive;
        } else {
            return MoveType.mtDropoffLocmotive;
        }
    }

    public SimpleStringProperty moveProperty() {
        return move;
    }

    public void setMove(String move) {
        this.move.set(move);
    }

    public void setMove(MoveType type) {
        if (type == MoveType.mtPickUp) {
            setMove("Pick up");
        } else if (type == MoveType.mtSetOut) {
            setMove("Set out");
        } else if (type == MoveType.mtPickupLocomotive) {
            setMove("Start");
        } else {
            setMove("End");
        }
    }

    public String getLading() {
        return lading.get();
    }

    public SimpleStringProperty ladingProperty() {
        return lading;
    }

    public void setLading(String lading) {
        this.lading.set(lading);
    }
}

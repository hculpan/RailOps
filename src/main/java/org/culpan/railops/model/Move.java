package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;

public class Move extends BaseModel {
    public enum MoveType { mtPickUp, mtSetOut, mtPickupLocomotive, mtDropoffLocmotive };

    @Column(name = "car_id")
    private int carId;

    private Car car;

    @Column
    private String action;

    @Column(name = "location_id")
    private int locationId;

    private Location location;

    @Column(name = "switch_list_id")
    private int switchListId;

    @Column
    private String lading;

    public MoveType getMoveType() {
        if (getAction().equalsIgnoreCase("pick up")) {
            return MoveType.mtPickUp;
        } else if (getAction().equalsIgnoreCase("set out")) {
            return MoveType.mtSetOut;
        } else if (getAction().equalsIgnoreCase("start")) {
            return MoveType.mtPickupLocomotive;
        } else {
            return MoveType.mtDropoffLocmotive;
        }
    }

    public void setMove(MoveType type) {
        if (type == MoveType.mtPickUp) {
            setAction("Pick up");
        } else if (type == MoveType.mtSetOut) {
            setAction("Set out");
        } else if (type == MoveType.mtPickupLocomotive) {
            setAction("Start");
        } else {
            setAction("End");
        }
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getSwitchListId() {
        return switchListId;
    }

    public void setSwitchListId(int switchListId) {
        this.switchListId = switchListId;
    }

    public String getLading() {
        return lading;
    }

    public void setLading(String lading) {
        this.lading = lading;
    }
}

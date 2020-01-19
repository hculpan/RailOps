package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.Table;

@Table(name = "moves")
public class Move extends BaseModel {
    public enum ActionType { atPickUp, atSetOut, atPickupLocomotive, atDropoffLocmotive };

    @Column(name = "car_id")
    private int carId;

    @Column
    private String action;

    @Column(name = "location_id")
    private int locationId;

    private Location location;

    @Column(name = "switch_list_id")
    private int switchListId;

    @Column
    private String lading;

    public ActionType getActionType() {
        if (getAction().equalsIgnoreCase("pick up")) {
            return ActionType.atPickUp;
        } else if (getAction().equalsIgnoreCase("set out")) {
            return ActionType.atSetOut;
        } else if (getAction().equalsIgnoreCase("start")) {
            return ActionType.atPickupLocomotive;
        } else {
            return ActionType.atDropoffLocmotive;
        }
    }

    public void setAction(ActionType type) {
        if (type == ActionType.atPickUp) {
            setAction("Pick up");
        } else if (type == ActionType.atSetOut) {
            setAction("Set out");
        } else if (type == ActionType.atPickupLocomotive) {
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

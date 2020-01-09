package org.culpan.railops.model;

public class Move extends BaseModel {
    public enum ActionType { atPickUp, atSetOut, atPickupLocomotive, atDropoffLocmotive };

    private int switchListId;
    private int locationId;
    private Car car;
    private String action;
    private String lading;

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

    public String getLading() {
        return lading;
    }

    public void setLading(String lading) {
        this.lading = lading;
    }

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
}

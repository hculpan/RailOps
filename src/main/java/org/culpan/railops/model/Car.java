package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Car extends BaseModel {
    public final static List<String> aarCodes = new ArrayList<>(Arrays.asList(
            "F - Flat",
            "G - Gondola",
            "H - Hopper",
            "R - Reefer",
            "N - Caboose",
            "S - Stock (livestock)",
            "T - Tank",
            "X - Box",
            "MW - Maintenance of way"
    ));

    private String kind;
    private String aarCode;
    private String roadId;
    private String description;
    private int waybillId;
    private int locationId;
    private int waybillSequence = 0;
    private boolean switching = false;

    public Car() {}

    public Car(String kind, String roadId, String aarCode, String description, int locationId) {
        this(kind, roadId, aarCode, description, locationId, 0);
    }

    public Car(String kind, String roadId, String aarCode, String description, int locationId, int waybillSequence) {
        setKind(kind);
        setRoadId(roadId);
        setAarCode(aarCode);
        setDescription(description);
        setLocationId(locationId);
        setWaybillSequence(waybillSequence);
    }

    public static List<String> getAarCodes() {
        return aarCodes;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAarCode() {
        return aarCode;
    }

    public void setAarCode(String aarCode) {
        this.aarCode = aarCode;
    }

    public String getRoadId() {
        return roadId;
    }

    public void setRoadId(String roadId) {
        this.roadId = roadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(int waybillId) {
        this.waybillId = waybillId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getWaybillSequence() {
        return waybillSequence;
    }

    public void setWaybillSequence(int waybillSequence) {
        this.waybillSequence = waybillSequence;
    }

    public boolean isSwitching() {
        return switching;
    }

    public void setSwitching(boolean switching) {
        this.switching = switching;
    }

    public int getSwitchingValue() {
        return (isSwitching() ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return Objects.equals(roadId, car.roadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roadId);
    }
}

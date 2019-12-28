package org.culpan.railops.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Car {
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

    private final SimpleStringProperty kind = new SimpleStringProperty();
    private final SimpleStringProperty mark = new SimpleStringProperty();
    private final SimpleStringProperty aarCode = new SimpleStringProperty();
    private final SimpleStringProperty id = new SimpleStringProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleBooleanProperty waybill = new SimpleBooleanProperty();
    private final SimpleStringProperty location = new SimpleStringProperty();

    private int waybillSequence = 0;
    private boolean switching = false;

    public Car() {}

    public Car(String kind, String mark, String id, String aarCode, String description, String location) {
        this(kind, mark, id, aarCode, description, location, 0);
    }

    public Car(String kind, String mark, String id, String aarCode, String description, String location, int waybillSequence) {
        setKind(kind);
        setMark(mark);
        setId(id);
        setAarCode(aarCode);
        setDescription(description);
        setLocation(location);
        setWaybillSequence(waybillSequence);
    }

    public String getKind() {
        return kind.get();
    }

    public SimpleStringProperty kindProperty() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind.set(kind);
    }

    public String getMark() {
        return mark.get();
    }

    public SimpleStringProperty markProperty() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark.set(mark);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getAarCode() {
        return aarCode.get();
    }

    public SimpleStringProperty aarCodeProperty() {
        return aarCode;
    }

    public void setAarCode(String aar) {
        this.aarCode.set(aar);
    }

    public boolean isWaybill() {
        return waybill.get();
    }

    public SimpleBooleanProperty waybillProperty() {
        return waybill;
    }

    public void setWaybill(boolean waybill) {
        this.waybill.set(waybill);
    }

    public String getLocation() {
        return location.get();
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
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
        return Objects.equals(id, car.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

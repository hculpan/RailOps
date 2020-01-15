package org.culpan.railops.model;

import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.WaybillDao;
import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.OneToOne;
import org.culpan.railops.dao.annotations.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Table(name = "cars")
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

    @Column
    private String kind;

    @Column(name = "road_mark")
    private String roadMark;

    @Column(name = "road_id")
    private String roadId;

    @Column
    private String description;

    @Column
    private String aarCode;

    @Column(name = "location_id")
    private int locationId;

    @OneToOne(fieldName = "locationId", dao = LocationsDao.class)
    private Location location;

    @Column(name = "waybill_sequence")
    private int waybillSequence;

    @Column
    private boolean switching;

    @Column(name = "waybill_id")
    private int waybillId;

    @OneToOne(fieldName = "waybillId", dao = WaybillDao.class)
    private Waybill waybill;

    public Car() {

    }

    public boolean hasWaybill() {
        return waybillId > 0;
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

    public String getRoadMark() {
        return roadMark;
    }

    public void setRoadMark(String roadMark) {
        this.roadMark = roadMark;
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

    public String getAarCode() {
        return aarCode;
    }

    public void setAarCode(String aarCode) {
        this.aarCode = aarCode;
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
        if (location != null) setLocationId(location.getId());
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

    public int getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(int waybillId) {
        this.waybillId = waybillId;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
        if (waybill != null) setWaybillId(waybill.getId());
    }
}

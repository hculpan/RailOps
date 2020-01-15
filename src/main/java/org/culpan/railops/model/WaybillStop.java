package org.culpan.railops.model;

import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.OneToOne;
import org.culpan.railops.dao.annotations.Table;

@Table(name = "waybill_stops")
public class WaybillStop extends BaseModel {
    @Column
    private String consignee;

    @Column
    private String routing;

    @Column
    private String shipper;

    @Column(name = "shipper_address")
    private String shipperAddress;

    @Column
    private String lading;

    @Column(name = "location_id")
    private int locationId;

    @OneToOne(fieldName = "locationId", dao = LocationsDao.class)
    private Location location;

    @Column(name = "waybill_id")
    private int waybillId;

    @Column
    private int sequence;

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getShipperAddress() {
        return shipperAddress;
    }

    public void setShipperAddress(String shipperAddress) {
        this.shipperAddress = shipperAddress;
    }

    public String getLading() {
        return lading;
    }

    public void setLading(String lading) {
        this.lading = lading;
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
        setLocationId(location.getId());
    }

    public int getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(int waybillId) {
        this.waybillId = waybillId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}

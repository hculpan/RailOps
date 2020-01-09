package org.culpan.railops.model;

public class WaybillStop extends BaseModel {
    private int sequence;
    private String consignee;
    private int locationId;
    private String routing;
    private String shipper;
    private String shipper_address;
    private String lading;
    private int waybillId;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
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

    public String getShipper_address() {
        return shipper_address;
    }

    public void setShipper_address(String shipper_address) {
        this.shipper_address = shipper_address;
    }

    public String getLading() {
        return lading;
    }

    public void setLading(String lading) {
        this.lading = lading;
    }

    public int getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(int waybillId) {
        this.waybillId = waybillId;
    }
}

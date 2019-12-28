package org.culpan.railops.model;

public class WaybillStop {
    private int sequence;
    private String consignee;
    private Location location;
    private String routing;
    private String shipper;
    private String shipper_address;
    private String lading;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDestination() {
        return (location != null ? location.getName() : null);
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
}

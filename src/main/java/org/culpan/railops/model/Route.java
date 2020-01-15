package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;

public class Route extends BaseModel {
    @Column
    private String name;

    @Column(name = "railroad_id")
    private int railroadId;

    private Railroad railroad;

    @Column(name = "switch_list_id")
    private int switchListId;

    private SwitchList switchList;

    public Route() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRailroadId() {
        return railroadId;
    }

    public void setRailroadId(int railroadId) {
        this.railroadId = railroadId;
    }

    public Railroad getRailroad() {
        return railroad;
    }

    public void setRailroad(Railroad railroad) {
        this.railroad = railroad;
    }

    public int getSwitchListId() {
        return switchListId;
    }

    public void setSwitchListId(int switchListId) {
        this.switchListId = switchListId;
    }

    public SwitchList getSwitchList() {
        return switchList;
    }

    public void setSwitchList(SwitchList switchList) {
        this.switchList = switchList;
    }
}

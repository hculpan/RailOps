package org.culpan.railops.model;

import org.culpan.railops.dao.RailroadsDao;
import org.culpan.railops.dao.SwitchListDao;
import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.OneToOne;
import org.culpan.railops.dao.annotations.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "routes")
public class Route extends BaseModel {
    @Column
    private String identifier;

    @Column
    private String name;

    @Column(name = "railroad_id")
    private int railroadId;

    @OneToOne(fieldName = "railroadId", dao = RailroadsDao.class)
    private Railroad railroad;

    @Column(name = "switch_list_id")
    private int switchListId;

    @OneToOne(fieldName = "switchListId", dao = SwitchListDao.class)
    private SwitchList switchList;

    private final List<Location> locations = new ArrayList<>();

    public Route() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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
        if (railroad != null) this.railroadId = railroad.getId();
        else this.railroadId = 0;
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
        if (switchList != null) setSwitchListId(switchList.getId());
        else setSwitchListId(0);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getDisplayName() {
        String result;

        if ((getName() == null || getName().isBlank()) && getRailroad() != null) {
            result = String.format("%s #%s",
                    getRailroad().getDisplayName(),
                    getIdentifier());
        } else if (getName() == null || getName().isBlank()) {
            result = String.format("Train #%s",
                    getIdentifier());
        } else {
            result = String.format("%s #%s",
                    getName(),
                    getIdentifier());
        }

        return result;
    }
}

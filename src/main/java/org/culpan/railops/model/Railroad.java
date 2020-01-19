package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.Table;

@Table(name="railroads")
public class Railroad extends BaseModel {
    @Column
    private String mark;

    @Column
    private String name;

    @Column(name = "short_name")
    private String shortName;

    public Railroad() {}

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDisplayName() {
        if (shortName == null || shortName.isBlank()) return name;
        return shortName;
    }
}

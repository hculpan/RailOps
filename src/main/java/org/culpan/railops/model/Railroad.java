package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;
import org.culpan.railops.dao.annotations.Table;

@Table(name="railroads")
public class Railroad extends BaseModel {
    @Column
    private String mark;

    @Column
    private String name;

    public Railroad() {}

    public Railroad(String reportingMark, String name) {
        setMark(reportingMark);
        setName(name);
    }

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
}

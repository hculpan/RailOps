package org.culpan.railops.model;

import javafx.beans.property.SimpleStringProperty;

public class Railroad extends BaseModel {
    private String mark;
    private String name;

    public Railroad() {}

    public Railroad(String reportingMark, String name) {
        this.setMark(reportingMark);
        this.setName(name);
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

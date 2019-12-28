package org.culpan.railops.model;

import javafx.beans.property.SimpleStringProperty;

public class Railroad {
    private final SimpleStringProperty reportingMark = new SimpleStringProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();

    public Railroad() {}

    public Railroad(String reportingMark, String name) {
        this.reportingMark.set(reportingMark);
        this.name.set(name);
    }

    public String getReportingMark() {
        return reportingMark.get();
    }

    public SimpleStringProperty reportingMarkProperty() {
        return reportingMark;
    }

    public void setReportingMark(String reportingMark) {
        this.reportingMark.set(reportingMark);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}

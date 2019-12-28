module RailOps.main {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;

        exports org.culpan.railops.controllers;
        opens org.culpan.railops.controllers to javafx.fxml;
        opens org.culpan.railops.model to javafx.base;
        opens org.culpan.railops to javafx.graphics;
}
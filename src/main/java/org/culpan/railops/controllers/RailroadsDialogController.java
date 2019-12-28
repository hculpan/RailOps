package org.culpan.railops.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.culpan.railops.dao.Datastore;
import org.culpan.railops.model.Railroad;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RailroadsDialogController implements Initializable {
    @FXML
    private TableView<Railroad> tableRailroads;

    @FXML
    private TextField textMark;

    @FXML
    private TextField textName;

    private final ObservableList<Railroad> data = FXCollections.observableArrayList();

    public void itemSelected(MouseEvent t) {
        int index = tableRailroads.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            Railroad r = data.get(index);
            if (r != null) {
                textMark.setText(r.getReportingMark());
                textName.setText(r.getName());
            } else {
                textMark.setText("");
                textName.setText("");
            }
        } else {
            textMark.setText("");
            textName.setText("");
        }
    }

    public void okClicked(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public void addRailroad(ActionEvent event) {
        if (textMark.getText() != null && !textMark.getText().isEmpty()) {
            Railroad r = new Railroad(textMark.getText().trim(), textName.getText());
            try {
                Datastore.instance.addOrUpdate(r);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            initialize(null, null);
        }
    }

    public void deleteRailroad(ActionEvent event) {
        int index = tableRailroads.getSelectionModel().getSelectedIndex();
        if (index >=0) {
            Railroad r = data.get(index);
            try {
                Datastore.instance.delete(r);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            initialize(null, null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Railroad> railroadList = Datastore.instance.loadRailroads();
            data.clear();
            data.addAll(railroadList);
            tableRailroads.setItems(data);
            tableRailroads.refresh();
            textMark.setText("");
            textName.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

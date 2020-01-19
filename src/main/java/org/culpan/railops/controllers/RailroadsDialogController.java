package org.culpan.railops.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.culpan.railops.dao.RailroadsDao;
import org.culpan.railops.model.Railroad;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RailroadsDialogController implements Initializable {
    private final static RailroadsDao railroadsDao = new RailroadsDao();

    @FXML
    private TableView<Railroad> tableRailroads;

    @FXML
    private TextField textMark;

    @FXML
    private TextField textName;

    @FXML
    private TextField textShortName;

    private final ObservableList<Railroad> data = FXCollections.observableArrayList();

    public void itemSelected() {
        int index = tableRailroads.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            Railroad r = data.get(index);
            if (r != null) {
                textMark.setText(r.getMark());
                textName.setText(r.getName());
                textShortName.setText(r.getShortName());
            } else {
                textMark.setText("");
                textName.setText("");
                textShortName.setText("");
            }
        } else {
            textMark.setText("");
            textName.setText("");
            textShortName.setText("");
        }
    }

    public void okClicked(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }

    public void addRailroad(ActionEvent event) {
        if (textMark.getText() != null && !textMark.getText().isEmpty()) {
            Railroad r = new Railroad();
            r.setMark(textMark.getText());
            r.setName(textName.getText());
            r.setShortName(textShortName.getText());
            railroadsDao.addOrUpdate(r);

            initialize(null, null);
        }
    }

    public void deleteRailroad(ActionEvent event) {
        int index = tableRailroads.getSelectionModel().getSelectedIndex();
        if (index >=0) {
            Railroad r = data.get(index);
            railroadsDao.delete(r);

            initialize(null, null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Railroad> railroadList = railroadsDao.load();
        data.clear();
        data.addAll(railroadList);
        tableRailroads.setItems(data);
        tableRailroads.refresh();
        textMark.setText("");
        textName.setText("");
        textShortName.setText("");
    }
}

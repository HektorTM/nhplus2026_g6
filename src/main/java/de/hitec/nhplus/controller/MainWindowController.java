package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import de.hitec.nhplus.utils.AlertUtil;
import de.hitec.nhplus.utils.Session;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainWindowController {

    private static final boolean DEV_MODE = true;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button employeeManagementButton;

    @FXML
    private void initialize() {
        boolean isAdmin = DEV_MODE || Session.isAdmin();

        employeeManagementButton.setVisible(isAdmin);
        employeeManagementButton.setManaged(isAdmin);
    }

    @FXML
    private void handleShowAllPatient(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllPatientView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleShowAllTreatments(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllTreatmentView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleShowEmployeeManagement(ActionEvent event) {
        if (!DEV_MODE && !Session.isAdmin()) {
            AlertUtil.showError(
                    "Zugriff verweigert",
                    "Sie haben keine Berechtigung für die Rollenverwaltung."
            );
            return;
        }

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/EmployeeManagementView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

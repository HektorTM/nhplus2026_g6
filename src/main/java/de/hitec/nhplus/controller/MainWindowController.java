package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    private static final boolean DEV_MODE = false;

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
    private void handleShowAllCaregivers(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Session.clear();

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            VBox pane = loader.load();

            LoginController controller = loader.getController();
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            controller.setStage(stage);

            stage.setScene(new Scene(pane));
            stage.setTitle("NHPlus — Anmeldung");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
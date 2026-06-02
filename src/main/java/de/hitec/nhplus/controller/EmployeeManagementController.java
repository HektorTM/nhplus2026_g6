package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.EmployeeDao;
import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller für die Rollenverwaltung.
 *
 * <p>Admins können hier Mitarbeiter anzeigen, Rollen ändern und Mitarbeiter löschen.
 * Schutzregeln wie "letzter Admin darf nicht gelöscht werden" werden im EmployeeDao geprüft.</p>
 */
public class EmployeeManagementController {

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, Number> idColumn;

    @FXML
    private TableColumn<Employee, String> firstNameColumn;

    @FXML
    private TableColumn<Employee, String> surnameColumn;

    @FXML
    private TableColumn<Employee, String> usernameColumn;

    @FXML
    private TableColumn<Employee, String> phoneColumn;

    @FXML
    private TableColumn<Employee, String> roleColumn;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private Label statusLabel;

    private EmployeeDao employeeDao;
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        employeeDao = DaoFactory.getDaoFactory().createEmployeeDao();

        idColumn.setCellValueFactory(cellData -> cellData.getValue().eidProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));

        employeeTable.setItems(employees);

        loadEmployees();
    }

    /**
     * Lädt alle Mitarbeiter aus der Datenbank in die Tabelle.
     */
    private void loadEmployees() {
        try {
            List<Employee> employeeList = employeeDao.readAll();

            employees.clear();
            employees.addAll(employeeList);

            statusLabel.setText("Mitarbeiter geladen: " + employees.size());
        } catch (SQLException exception) {
            AlertUtil.showError("Datenbankfehler", "Mitarbeiter konnten nicht geladen werden.");
            exception.printStackTrace();
        }
    }

    /**
     * Ändert die Rolle des ausgewählten Mitarbeiters.
     */
    @FXML
    private void handleChangeRole() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        Role selectedRole = roleComboBox.getValue();

        if (selectedEmployee == null) {
            AlertUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Mitarbeiter aus.");
            return;
        }

        if (selectedRole == null) {
            AlertUtil.showWarning("Keine Rolle", "Bitte wählen Sie eine neue Rolle aus.");
            return;
        }

        try {
            employeeDao.updateRole(selectedEmployee.getEid(), selectedRole);
            loadEmployees();

            statusLabel.setText("Rolle wurde geändert.");
        } catch (SQLException exception) {
            AlertUtil.showError("Aktion nicht erlaubt", exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Löscht den ausgewählten Mitarbeiter.
     */
    @FXML
    private void handleDeleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            AlertUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Mitarbeiter aus.");
            return;
        }

        try {
            employeeDao.deleteById(selectedEmployee.getEid());
            loadEmployees();

            statusLabel.setText("Mitarbeiter wurde gelöscht.");
        } catch (SQLException exception) {
            AlertUtil.showError("Aktion nicht erlaubt", exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Aktualisiert die Tabelle.
     */
    @FXML
    private void handleRefresh() {
        loadEmployees();
    }
}
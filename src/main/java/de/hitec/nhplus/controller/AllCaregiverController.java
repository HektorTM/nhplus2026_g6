package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.EmployeeDao;
import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;

/**
 * Controller for the employee overview view.
 *
 * <p>This view is used to display, add, edit and delete employees.
 * It replaces the old caregiver table and uses the shared employee table,
 * which also contains login and role data.</p>
 */
public class AllCaregiverController {

    @FXML private TableView<Employee> tableView;
    @FXML private TableColumn<Employee, Long> columnId;
    @FXML private TableColumn<Employee, String> columnFirstName;
    @FXML private TableColumn<Employee, String> columnSurname;
    @FXML private TableColumn<Employee, String> columnPersonnelNumber;
    @FXML private TableColumn<Employee, String> columnQualification;
    @FXML private TableColumn<Employee, String> columnRole;

    @FXML private Button buttonDelete;
    @FXML private Button buttonAdd;

    @FXML private ComboBox<Role> comboBoxRole;
    @FXML private Button buttonChangeRole;

    @FXML private TextField textFieldFirstName;
    @FXML private TextField textFieldSurname;
    @FXML private TextField textFieldPersonnelNumber;
    @FXML private TextField textFieldQualification;

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private EmployeeDao dao;

    /**
     * Initializes the controller and loads all employees from the database.
     */
    @FXML
    public void initialize() {
        this.dao = DaoFactory.getDaoFactory().createEmployeeDao();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("eid"));

        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnPersonnelNumber.setCellValueFactory(new PropertyValueFactory<>("personnelNumber"));
        this.columnPersonnelNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnQualification.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        this.columnQualification.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        this.tableView.setItems(this.employees);
        this.comboBoxRole.setItems(FXCollections.observableArrayList(Role.values()));
        this.buttonChangeRole.setDisable(true);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    boolean noSelection = newValue == null;
                    this.buttonDelete.setDisable(noSelection);
                    this.buttonChangeRole.setDisable(noSelection);
                }
        );

        this.buttonAdd.setDisable(true);
        this.textFieldFirstName.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
        this.textFieldSurname.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
        this.textFieldPersonnelNumber.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
        this.textFieldQualification.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));

        readAllAndShowInTableView();
    }

    /**
     * Loads all employees and shows them in the table.
     */
    private void readAllAndShowInTableView() {
        this.employees.clear();

        try {
            this.employees.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            AlertUtil.showError("Datenbankfehler", "Mitarbeiter konnten nicht geladen werden.");
            exception.printStackTrace();
        }
    }

    /**
     * Adds a new employee without login data.
     * The EmployeeDao automatically assigns the default role MITARBEITER
     * if an admin already exists.
     */
    @FXML
    public void handleAdd() {
        String firstName = this.textFieldFirstName.getText();
        String surname = this.textFieldSurname.getText();
        String personnelNumber = this.textFieldPersonnelNumber.getText();
        String qualification = this.textFieldQualification.getText();

        Employee employee = new Employee(
                firstName,
                surname,
                personnelNumber,
                qualification,
                null,
                null,
                null,
                null,
                Role.MITARBEITER
        );

        try {
            this.dao.create(employee);
            readAllAndShowInTableView();
            clearTextfields();
        } catch (SQLException exception) {
            AlertUtil.showError("Datenbankfehler", "Mitarbeiter konnte nicht gespeichert werden.");
            exception.printStackTrace();
        }
    }

    /**
     * Deletes the selected employee.
     * Admin protection rules are handled by EmployeeDao.
     */
    @FXML
    public void handleDelete() {
        Employee selectedItem = this.tableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            AlertUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Mitarbeiter aus.");
            return;
        }

        try {
            this.dao.deleteById(selectedItem.getEid());
            this.tableView.getItems().remove(selectedItem);
        } catch (SQLException exception) {
            AlertUtil.showError("Aktion nicht erlaubt", exception.getMessage());
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleChangeRole() {
        Employee selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        Role selectedRole = this.comboBoxRole.getValue();

        if (selectedItem == null) {
            AlertUtil.showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Mitarbeiter aus.");
            return;
        }

        if (selectedRole == null) {
            AlertUtil.showWarning("Keine Rolle", "Bitte wählen Sie zuerst eine Rolle aus.");
            return;
        }

        try {
            this.dao.updateRole(selectedItem.getEid(), selectedRole);
            readAllAndShowInTableView();
        } catch (SQLException exception) {
            AlertUtil.showError("Aktion nicht erlaubt", exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Handles inline editing of the first name column.
     *
     * @param event cell edit event containing the updated value
     */
    @FXML
    public void handleOnEditFirstName(TableColumn.CellEditEvent<Employee, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    /**
     * Handles inline editing of the surname column.
     *
     * @param event cell edit event containing the updated value
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Employee, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    /**
     * Handles inline editing of the personnel number column.
     *
     * @param event cell edit event containing the updated value
     */
    @FXML
    public void handleOnEditPersonnelNumber(TableColumn.CellEditEvent<Employee, String> event) {
        event.getRowValue().setPersonnelNumber(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    /**
     * Handles inline editing of the qualification column.
     *
     * @param event cell edit event containing the updated value
     */
    @FXML
    public void handleOnEditQualification(TableColumn.CellEditEvent<Employee, String> event) {
        event.getRowValue().setQualification(event.getNewValue());
        doUpdate(event.getRowValue());
    }

    /**
     * Updates an employee in the database.
     *
     * @param employee employee with changed values
     */
    private void doUpdate(Employee employee) {
        try {
            this.dao.update(employee);
            readAllAndShowInTableView();
        } catch (SQLException exception) {
            AlertUtil.showError("Datenbankfehler", "Mitarbeiter konnte nicht aktualisiert werden.");
            exception.printStackTrace();
        }
    }

    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldPersonnelNumber.clear();
        this.textFieldQualification.clear();
    }

    private boolean areInputDataValid() {
        return !this.textFieldFirstName.getText().isBlank()
                && !this.textFieldSurname.getText().isBlank()
                && !this.textFieldPersonnelNumber.getText().isBlank()
                && !this.textFieldQualification.getText().isBlank();
    }
}
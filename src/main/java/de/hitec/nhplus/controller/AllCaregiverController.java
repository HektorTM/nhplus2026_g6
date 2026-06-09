package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.sql.SQLException;



/**
 * Controller for the caregiver overview view.
 * Handles displaying, adding, editing, and deleting caregivers.
 */
public class AllCaregiverController {

    @FXML private TableView<Caregiver> tableView;
    @FXML private TableColumn<Caregiver, Long> columnId;
    @FXML private TableColumn<Caregiver, String> columnFirstName;
    @FXML private TableColumn<Caregiver, String> columnSurname;
    @FXML private TableColumn<Caregiver, String> columnPersonnelNumber;
    @FXML private TableColumn<Caregiver, String> columnQualification;
    @FXML private Button buttonDelete;
    @FXML private Button buttonAdd;
    @FXML private TextField textFieldFirstName;
    @FXML private TextField textFieldSurname;
    @FXML private TextField textFieldPersonnelNumber;
    @FXML private TextField textFieldQualification;

    private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();
    private CaregiverDao dao;

    /**
     * Initializes the controller. Called automatically by JavaFX after the FXML fields are injected.
     * Loads all caregivers, configures the table columns, and sets up button state listeners.
     */
    public void initialize() {
        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));

        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnPersonnelNumber.setCellValueFactory(new PropertyValueFactory<>("personnelNumber"));
        this.columnPersonnelNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnQualification.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        this.columnQualification.setCellFactory(TextFieldTableCell.forTableColumn());

        this.tableView.setItems(this.caregivers);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> this.buttonDelete.setDisable(newValue == null)
        );

        this.buttonAdd.setDisable(true);
        this.textFieldFirstName.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
        this.textFieldSurname.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
        this.textFieldPersonnelNumber.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
        this.textFieldQualification.textProperty().addListener((obs, old, newVal) -> buttonAdd.setDisable(!areInputDataValid()));
    }

    private void readAllAndShowInTableView() {
        this.caregivers.clear();
        this.dao = DaoFactory.getDaoFactory().createCaregiverDao();
        try {
            this.caregivers.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Handles the Add button click. Reads input fields, creates a new {@link Caregiver},
     * persists it via the DAO, refreshes the table, and clears the input fields.
     */
    @FXML
    public void handleAdd() {
        String firstName = this.textFieldFirstName.getText();
        String surname = this.textFieldSurname.getText();
        String personnelNumber = this.textFieldPersonnelNumber.getText();
        String qualification = this.textFieldQualification.getText();
        try {
            this.dao.create(new Caregiver(firstName, surname, personnelNumber, qualification));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllAndShowInTableView();
        clearTextfields();
    }

    /**
     * Handles the Delete button click. Deletes the selected caregiver from the database
     * and removes it from the table view.
     */
    @FXML
    public void handleDelete() {
        Caregiver selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                this.dao.deleteById(selectedItem.getCid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Handles inline editing of the first name column.
     *
     * @param event Cell edit event containing the updated value.
     */
    @FXML
    public void handleOnEditFirstName(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        doUpdate(event);
    }

    /**
     * Handles inline editing of the surname column.
     *
     * @param event Cell edit event containing the updated value.
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        doUpdate(event);
    }

    /**
     * Handles inline editing of the personnel number column.
     *
     * @param event Cell edit event containing the updated value.
     */
    @FXML
    public void handleOnEditPersonnelNumber(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setPersonnelNumber(event.getNewValue());
        doUpdate(event);
    }

    /**
     * Handles inline editing of the qualification column.
     *
     * @param event Cell edit event containing the updated value.
     */
    @FXML
    public void handleOnEditQualification(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setQualification(event.getNewValue());
        doUpdate(event);
    }

    private void doUpdate(TableColumn.CellEditEvent<Caregiver, String> event) {
        try {
            this.dao.update(event.getRowValue());
        } catch (SQLException exception) {
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
        return !this.textFieldFirstName.getText().isBlank() &&
                !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldPersonnelNumber.getText().isBlank() &&
                !this.textFieldQualification.getText().isBlank();
    }



}

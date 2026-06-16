package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Employee extends Person {

    private final SimpleLongProperty eid;
    private final SimpleStringProperty personnelNumber;
    private final SimpleStringProperty qualification;
    private final SimpleStringProperty username;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty passwordHash;
    private final SimpleStringProperty salt;
    private final SimpleStringProperty role;

    /**
     * Constructor for new employees who have not yet been saved to the database.
     *
     * @param firstName    first name of the employee
     * @param surname      last name of the employee
     * @param username     username for login
     * @param phoneNumber  phone number of the employee
     * @param passwordHash hashed password value
     * @param salt         salt for password hashing
     * @param role         role of the employee
     */
    public Employee(String firstName, String surname, String personnelNumber, String qualification,
                    String username, String phoneNumber, String passwordHash, String salt, Role role) {
        super(firstName, surname);
        this.eid = new SimpleLongProperty(0);
        this.personnelNumber = new SimpleStringProperty(personnelNumber);
        this.qualification = new SimpleStringProperty(qualification);
        this.username = new SimpleStringProperty(username);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.salt = new SimpleStringProperty(salt);
        this.role = new SimpleStringProperty(role == null ? Role.MITARBEITER.name() : role.name());
    }

    /**
     * Constructor for employees who are already saved in the database.
     *
     * @param eid          employee ID
     * @param firstName    first name of the employee
     * @param surname      last name of the employee
     * @param username     username for login
     * @param phoneNumber  phone number of the employee
     * @param passwordHash hashed password value
     * @param salt         salt for password hashing
     * @param role         role of the employee
     */
    public Employee(long eid, String firstName, String surname, String personnelNumber, String qualification,
                    String username, String phoneNumber, String passwordHash, String salt, Role role) {
        super(firstName, surname);
        this.eid = new SimpleLongProperty(eid);
        this.personnelNumber = new SimpleStringProperty(personnelNumber);
        this.qualification = new SimpleStringProperty(qualification);
        this.username = new SimpleStringProperty(username);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.salt = new SimpleStringProperty(salt);
        this.role = new SimpleStringProperty(role == null ? Role.MITARBEITER.name() : role.name());
    }

    public long getEid() {
        return eid.get();
    }

    public SimpleLongProperty eidProperty() {
        return eid;
    }

    public void setEid(long eid) {
        this.eid.set(eid);
    }

    public String getPersonnelNumber() {
        return personnelNumber.get();
    }

    public SimpleStringProperty personnelNumberProperty() {
        return personnelNumber;
    }

    public void setPersonnelNumber(String personnelNumber) {
        this.personnelNumber.set(personnelNumber);
    }

    public String getQualification() {
        return qualification.get();
    }

    public SimpleStringProperty qualificationProperty() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification.set(qualification);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public SimpleStringProperty passwordHashProperty() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    public String getSalt() {
        return salt.get();
    }

    public SimpleStringProperty saltProperty() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt.set(salt);
    }

    public Role getRole() {
        return Role.fromString(role.get());
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    public void setRole(Role role) {
        this.role.set(role == null ? Role.MITARBEITER.name() : role.name());
    }

    /**
     * Checks whether the employee has admin rights.
     *
     * @return true if the employee has the ADMIN role
     */
    public boolean isAdmin() {
        return getRole() == Role.ADMIN;
    }

    /**
     * Checks whether the employee has administration rights.
     *
     * @return true if the employee is ADMIN or VERWALTUNG
     */
    public boolean canUseAdministrationFunctions() {
        return getRole() == Role.ADMIN || getRole() == Role.VERWALTUNG;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "eid=" + getEid() +
                ", firstName='" + getFirstName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", personnelNumber='" + getPersonnelNumber() + '\'' +
                ", qualification='" + getQualification() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}

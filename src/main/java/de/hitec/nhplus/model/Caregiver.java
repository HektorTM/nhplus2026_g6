package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Caregiver extends Person {
    private SimpleLongProperty cid;
    private final SimpleStringProperty personnelNumber;
    private final SimpleStringProperty qualification;

    public Caregiver(String firstname, String surname, String personnelNumber, String qualification) {
        super(firstname, surname);
        this.cid = new SimpleLongProperty(0);
        this.personnelNumber = new SimpleStringProperty(personnelNumber);
        this.qualification = new SimpleStringProperty(qualification);
    }

    public Caregiver(long cid, String firstName, String surname, String personnelNumber, String qualification) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.personnelNumber = new SimpleStringProperty(personnelNumber);
        this.qualification = new SimpleStringProperty(qualification);
    }

    public long getCid () {
        return cid.get();
    }

    public SimpleLongProperty cidProperty() {
        return cid;
    }

    public String getPersonnelNumber(){
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

    @Override
    public String toString() {
        return "Caregiver" + "\nCID: " + this.cid +
                "\nFirstname: " + this.getFirstName() +
                "\nSurname: " + this.getSurname() +
                "\nPersonnelNumber: " + this.personnelNumber +
                "\nQualification: " + this.qualification + "\n";
    }



}

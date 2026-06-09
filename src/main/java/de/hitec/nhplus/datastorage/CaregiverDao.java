package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import java.sql.*;
import java.util.ArrayList;

public class CaregiverDao extends DaoImp<Caregiver> {

    public CaregiverDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(Caregiver caregiver) {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = "INSERT INTO caregiver (firstname, surname, personnelNumber, qualification) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getFirstName());
            preparedStatement.setString(2, caregiver.getSurname());
            preparedStatement.setString(3, caregiver.getPersonnelNumber());
            preparedStatement.setString(4, caregiver.getQualification());

        } catch (SQLException e) {
            e.printStackTrace();;
        }

        return preparedStatement;

    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = "SELECT * FROM caregiver WHERE cid = ?";
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Caregiver(
                result.getLong("cid"),
                result.getString("firstname"),
                result.getString("surname"),
                result.getString("personnelNumber"),
                result.getString("qualification")
        );
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = "SELECT * FROM caregiver";
            preparedStatement = connection.prepareStatement(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Caregiver> list = new ArrayList<>();
        while (result.next()) {
            Caregiver caregiver = new Caregiver(
                    result.getLong("cid"),
                    result.getString("firstname"),
                    result.getString("surname"),
                    result.getString("personnelNumber"),
                    result.getString("qualification")
            );
            list.add(caregiver);
        }
        return list;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE caregiver SET " +
                    "firstname = ?, " +
                    "surname = ?, " +
                    "personnelNumber = ?, " +
                    "qualification = ? " +
                    "WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getFirstName());
            preparedStatement.setString(2, caregiver.getSurname());
            preparedStatement.setString(3, caregiver.getPersonnelNumber());
            preparedStatement.setString(4, caregiver.getQualification());
            preparedStatement.setLong(5, caregiver.getCid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long cid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM caregiver WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, cid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

}

package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import java.sql.*;
import java.util.ArrayList;

/**
 * Provides database access for {@link Caregiver} objects.
 * Implements CRUD operations using the DAO pattern.
 */
public class CaregiverDao extends DaoImp<Caregiver> {

    /**
     * Constructs a {@code CaregiverDao} and passes the connection to the superclass.
     *
     * @param connection Active database connection.
     */
    public CaregiverDao(Connection connection) {
        super(connection);
    }

    /**
     * Generates a {@code PreparedStatement} to insert a new caregiver into the database.
     *
     * @param caregiver The {@link Caregiver} object to persist.
     * @return {@code PreparedStatement} to insert the given caregiver.
     */
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

    /**
     * Generates a {@code PreparedStatement} to query a caregiver by ID.
     *
     * @param key The caregiver ID to query.
     * @return {@code PreparedStatement} to fetch the caregiver with the given ID.
     */
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

    /**
     * Maps a single {@code ResultSet} row to a {@link Caregiver} object.
     *
     * @param result {@code ResultSet} containing one caregiver row.
     * @return A {@link Caregiver} instance populated with data from the result set.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Generates a {@code PreparedStatement} to query all caregivers.
     *
     * @return {@code PreparedStatement} to fetch all caregivers.
     */
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

    /**
     * Maps all rows of a {@code ResultSet} to an {@code ArrayList} of {@link Caregiver} objects.
     *
     * @param result {@code ResultSet} containing all caregiver rows.
     * @return {@code ArrayList} of all caregivers from the result set.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Generates a {@code PreparedStatement} to update an existing caregiver, identified by their ID.
     *
     * @param caregiver The {@link Caregiver} object with updated values.
     * @return {@code PreparedStatement} to update the given caregiver.
     */
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

    /**
     * Generates a {@code PreparedStatement} to delete a caregiver by their ID.
     *
     * @param cid The ID of the caregiver to delete.
     * @return {@code PreparedStatement} to delete the caregiver with the given ID.
     */
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

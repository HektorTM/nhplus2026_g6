package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * Provides database access for {@link User} objects.
 * Implements CRUD operations using the DAO pattern.
 */
public class UserDao extends DaoImp<User> {

    /**
     * Constructs a {@code UserDao} and passes the connection to the superclass.
     *
     * @param connection Active database connection.
     */
    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Generates a {@code PreparedStatement} to insert a new user into the database.
     *
     * @param user The {@link User} object to persist.
     * @return {@code PreparedStatement} to insert the given user.
     */
    @Override
    protected PreparedStatement getCreateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO user (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setString(4, user.getRole());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a {@code PreparedStatement} to query a user by ID.
     *
     * @param key The user ID to query.
     * @return {@code PreparedStatement} to fetch the user with the given ID.
     */
    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Maps a single {@code ResultSet} row to a {@link User} object.
     *
     * @param result {@code ResultSet} containing one user row.
     * @return A {@link User} instance populated with data from the result set.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    protected User getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new User(
                result.getLong("id"),
                result.getString("username"),
                result.getString("password_hash"),
                result.getString("salt"),
                result.getString("role")
        );
    }

    /**
     * Generates a {@code PreparedStatement} to query all users.
     *
     * @return {@code PreparedStatement} to fetch all users.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Maps all rows of a {@code ResultSet} to an {@code ArrayList} of {@link User} objects.
     *
     * @param result {@code ResultSet} containing all user rows.
     * @return {@code ArrayList} of all users from the result set.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (result.next()) {
            list.add(new User(
                    result.getLong("id"),
                    result.getString("username"),
                    result.getString("password_hash"),
                    result.getString("salt"),
                    result.getString("role")
            ));
        }
        return list;
    }

    /**
     * Generates a {@code PreparedStatement} to update an existing user.
     *
     * @param user The {@link User} object with updated values.
     * @return {@code PreparedStatement} to update the given user.
     */
    @Override
    protected PreparedStatement getUpdateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE user SET username = ?, password_hash = ?, salt = ?, role = ? WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setLong(5, user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a {@code PreparedStatement} to delete a user by their ID.
     *
     * @param key The ID of the user to delete.
     * @return {@code PreparedStatement} to delete the user with the given ID.
     */
    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM user WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The matching {@link User}, or {@code null} if not found.
     * @throws SQLException If a database access error occurs.
     */
    public User findByUsername(String username) throws SQLException {
        final String SQL = "SELECT * FROM user WHERE username = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(SQL)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return getInstanceFromResultSet(result);
                }
            }
        }
        return null;
    }
}

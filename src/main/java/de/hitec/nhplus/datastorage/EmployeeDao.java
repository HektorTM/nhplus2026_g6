package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO class for employee accounts.
 *
 * <p>This class manages the separate table for employees,
 * login credentials, and roles. It also contains protection rules for admins:
 * at least one admin must always remain in the system.</p>
 */
public class EmployeeDao extends DaoImp<Employee> {

    public EmployeeDao(Connection connection) {
        super(connection);
        createTableIfNotExists();
        assignDefaultRoleToEmployeesWithoutRole();
    }

    /**
     * Creates the employee accounts table if it does not already exist.
     */
    private void createTableIfNotExists() {
        final String SQL = """
            CREATE TABLE IF NOT EXISTS employee (
                eid INTEGER PRIMARY KEY AUTOINCREMENT,
                firstname TEXT NOT NULL,
                surname TEXT NOT NULL,
                personnelNumber TEXT UNIQUE,
                qualification TEXT,
                username TEXT UNIQUE,
                phoneNumber TEXT,
                passwordHash TEXT,
                salt TEXT,
                role TEXT NOT NULL DEFAULT 'MITARBEITER'
            )
            """;

        try (Statement statement = this.connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Automatically assigns the default role MITARBEITER to employees without a role.
     */
    private void assignDefaultRoleToEmployeesWithoutRole() {
        final String SQL = """
                UPDATE employee
                SET role = 'MITARBEITER'
                WHERE role IS NULL OR role = ''
                """;

        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Creates a new employee.
     *
     * <p>If no admin exists yet, the first registered employee is automatically
     * made admin. If an admin already exists, the new employee receives the
     * MITARBEITER role automatically.</p>
     *
     * @param employee employee to be saved
     * @throws SQLException if saving fails
     */
    @Override
    public void create(Employee employee) throws SQLException {
        if (!adminExists()) {
            employee.setRole(Role.ADMIN);
        } else {
            employee.setRole(Role.MITARBEITER);
        }

        super.create(employee);
    }

    /**
     * Checks whether at least one admin exists.
     *
     * @return true if at least one admin is present
     * @throws SQLException on database errors
     */
    public boolean adminExists() throws SQLException {
        final String SQL = "SELECT COUNT(*) FROM employee WHERE role = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(SQL)) {
            statement.setString(1, Role.ADMIN.name());

            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getInt(1) > 0;
            }
        }
    }

    /**
     * Counts all admins in the employee database.
     *
     * @return number of admins
     * @throws SQLException on database errors
     */
    public int countAdmins() throws SQLException {
        final String SQL = "SELECT COUNT(*) FROM employee WHERE role = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(SQL)) {
            statement.setString(1, Role.ADMIN.name());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Finds an employee by username.
     *
     * <p>This method is used by the login process.</p>
     *
     * @param username username
     * @return employee or null if no employee was found
     * @throws SQLException on database errors
     */
    public Employee findByUsername(String username) throws SQLException {
        final String SQL = "SELECT * FROM employee WHERE username = ?";

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

    /**
     * Changes the role of an employee.
     *
     * <p>The role of the last admin may not be changed, so that
     * at least one admin always remains in the system.</p>
     *
     * @param employeeId employee ID
     * @param newRole    new role
     * @throws SQLException if the change is not permitted or fails
     */
    public void updateRole(long employeeId, Role newRole) throws SQLException {
        Employee employee = read(employeeId);

        if (employee == null) {
            throw new SQLException("Employee not found.");
        }

        Role currentRole = employee.getRole();

        if (currentRole == Role.ADMIN && newRole != Role.ADMIN && countAdmins() <= 1) {
            throw new SQLException("The role of the last admin cannot be changed.");
        }

        final String SQL = "UPDATE employee SET role = ? WHERE eid = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(SQL)) {
            statement.setString(1, newRole.name());
            statement.setLong(2, employeeId);
            statement.executeUpdate();
        }
    }

    /**
     * Prevents direct deletion of an admin account.
     *
     * <p>An admin must first be changed to Verwaltung or Mitarbeiter.
     * This avoids admins being accidentally deleted directly.</p>
     *
     * @param employeeId employee ID
     * @throws SQLException if the employee is an admin or deletion fails
     */
    @Override
    public void deleteById(long employeeId) throws SQLException {
        Employee employee = read(employeeId);

        if (employee == null) {
            throw new SQLException("Employee not found.");
        }

        if (employee.getRole() == Role.ADMIN) {
            throw new SQLException("An admin cannot be deleted directly. Change the role first.");
        }

        super.deleteById(employeeId);
    }

    @Override
    protected Employee getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Employee(
                result.getLong("eid"),
                result.getString("firstname"),
                result.getString("surname"),
                result.getString("personnelNumber"),
                result.getString("qualification"),
                result.getString("username"),
                result.getString("phoneNumber"),
                result.getString("passwordHash"),
                result.getString("salt"),
                Role.fromString(result.getString("role"))
        );
    }

    @Override
    protected ArrayList<Employee> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Employee> employees = new ArrayList<>();

        while (result.next()) {
            employees.add(getInstanceFromResultSet(result));
        }

        return employees;
    }

    @Override
    protected PreparedStatement getCreateStatement(Employee employee) {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = """
                INSERT INTO employee
                (firstname, surname, personnelNumber, qualification, username, phoneNumber, passwordHash, salt, role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getSurname());
            preparedStatement.setString(3, employee.getPersonnelNumber());
            preparedStatement.setString(4, employee.getQualification());
            preparedStatement.setString(5, employee.getUsername());
            preparedStatement.setString(6, employee.getPhoneNumber());
            preparedStatement.setString(7, employee.getPasswordHash());
            preparedStatement.setString(8, employee.getSalt());
            preparedStatement.setString(9, employee.getRole().name());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long employeeId) {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = "SELECT * FROM employee WHERE eid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, employeeId);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = "SELECT * FROM employee";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Employee employee) {
        PreparedStatement preparedStatement = null;

        try {
            Employee oldEmployee = read(employee.getEid());

            if (oldEmployee != null
                    && oldEmployee.getRole() == Role.ADMIN
                    && employee.getRole() != Role.ADMIN
                    && countAdmins() <= 1) {
                throw new SQLException("The role of the last admin cannot be changed.");
            }

            final String SQL = """
                UPDATE employee SET
                    firstname = ?,
                    surname = ?,
                    personnelNumber = ?,
                    qualification = ?,
                    username = ?,
                    phoneNumber = ?,
                    passwordHash = ?,
                    salt = ?,
                    role = ?
                WHERE eid = ?
                """;

            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getSurname());
            preparedStatement.setString(3, employee.getPersonnelNumber());
            preparedStatement.setString(4, employee.getQualification());
            preparedStatement.setString(5, employee.getUsername());
            preparedStatement.setString(6, employee.getPhoneNumber());
            preparedStatement.setString(7, employee.getPasswordHash());
            preparedStatement.setString(8, employee.getSalt());
            preparedStatement.setString(9, employee.getRole().name());
            preparedStatement.setLong(10, employee.getEid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long employeeId) {
        PreparedStatement preparedStatement = null;

        try {
            final String SQL = "DELETE FROM employee WHERE eid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, employeeId);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return preparedStatement;
    }
}

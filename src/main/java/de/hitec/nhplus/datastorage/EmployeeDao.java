package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO-Klasse für Mitarbeiterkonten.
 *
 * <p>Diese Klasse verwaltet die separate Tabelle für Mitarbeiter,
 * Login-Daten und Rollen. Außerdem enthält sie Schutzregeln für Admins:
 * Es muss immer mindestens ein Admin im System bleiben.</p>
 */
public class EmployeeDao extends DaoImp<Employee> {

    public EmployeeDao(Connection connection) {
        super(connection);
        createTableIfNotExists();
        assignDefaultRoleToEmployeesWithoutRole();
    }

    /**
     * Erstellt die Tabelle für Mitarbeiterkonten, falls sie noch nicht existiert.
     */
    private void createTableIfNotExists() {
        final String SQL = """
                CREATE TABLE IF NOT EXISTS employee (
                    eid INTEGER PRIMARY KEY AUTOINCREMENT,
                    firstname TEXT NOT NULL,
                    surname TEXT NOT NULL,
                    username TEXT NOT NULL UNIQUE,
                    phoneNumber TEXT,
                    passwordHash TEXT NOT NULL,
                    salt TEXT NOT NULL,
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
     * Setzt für Mitarbeiter ohne Rolle automatisch die Standardrolle MITARBEITER.
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
     * Erstellt einen neuen Mitarbeiter.
     *
     * <p>Wenn noch kein Admin existiert, wird der erste registrierte
     * Mitarbeiter automatisch Admin. Wenn bereits ein Admin existiert,
     * erhält der neue Mitarbeiter automatisch die Rolle Mitarbeiter.</p>
     *
     * @param employee Mitarbeiter, der gespeichert werden soll
     * @throws SQLException wenn das Speichern fehlschlägt
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
     * Prüft, ob mindestens ein Admin existiert.
     *
     * @return true, wenn mindestens ein Admin vorhanden ist
     * @throws SQLException bei Datenbankfehlern
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
     * Zählt alle Admins in der Mitarbeiterdatenbank.
     *
     * @return Anzahl der Admins
     * @throws SQLException bei Datenbankfehlern
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
     * Sucht einen Mitarbeiter anhand des Benutzernamens.
     *
     * <p>Diese Methode wird später vom Login benötigt.</p>
     *
     * @param username Benutzername
     * @return Mitarbeiter oder null, wenn kein Mitarbeiter gefunden wurde
     * @throws SQLException bei Datenbankfehlern
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
     * Ändert die Rolle eines Mitarbeiters.
     *
     * <p>Die Rolle des letzten Admins darf nicht geändert werden, damit
     * immer mindestens ein Admin im System vorhanden bleibt.</p>
     *
     * @param employeeId ID des Mitarbeiters
     * @param newRole    neue Rolle
     * @throws SQLException wenn die Änderung nicht erlaubt ist oder fehlschlägt
     */
    public void updateRole(long employeeId, Role newRole) throws SQLException {
        Employee employee = read(employeeId);

        if (employee == null) {
            throw new SQLException("Mitarbeiter wurde nicht gefunden.");
        }

        Role currentRole = employee.getRole();

        if (currentRole == Role.ADMIN && newRole != Role.ADMIN && countAdmins() <= 1) {
            throw new SQLException("Die Rolle des letzten Admins darf nicht geändert werden.");
        }

        final String SQL = "UPDATE employee SET role = ? WHERE eid = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(SQL)) {
            statement.setString(1, newRole.name());
            statement.setLong(2, employeeId);
            statement.executeUpdate();
        }
    }

    /**
     * Verhindert das direkte Löschen eines Admin-Accounts.
     *
     * <p>Ein Admin muss zuerst auf Verwaltung oder Mitarbeiter geändert werden.
     * Dadurch wird vermieden, dass Admins versehentlich direkt gelöscht werden.</p>
     *
     * @param employeeId ID des Mitarbeiters
     * @throws SQLException wenn der Mitarbeiter Admin ist oder das Löschen fehlschlägt
     */
    @Override
    public void deleteById(long employeeId) throws SQLException {
        Employee employee = read(employeeId);

        if (employee == null) {
            throw new SQLException("Mitarbeiter wurde nicht gefunden.");
        }

        if (employee.getRole() == Role.ADMIN) {
            throw new SQLException("Ein Admin kann nicht direkt gelöscht werden. Ändern Sie zuerst die Rolle.");
        }

        super.deleteById(employeeId);
    }

    @Override
    protected Employee getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Employee(
                result.getLong("eid"),
                result.getString("firstname"),
                result.getString("surname"),
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
                    (firstname, surname, username, phoneNumber, passwordHash, salt, role)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getSurname());
            preparedStatement.setString(3, employee.getUsername());
            preparedStatement.setString(4, employee.getPhoneNumber());
            preparedStatement.setString(5, employee.getPasswordHash());
            preparedStatement.setString(6, employee.getSalt());
            preparedStatement.setString(7, employee.getRole().name());
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
                throw new SQLException("Die Rolle des letzten Admins darf nicht geändert werden.");
            }

            final String SQL = """
                    UPDATE employee SET
                        firstname = ?,
                        surname = ?,
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
            preparedStatement.setString(3, employee.getUsername());
            preparedStatement.setString(4, employee.getPhoneNumber());
            preparedStatement.setString(5, employee.getPasswordHash());
            preparedStatement.setString(6, employee.getSalt());
            preparedStatement.setString(7, employee.getRole().name());
            preparedStatement.setLong(8, employee.getEid());
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
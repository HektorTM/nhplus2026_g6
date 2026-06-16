package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.utils.PasswordUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EmployeeDao.
 * Uses an in-memory database to avoid modifying production data.
 */
class EmployeeDaoTest {

    private static Connection connection;
    private EmployeeDao dao;

    /**
     * Runs once before all tests: creates an in-memory database.
     */
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    /**
     * Runs before each test: creates a fresh DAO and clears the table.
     */
    @BeforeEach
    void setUp() throws SQLException {
        dao = new EmployeeDao(connection);
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM employee");
        }
    }

    /**
     * Runs once after all tests: closes the database connection.
     */
    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("create(): First employee is automatically assigned the ADMIN role")
    void firstEmployeeBecomesAdmin() throws SQLException {
        String salt = PasswordUtil.generateSalt();
        Employee employee = new Employee(
                "Anna", "Admin", "P001", "Systemadministrator",
                "anna", "0421 111111", PasswordUtil.hash("test123", salt), salt, null
        );

        dao.create(employee);

        Employee loaded = dao.findByUsername("anna");
        assertNotNull(loaded);
        assertEquals(Role.ADMIN, loaded.getRole(), "First employee should automatically become ADMIN");
    }

    @Test
    @DisplayName("create(): Second employee is automatically assigned the MITARBEITER role")
    void secondEmployeeGetsMitarbeiter() throws SQLException {
        String salt1 = PasswordUtil.generateSalt();
        Employee admin = new Employee(
                "Anna", "Admin", "P001", "Systemadministrator",
                "anna", "0421 111111", PasswordUtil.hash("admin123", salt1), salt1, null
        );
        dao.create(admin);

        String salt2 = PasswordUtil.generateSalt();
        Employee second = new Employee(
                "Max", "Muster", "P002", "Pflegekraft",
                "max", "0421 222222", PasswordUtil.hash("test456", salt2), salt2, null
        );
        dao.create(second);

        Employee loaded = dao.findByUsername("max");
        assertNotNull(loaded);
        assertEquals(Role.MITARBEITER, loaded.getRole(), "Second employee should automatically get MITARBEITER");
    }

    @Test
    @DisplayName("deleteById(): Deleting the last admin throws a SQLException")
    void deleteLastAdminThrowsException() throws SQLException {
        String salt = PasswordUtil.generateSalt();
        Employee admin = new Employee(
                "Anna", "Admin", "P001", "Systemadministrator",
                "anna", "0421 111111", PasswordUtil.hash("admin123", salt), salt, null
        );
        dao.create(admin);

        Employee loaded = dao.findByUsername("anna");
        assertNotNull(loaded);

        assertThrows(SQLException.class, () -> dao.deleteById(loaded.getEid()),
                "Deleting the last admin should throw a SQLException");
    }
}

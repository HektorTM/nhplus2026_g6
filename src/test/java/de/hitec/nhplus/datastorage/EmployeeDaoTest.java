package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the EmployeeDao class.
 * Uses a separate in-memory database to avoid modifying production data.
 */
class EmployeeDaoTest {

    private static Connection connection;
    private EmployeeDao dao;

    /**
     * Runs once before all tests: creates an in-memory test database.
     */
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    /**
     * Runs before each individual test: creates a fresh DAO object
     * and clears the table so tests do not interfere with each other.
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
    @DisplayName("create() and readAll(): A new employee is saved and can be read")
    void createAndReadAll() throws SQLException {
        // Arrange
        Employee employee = new Employee(
                "Anna", "Müller", "P001", "Krankenpflege",
                "amueller", "0421123456", "hash", "salt", Role.MITARBEITER
        );

        // Act
        dao.create(employee);
        List<Employee> all = dao.readAll();

        // Assert
        assertEquals(1, all.size(), "There should be exactly one employee in the database");

        Employee loaded = all.get(0);
        assertEquals("Anna", loaded.getFirstName());
        assertEquals("Müller", loaded.getSurname());
        assertEquals("amueller", loaded.getUsername());
    }

    @Test
    @DisplayName("create(): The first employee automatically becomes ADMIN")
    void firstEmployeeBecomesAdmin() throws SQLException {
        // Arrange
        Employee employee = new Employee(
                "Max", "Mustermann", "P002", "Verwaltung",
                "mmustermann", "0421654321", "hash", "salt", Role.MITARBEITER
        );

        // Act
        dao.create(employee);
        List<Employee> all = dao.readAll();

        // Assert
        assertEquals(1, all.size());
        assertEquals(Role.ADMIN, all.get(0).getRole(),
                "The first employee should automatically receive the ADMIN role");
    }

    @Test
    @DisplayName("create(): The second employee automatically becomes MITARBEITER")
    void secondEmployeeGetsMitarbeiter() throws SQLException {
        // Arrange
        Employee first = new Employee(
                "Anna", "Admin", "P001", "Leitung",
                "aadmin", "0421000001", "hash", "salt", Role.MITARBEITER
        );
        Employee second = new Employee(
                "Max", "Muster", "P002", "Pflege",
                "mmuster", "0421000002", "hash", "salt", Role.MITARBEITER
        );

        // Act
        dao.create(first);
        dao.create(second);
        List<Employee> all = dao.readAll();

        // Assert
        assertEquals(2, all.size());
        Employee loaded = all.stream()
                .filter(e -> e.getUsername().equals("mmuster"))
                .findFirst()
                .orElseThrow();
        assertEquals(Role.MITARBEITER, loaded.getRole(),
                "The second employee should automatically receive the MITARBEITER role");
    }

    @Test
    @DisplayName("deleteById(): A non-admin employee can be deleted successfully")
    void deleteNonAdminSucceeds() throws SQLException {
        // Arrange: create two employees so the second is not admin
        Employee admin = new Employee(
                "Chef", "Admin", "P003", "Leitung",
                "cadmin", "0421000001", "hash", "salt", Role.ADMIN
        );
        Employee mitarbeiter = new Employee(
                "Hans", "Schmidt", "P004", "Pflege",
                "hschmidt", "0421000002", "hash", "salt", Role.MITARBEITER
        );
        dao.create(admin);
        dao.create(mitarbeiter);

        // Get the pid of the Mitarbeiter
        List<Employee> all = dao.readAll();
        Employee toDelete = all.stream()
                .filter(e -> e.getRole() == Role.MITARBEITER)
                .findFirst()
                .orElseThrow();

        // Act
        dao.deleteById(toDelete.getEid());

        // Assert
        List<Employee> afterDelete = dao.readAll();
        assertEquals(1, afterDelete.size(), "Only the admin should remain");
        assertEquals(Role.ADMIN, afterDelete.get(0).getRole());
    }

    @Test
    @DisplayName("deleteById(): Deleting an admin directly throws SQLException")
    void deleteAdminThrowsException() throws SQLException {
        // Arrange
        Employee admin = new Employee(
                "Root", "Admin", "P005", "Leitung",
                "radmin", "0421000003", "hash", "salt", Role.ADMIN
        );
        dao.create(admin);

        long eid = dao.readAll().get(0).getEid();

        // Act & Assert
        assertThrows(SQLException.class, () -> dao.deleteById(eid),
                "Deleting an admin directly should throw a SQLException");
    }
}

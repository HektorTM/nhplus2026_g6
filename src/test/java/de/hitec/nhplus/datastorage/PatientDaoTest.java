package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Patient;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example test class for the PatientDao class.
 * Uses a separate test database to avoid modifying production data.
 *
 * This class serves as a template: create tests for your own DAO classes in the same way.
 */
class PatientDaoTest {

    private static Connection connection;
    private PatientDao dao;

    /**
     * Runs once before all tests: creates an in-memory test database.
     */
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        // In-memory database: only exists during the tests
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE patient (" +
                            "pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "firstname TEXT NOT NULL, " +
                            "surname TEXT NOT NULL, " +
                            "dateOfBirth TEXT NOT NULL, " +
                            "carelevel TEXT NOT NULL, " +
                            "roomnumber TEXT NOT NULL, " +
                            "assets TEXT NOT NULL)"
            );
        }
    }

    /**
     * Runs before each individual test: creates a fresh DAO object
     * and clears the table so tests do not interfere with each other.
     */
    @BeforeEach
    void setUp() throws SQLException {
        dao = new PatientDao(connection);
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM patient");
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
    @DisplayName("create() and readAll(): A new patient is saved and can be read")
    void createAndReadAll() throws SQLException {
        // Arrange: prepare a new patient
        Patient patient = new Patient(
                "Max", "Mustermann",
                LocalDate.of(1990, 5, 15),
                "3", "101", "normal"
        );

        // Act: create patient in database and read all patients
        dao.create(patient);
        List<Patient> allPatients = dao.readAll();

        // Assert: exactly one patient should be present
        assertEquals(1, allPatients.size(), "There should be exactly one patient in the database");

        Patient loaded = allPatients.get(0);
        assertEquals("Max", loaded.getFirstName());
        assertEquals("Mustermann", loaded.getSurname());
        assertEquals("3", loaded.getCareLevel());
        assertEquals("101", loaded.getRoomNumber());
    }

    @Test
    @DisplayName("deleteById(): A deleted patient is no longer retrievable")
    void deleteById() throws SQLException {
        // Arrange: create a patient
        Patient patient = new Patient(
                "Erika", "Musterfrau",
                LocalDate.of(1985, 3, 20),
                "2", "202", "normal"
        );
        dao.create(patient);

        // Get the ID of the created patient
        List<Patient> allPatients = dao.readAll();
        assertEquals(1, allPatients.size());
        long pid = allPatients.get(0).getPid();

        // Act: delete patient
        dao.deleteById(pid);

        // Assert: no patient should be present anymore
        List<Patient> afterDelete = dao.readAll();
        assertTrue(afterDelete.isEmpty(), "After deletion, no patient should be present");
    }
}

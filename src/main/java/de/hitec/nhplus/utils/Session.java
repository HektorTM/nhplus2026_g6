package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;

/**
 * Stores information about the currently logged-in employee.
 *
 * <p>Only safe session data is stored here: employee ID, username and role.
 * Password hash and salt are not stored in the session.</p>
 */
public final class Session {

    private static Long currentEmployeeId;
    private static String currentUsername;
    private static Role currentRole;

    private Session() {
        // Utility class — no instantiation
    }

    /**
     * Sets the current employee after a successful login.
     * Only ID, username and role are copied into the session.
     *
     * @param employee logged-in employee
     */
    public static void setCurrentEmployee(Employee employee) {
        if (employee == null) {
            clear();
            return;
        }

        currentEmployeeId = employee.getEid();
        currentUsername = employee.getUsername();
        currentRole = employee.getRole();
    }

    /**
     * Returns the ID of the currently logged-in employee.
     *
     * @return employee ID or null if no employee is logged in
     */
    public static Long getCurrentEmployeeId() {
        return currentEmployeeId;
    }

    /**
     * Returns the username of the currently logged-in employee.
     *
     * @return username or null if no employee is logged in
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Returns the role of the currently logged-in employee.
     *
     * @return current role or null if no employee is logged in
     */
    public static Role getCurrentRole() {
        return currentRole;
    }

    /**
     * Clears the current session, for example during logout.
     */
    public static void clear() {
        currentEmployeeId = null;
        currentUsername = null;
        currentRole = null;
    }

    /**
     * Checks whether an employee is currently logged in.
     *
     * @return true if a session exists
     */
    public static boolean isLoggedIn() {
        return currentEmployeeId != null && currentRole != null;
    }

    /**
     * Checks whether the current employee has admin rights.
     *
     * @return true if the current role is ADMIN
     */
    public static boolean isAdmin() {
        return currentRole == Role.ADMIN;
    }

    /**
     * Checks whether the current employee may use administration functions.
     * Admin and Verwaltung are allowed to use these functions.
     *
     * @return true if the current role is ADMIN or VERWALTUNG
     */
    public static boolean canUseAdministrationFunctions() {
        return currentRole == Role.ADMIN || currentRole == Role.VERWALTUNG;
    }

    /**
     * Checks whether the current employee has the normal employee role.
     *
     * @return true if the current role is MITARBEITER
     */
    public static boolean isMitarbeiter() {
        return currentRole == Role.MITARBEITER;
    }


    /**
     * Returns the currently logged-in employee as an {@link Employee} object.
     * The data is reconstructed from the stored session values.
     *
     * If no employee is currently logged in (missing id, role, or username),
     * this method returns {@code null}.
     *
     * @return the current {@link Employee} or {@code null} if no session is active
     */
    public static Employee getCurrentEmployee() {
        if (currentEmployeeId == null || currentRole == null || currentUsername == null) {
            return null;
        }

        Employee employee = new Employee(
                null,        // firstName unbekannt in Session
                null,        // surname unbekannt in Session
                null,
                null,
                currentUsername,
                null,
                null,
                null,
                currentRole
        );

        employee.setEid(currentEmployeeId);
        return employee;
    }
}
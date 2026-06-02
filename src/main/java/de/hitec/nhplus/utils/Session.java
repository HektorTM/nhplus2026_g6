package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Role;

/**
 * Speichert den aktuell angemeldeten Mitarbeiter.
 *
 * <p>Der Login setzt nach erfolgreicher Anmeldung den aktuellen Mitarbeiter.
 * Andere Klassen können danach prüfen, welche Rolle der aktuelle Benutzer hat.</p>
 */
public final class Session {

    private static Employee currentEmployee;

    private Session() {
        // Utility-Klasse — keine Instanziierung
    }

    public static void setCurrentEmployee(Employee employee) {
        currentEmployee = employee;
    }

    public static Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public static void clear() {
        currentEmployee = null;
    }

    public static boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public static boolean isAdmin() {
        return currentEmployee != null && currentEmployee.getRole() == Role.ADMIN;
    }

    public static boolean canUseAdministrationFunctions() {
        return currentEmployee != null
                && (currentEmployee.getRole() == Role.ADMIN
                || currentEmployee.getRole() == Role.VERWALTUNG);
    }

    public static boolean isMitarbeiter() {
        return currentEmployee != null && currentEmployee.getRole() == Role.MITARBEITER;
    }
}
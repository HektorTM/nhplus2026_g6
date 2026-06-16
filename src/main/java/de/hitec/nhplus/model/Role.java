package de.hitec.nhplus.model;

/**
 * ADMIN:
 * Has access to all functions, including role management.
 *
 * VERWALTUNG:
 * Has access to administration functions, e.g. export,
 * but no access to role management.
 *
 * MITARBEITER:
 * Default role for regular employees.
 */
public enum Role {
    ADMIN,
    VERWALTUNG,
    MITARBEITER;


    public static Role fromString(String value) {
        if (value == null || value.isBlank()) {
            return MITARBEITER;
        }

        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return MITARBEITER;
        }
    }
}

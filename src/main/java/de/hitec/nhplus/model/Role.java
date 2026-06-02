package de.hitec.nhplus.model;

/**
 * ADMIN:
 * Hat Zugriff auf alle Funktionen, inklusive Rollenverwaltung.
 *
 * VERWALTUNG:
 * Hat Zugriff auf Verwaltungsfunktionen, z. B. Export,
 * aber keinen Zugriff auf die Rollenverwaltung.
 *
 * MITARBEITER:
 * Standardrolle für normale Mitarbeiter.
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
package de.hitec.nhplus.utils;

import javafx.scene.control.Alert;

/**
 * Hilfsklasse zum Anzeigen von einfachen Meldungen in der JavaFX-Oberfläche.
 */
public final class AlertUtil {

    private AlertUtil() {
        // Utility-Klasse — keine Instanziierung
    }

    /**
     * Zeigt eine Fehlermeldung an.
     *
     * @param title   Titel des Fensters
     * @param message Inhalt der Fehlermeldung
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Zeigt eine Information an.
     *
     * @param title   Titel des Fensters
     * @param message Inhalt der Information
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Zeigt eine Warnung an.
     *
     * @param title   Titel des Fensters
     * @param message Inhalt der Warnung
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
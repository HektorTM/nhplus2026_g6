package de.hitec.nhplus.utils;

import javafx.scene.control.Alert;

/**
 * Utility class for displaying simple messages in the JavaFX interface.
 */
public final class AlertUtil {

    private AlertUtil() {
        // Utility class — no instantiation
    }

    /**
     * Displays an error message.
     *
     * @param title   window title
     * @param message error message content
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information message.
     *
     * @param title   window title
     * @param message information content
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a warning message.
     *
     * @param title   window title
     * @param message warning content
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the login view.
 * Verifies user credentials against the database and switches to the main window on success.
 */
public class LoginController {

    @FXML private TextField textFieldUsername;
    @FXML private PasswordField passwordField;
    @FXML private Label labelError;

    private Stage stage;

    /**
     * Sets the primary stage so the controller can switch scenes after login.
     *
     * @param stage The primary application stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the login button click.
     * Verifies the entered credentials against the database.
     * On success, switches to the main window. On failure, shows an error message.
     */
    @FXML
    public void handleLogin() {
        String username = textFieldUsername.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showError("Bitte Benutzername und Passwort eingeben.");
            return;
        }

        try {
            UserDao dao = DaoFactory.getDaoFactory().createUserDao();
            User user = dao.findByUsername(username);

            if (user == null || !PasswordUtil.verify(password, user.getSalt(), user.getPasswordHash())) {
                showError("Benutzername oder Passwort falsch.");
                passwordField.clear();
                return;
            }

            loadMainWindow();

        } catch (SQLException exception) {
            exception.printStackTrace();
            showError("Datenbankfehler. Bitte erneut versuchen.");
        }
    }

    /**
     * Displays an error message below the login form.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        labelError.setText(message);
        labelError.setVisible(true);
    }

    /**
     * Loads the main window and replaces the login scene on the same stage.
     */
    private void loadMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();
            Scene mainScene = new Scene(pane);
            stage.setScene(mainScene);
            stage.setResizable(false);
        } catch (IOException exception) {
            exception.printStackTrace();
            showError("Hauptfenster konnte nicht geladen werden.");
        }
    }
}

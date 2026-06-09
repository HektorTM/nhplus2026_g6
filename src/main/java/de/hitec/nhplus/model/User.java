package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a user account in the NHPlus system.
 * Stores credentials and role information for authentication.
 */
public class User {

    private SimpleLongProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty passwordHash;
    private final SimpleStringProperty salt;
    private final SimpleStringProperty role;

    /**
     * Constructor for a new user that has not been persisted yet (no ID).
     *
     * @param username     Unique username.
     * @param passwordHash SHA-256 hash of the password.
     * @param salt         Salt used during hashing.
     * @param role         Role of the user (e.g. Admin, Verwaltung, Mitarbeiter).
     */
    public User(String username, String passwordHash, String salt, String role) {
        this.id = new SimpleLongProperty(0);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.salt = new SimpleStringProperty(salt);
        this.role = new SimpleStringProperty(role);
    }

    /**
     * Constructor for a user loaded from the database (has an ID).
     *
     * @param id           Database ID.
     * @param username     Unique username.
     * @param passwordHash SHA-256 hash of the password.
     * @param salt         Salt used during hashing.
     * @param role         Role of the user.
     */
    public User(long id, String username, String passwordHash, String salt, String role) {
        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.salt = new SimpleStringProperty(salt);
        this.role = new SimpleStringProperty(role);
    }

    public long getId() { return id.get(); }
    public SimpleLongProperty idProperty() { return id; }

    public String getUsername() { return username.get(); }
    public SimpleStringProperty usernameProperty() { return username; }

    public String getPasswordHash() { return passwordHash.get(); }
    public SimpleStringProperty passwordHashProperty() { return passwordHash; }

    public String getSalt() { return salt.get(); }
    public SimpleStringProperty saltProperty() { return salt; }

    public String getRole() { return role.get(); }
    public SimpleStringProperty roleProperty() { return role; }
    public void setRole(String role) { this.role.set(role); }

    @Override
    public String toString() {
        return "User" + "\nID: " + this.id +
                "\nUsername: " + this.username +
                "\nRole: " + this.role + "\n";
    }
}

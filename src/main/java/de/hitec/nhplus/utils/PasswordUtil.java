package de.hitec.nhplus.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for hashing passwords with SHA-256 and salt.
 *
 * <p>This class is used in the NHPlus login system (AB 04) so that
 * passwords are not stored in plaintext in the database. Storing
 * plaintext passwords would violate Art. 32 GDPR (state of the art
 * in access protection).</p>
 *
 * <h3>Why a salt?</h3>
 * <p>Without a salt, two users with the same password would have the same hash
 * in the database. An attacker could look up common passwords using a
 * precomputed table (<em>Rainbow Table</em>). The salt makes every hash
 * unique — even for identical plaintext.</p>
 *
 * <h3>Note for learners</h3>
 * <p>In production systems, <em>Key Derivation Functions</em> such as
 * <b>bcrypt</b>, <b>scrypt</b>, or <b>Argon2</b> should be used for password
 * storage. SHA-256 is a fast hash function; modern GPUs can compute billions
 * of SHA-256 hashes per second, which speeds up brute-force attacks. KDFs are
 * intentionally compute-intensive (cost factor) and therefore significantly
 * more resistant.</p>
 *
 * <p>For the learning context, SHA-256 with salt is sufficient to demonstrate
 * the basic principle. A real system would use bcrypt.</p>
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH_BYTES = 16;

    private PasswordUtil() {
        // Utility class — no instantiation
    }

    /**
     * Generates a new random salt as a hex string.
     *
     * <p>When a user is created, a salt is generated once and stored
     * alongside the computed hash in the database.</p>
     *
     * @return 32 hex characters (16 bytes of random data)
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        return toHex(salt);
    }

    /**
     * Computes the SHA-256 hash of a password with salt.
     *
     * @param password plaintext password
     * @param salt     salt as a hex string (from {@link #generateSalt()})
     * @return hash as a hex string
     * @throws IllegalStateException if SHA-256 is not available in the JVM
     */
    public static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available in this JVM.", e);
        }
    }

    /**
     * Checks whether an entered password matches the stored hash.
     *
     * <p>Typical use during login: fetch the username from the DB, read the salt
     * and expected hash, then call this method.</p>
     *
     * <p><b>Security note:</b> {@code equalsIgnoreCase} is <em>not
     * constant-time</em> — for an unequal comparison, Java short-circuits as
     * soon as the first differing character is found. An attacker could use
     * very precise timing measurements to draw conclusions (<em>timing attack</em>).
     * Production systems therefore use a constant-time comparison such as
     * {@link java.security.MessageDigest#isEqual(byte[], byte[])}.
     * For the learning context, the simple comparison is sufficient.</p>
     *
     * @param password     plaintext password from the login form
     * @param salt         salt that was stored when the user was created
     * @param expectedHash hash value from the database
     * @return {@code true} if password + salt produce the expected hash
     */
    public static boolean verify(String password, String salt, String expectedHash) {
        return hash(password, salt).equalsIgnoreCase(expectedHash);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

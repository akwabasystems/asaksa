
package com.akwabasystems.asakusa.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;


/**
 * Utility methods for hashing passwords in order to store them in a database.
 *
 * <p>We rely on a third-party implementation of the bcrypt password hash function.
 *
 * @see <a href="https://github.com/patrickfav/bcrypt">patrickfav/bcrypt</a>
 */
public class PasswordUtils {

    public static String hash(char[] password) {
        return BCrypt.withDefaults().hashToString(12, password);
    }
    
    public static boolean matches(char[] password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password, hash);
        return result.verified;
    }
    
}

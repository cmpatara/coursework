/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * This class uses BCrypt library to store hashes of passwords with salts in
 * SQLite database. This approach fixes the password vulnerability when some
 * person accesses the database.
 */
public class Password {

    /**
     * Takes password as a parameter and returns hash of this password using
     * BCrypt library.
     *
     * @param pswrd given password to be hashed
     * @return hash of given password
     */
    public static String getHash(String pswrd) {
        return BCrypt.hashpw(pswrd, BCrypt.gensalt());
    }

    /**
     * Checks whether given password coincides with the given hash (from
     * database).
     *
     * @param pw given password
     * @param pw_hash given hash
     * @return true if hash of given password coincides with the given hash,
     * otherwise false
     */
    public static boolean checkPswrds(String pw, String pw_hash) {
        return BCrypt.checkpw(pw, pw_hash);
    }
}

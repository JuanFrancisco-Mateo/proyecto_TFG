package es.cifpcarlos3.proyecto.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean verificarPassword(String password, String hash){
        return BCrypt.checkpw(password, hash);
    }
}

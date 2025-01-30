package by.andersen.coworkingspace.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHashUtils {
  private static final int SALT_SIZE = 16;
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
  private static final SecureRandom secureRandom = new SecureRandom();

  public static String hash(char[] password) {
    byte[] salt = new byte[SALT_SIZE];
    secureRandom.nextBytes(salt);
    byte[] hash = pbkdf2(password, salt);
    return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
  }

  public static boolean verifyPassword(char[] password, String hashedPassword) {
    String[] saltHash = hashedPassword.split(":");
    byte[] salt = Base64.getDecoder().decode(saltHash[0]);
    byte[] hash = Base64.getDecoder().decode(saltHash[1]);
    byte[] testHash = pbkdf2(password, salt);
    String encodedHash = Base64.getEncoder().encodeToString(hash);
    String encodedTestHash = Base64.getEncoder().encodeToString(testHash);
    return encodedHash.equals(encodedTestHash);
  }

  private static byte[] pbkdf2(char[] password, byte[] salt) {
    PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
    SecretKeyFactory secretKeyFactory;
    try {
      secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
      return secretKeyFactory.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}

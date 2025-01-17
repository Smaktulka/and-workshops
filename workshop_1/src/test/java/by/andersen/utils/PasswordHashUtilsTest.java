package by.andersen.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordHashUtilsTest {
  @Test
  public void givenPassword_whenHash_andVerifyPassword_thenReturnTrue() {
    char[] password = "pass".toCharArray();
    String hashedPassword = PasswordHashUtils.hash(password);

    Assertions.assertTrue(PasswordHashUtils.verifyPassword(password, hashedPassword));
  }

  @Test
  public void givenWrongPassword_whenVerifyPassword_thenReturnFalse() {
    char[] password = "pass".toCharArray();
    String hashedPassword = PasswordHashUtils.hash(password);

    char[] wrongPassword = "wrong_pass".toCharArray();

    Assertions.assertFalse(PasswordHashUtils.verifyPassword(wrongPassword, hashedPassword));
  }

  @Test
  public void givenPassword_whenHashMultiply_andVerifyPassword_thenReturnTrue() {
    char[] password = "pass".toCharArray();

    String firstHash = PasswordHashUtils.hash(password);
    String secondHash = PasswordHashUtils.hash(password);

    Assertions.assertTrue(PasswordHashUtils.verifyPassword(password, firstHash));
    Assertions.assertTrue(PasswordHashUtils.verifyPassword(password, secondHash));
  }

  @Test
  public void givenMultipleHashesOfSamePassword_whenEquals_thenReturnFalse() {
    char[] password = "pass".toCharArray();

    String firstHash = PasswordHashUtils.hash(password);
    String secondHash = PasswordHashUtils.hash(password);

    Assertions.assertFalse(firstHash.equals(secondHash));
  }
}

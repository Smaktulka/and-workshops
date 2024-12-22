package by.andersen.utils;

import java.io.Console;

public class ConsolePasswordUtils {
  public static char[] readPassword() {
    Console console = System.console();
    if (console == null) {
      throw new RuntimeException("Cannot get console");
    }

    return console.readPassword();
  }
}

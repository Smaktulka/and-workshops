package by.andersen.utils;

public class CommandPrinter {
  public static <E extends Enum<E>> void printEnumValues(Class<E> enumClass) {
    if (enumClass == null) {
      throw new IllegalArgumentException("Enum class cannot be null");
    }

    System.out.println("Available commands:");
    for (E enumValue: enumClass.getEnumConstants()) {
      System.out.println(enumValue);
    }
  }
}

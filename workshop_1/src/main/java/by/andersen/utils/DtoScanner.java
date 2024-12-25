package by.andersen.utils;

import by.andersen.exception.DtoScanException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DtoScanner {
  private static final String DATE_PATTERN = "yyyy-MM-dd";

  public static <T> Optional<T> scan(Class<T> dtoClass) {
    T instance;
    Scanner inputReader = new Scanner(System.in);
    try {
      instance = dtoClass.getDeclaredConstructor().newInstance();

      Field[] fields = dtoClass.getDeclaredFields();
      for (Field field: fields) {
        field.setAccessible(true);
        List<String> validValuesOrFormat = null;
        if (field.getType().isEnum()) {
          validValuesOrFormat = convertEnumValuesToList((Class<? extends Enum>) field.getType());
        } else if (field.getType() == LocalDate.class) {
          validValuesOrFormat = new ArrayList<>();
          validValuesOrFormat.add(DATE_PATTERN);
        }

        if (validValuesOrFormat != null) {
          System.out.printf("%s(%s): ", field.getName(), validValuesOrFormat);
        } else {
          System.out.printf("%s: ", field.getName());
        }

        String input = inputReader.nextLine();

        setInputToField(instance, field, input);
      }
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
             NoSuchMethodException e) {
      throw new DtoScanException(e);
    } catch (IllegalArgumentException | DateTimeException e) {
      System.out.println("Invalid input!");
      return Optional.empty();
    }

    return Optional.of(instance);
  }

  private static <T> void setInputToField(T instance, Field field, String input)
      throws IllegalAccessException {
    if (field.getType() == int.class || field.getType() == Integer.class) {
      field.set(instance, Integer.parseInt(input));
    } else if (field.getType() == double.class || field.getType() == Double.class) {
      field.set(instance, Double.parseDouble(input));
    } else if (field.getType() == long.class || field.getType() == Long.class) {
      field.set(instance, Long.parseLong(input));
    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
      field.set(instance, Boolean.parseBoolean(input));
    } else if(field.getType() == BigDecimal.class) {
      field.set(instance, BigDecimal.valueOf(Double.parseDouble(input)));
    } else if (field.getType().isEnum()) {
      Class<? extends Enum> enumClass = (Class<? extends Enum>) field.getType();
      Enum<?> enumConstant = Enum.valueOf(enumClass, input.toUpperCase());
      field.set(instance, enumConstant);
    } else if (field.getType() == LocalDate.class) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
      LocalDate date = LocalDate.parse(input, formatter);
      field.set(instance, date);
    } else {
      field.set(instance, input);
    }
  }

  private static <E extends Enum<E>> List<String> convertEnumValuesToList(Class<E> enumClass) {
    return Arrays.stream(enumClass.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.toCollection(ArrayList<String>::new));
  }
}

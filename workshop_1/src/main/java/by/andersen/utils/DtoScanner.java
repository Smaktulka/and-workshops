package by.andersen.utils;

import by.andersen.dto.PeriodDto;
import by.andersen.exception.DtoScanException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class DtoScanner {
  public static <T> Optional<T> scan(Class<T> dtoClass) {
    if (dtoClass == null) {
      throw new IllegalArgumentException("Dto class cannot be null");
    }

    T instance;
    Scanner inputReader = new Scanner(System.in);
    try {
      Field[] nonStaticFields = Arrays.stream(dtoClass.getDeclaredFields())
          .filter(f -> !Modifier.isStatic(f.getModifiers()))
          .toArray(Field[]::new);

      List<Class> types = Arrays.stream(nonStaticFields).map(Field::getType)
          .collect(Collectors.toCollection(ArrayList::new));

      List<Object> parsedInputs = new ArrayList<>();
      for (Field field: nonStaticFields) {
        List<String> possibleValuesOrFormat = getPossibleValuesOrFormat(field);
        printPossibleValuesOrFormatForField(possibleValuesOrFormat, field.getName());

        String input = inputReader.nextLine();
        Object parsedInput = parseInput(field.getType(), input);
        parsedInputs.add(parsedInput);
      }

      instance = dtoClass
          .getDeclaredConstructor(types.toArray(Class[]::new))
          .newInstance(parsedInputs.toArray(Object[]::new));

    } catch  (IllegalArgumentException | DateTimeException e) {
      outputInvalidInputMessage(e.getMessage());
      return Optional.empty();
    } catch (NotImplementedException | InvocationTargetException | InstantiationException |
             NoSuchMethodException | IllegalAccessException e) {
      Throwable target = e.getCause();
      if (target.getClass().equals(DateTimeException.class)) {
        outputInvalidInputMessage(target.getMessage());
        return Optional.empty();
      }

      throw new DtoScanException(e);
    }

    return Optional.of(instance);
  }

  private static void outputInvalidInputMessage(String errorMessage) {
    System.out.println(errorMessage);
    System.out.println("Invalid input!");
  }

  private static List<String> getPossibleValuesOrFormat(Field field) {
    if (field.getType().isEnum()) {
      return convertEnumValuesToList((Class<? extends Enum>) field.getType());
    } else if (field.getType() == LocalDate.class) {
      return new ArrayList<>(Collections.singleton(PeriodDto.DATE_PATTERN));
    }

    return new ArrayList<>();
  }

  private static void printPossibleValuesOrFormatForField(List<String> possibleValuesOrFormat, String fieldName) {
    if (!possibleValuesOrFormat.isEmpty()) {
      System.out.printf("%s(%s): ", fieldName, possibleValuesOrFormat);
    } else {
      System.out.printf("%s: ", fieldName);
    }
  }

  private static Object parseInput(Class type, String input)
      throws IllegalAccessException, NotImplementedException {
    if (type == int.class || type == Integer.class) {
      return Integer.parseInt(input);
    } else if (type == double.class || type == Double.class) {
      return Double.parseDouble(input);
    } else if (type == long.class || type == Long.class) {
      return Long.parseLong(input);
    } else if (type == boolean.class || type == Boolean.class) {
      return Boolean.parseBoolean(input);
    } else if(type == BigDecimal.class) {
      return BigDecimal.valueOf(Double.parseDouble(input));
    } else if (type.isEnum()) {
      Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
      return Enum.valueOf(enumClass, input.toUpperCase());
    } else if (type == LocalDate.class) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PeriodDto.DATE_PATTERN);
      return LocalDate.parse(input, formatter);
    } else if (type == String.class){
      return input;
    } else {
      throw new NotImplementedException("Scan not implemented for " + type);
    }
  }

  private static <E extends Enum<E>> List<String> convertEnumValuesToList(Class<E> enumClass) {
    return Arrays.stream(enumClass.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.toCollection(ArrayList<String>::new));
  }
}

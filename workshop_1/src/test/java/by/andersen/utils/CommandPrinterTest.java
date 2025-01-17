package by.andersen.utils;

import by.andersen.enums.command.AdminLoopCommand;
import by.andersen.enums.command.CustomerLoopCommand;
import by.andersen.enums.command.StartLoopCommand;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommandPrinterTest {
  private final PrintStream standardOut = System.out;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(standardOut);
  }

  @ParameterizedTest
  @ValueSource(classes = {AdminLoopCommand.class, CustomerLoopCommand.class, StartLoopCommand.class })
  public <E extends Enum<E>> void givenCommandEnum_whenPrintEnumValues_thenPrintOutAllEnumValues(Class<E> enumClass) {
    long enumValuesAmount = enumClass.getEnumConstants().length;
    CommandPrinter.printEnumValues(enumClass);
    long newLineCharsAmount = countCharOccurrences(outputStreamCaptor.toString().trim(), '\n');

    Assertions.assertEquals(enumValuesAmount, newLineCharsAmount);
  }

  private long countCharOccurrences(String str, char ch) {
    return str.chars().filter(c -> c == ch).count();
  }

  @Test
  public void givenNull_whenPrintEnumValues_thenThrowRuntimeException() {
    Exception e = Assertions.assertThrows(RuntimeException.class, () -> CommandPrinter.printEnumValues(null));

    String expectedMessage = "Enum class cannot be null";
    String actualMessage = e.getMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }
}

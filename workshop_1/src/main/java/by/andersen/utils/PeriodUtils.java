package by.andersen.utils;

import by.andersen.dto.PeriodDto;
import java.time.LocalDate;

public class PeriodUtils {
  public static boolean periodsOverlap(PeriodDto firstPeriod, PeriodDto secondPeriod) {
    LocalDate firstStartDate = firstPeriod.getStartDate();
    LocalDate firstEndDate = firstPeriod.getEndDate();
    LocalDate secondStartDate = secondPeriod.getStartDate();
    LocalDate secondEndDate = secondPeriod.getEndDate();

    return (firstStartDate.isBefore(secondStartDate) && firstEndDate.isAfter(secondEndDate))
        || (firstStartDate.isBefore(secondEndDate) && firstEndDate.isAfter(secondStartDate))
        || (firstStartDate.isAfter(secondStartDate) && firstEndDate.isBefore(secondStartDate));
  }
}

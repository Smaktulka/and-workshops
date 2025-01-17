package by.andersen.repository;

import by.andersen.dto.PeriodDto;
import by.andersen.entity.Reservation;
import by.andersen.utils.PeriodUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

public class ReservationRepository extends JdbcRepository<Reservation, Long> {

  public ReservationRepository(
      DataSource dataSource,
      String currentSchema,
      String tableName,
      Class<Reservation> entityClass
  ) {
    super(dataSource, currentSchema, tableName, entityClass);
  }

  public List<Reservation> getReservationsByOwnerId(Long ownerId) {
    List<Reservation> reservations = this.findAll();
    return reservations.stream()
        .filter(reservation -> reservation.getOwnerId().equals(ownerId))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<Reservation> getReservationByWorkspaceIdAndPeriod(Long workspaceId, PeriodDto periodDto) {
    List<Reservation> reservations = this.findAll();
    return reservations.stream()
        .filter(reservation -> reservation.getWorkspaceId().equals(workspaceId)
            && PeriodUtils.periodsOverlap(reservation.getPeriod(), periodDto))
        .collect(Collectors.toCollection(ArrayList::new));
  }
}

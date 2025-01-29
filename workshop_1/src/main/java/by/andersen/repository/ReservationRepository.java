package by.andersen.repository;

import by.andersen.dto.PeriodDto;
import by.andersen.entity.Reservation;
import by.andersen.utils.PeriodUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepository extends HibernateRepository<Reservation, Long> {
  @Autowired
  public ReservationRepository(SessionFactory sessionFactory) {
    super(sessionFactory, Reservation.class);
  }

  public List<Reservation> getReservationsByOwnerId(Long ownerId) {
    List<Reservation> reservations = this.findAll();
    return reservations.stream()
        .filter(reservation -> reservation.getOwner().getId().equals(ownerId))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<Reservation> getReservationByWorkspaceIdAndPeriod(Long workspaceId, PeriodDto periodDto) {
    List<Reservation> reservations = this.findAll();
    return reservations.stream()
        .filter(reservation -> reservation.getWorkspace().getId().equals(workspaceId)
            && PeriodUtils.periodsOverlap(reservation.getPeriod(), periodDto))
        .collect(Collectors.toCollection(ArrayList::new));
  }
}

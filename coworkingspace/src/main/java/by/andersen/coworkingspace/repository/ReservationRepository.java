package by.andersen.coworkingspace.repository;

import by.andersen.coworkingspace.entity.Reservation;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findByOwnerId(Long ownerId);

  @Query("""
      SELECT r FROM Reservation r
        WHERE
          r.workspace.id = :workspaceId
          AND (
               (r.startTime <= :startTime AND r.endTime >= :endTime)
            OR (r.startTime < :endTime AND r.startTime > :startTime)
            OR (r.endTime > :starTime AND r.endTime < :endTime)
          )
      """)
  List<Reservation> getReservationByWorkspaceIdAndPeriodOverlap(
      @Param("workspaceId") Long workspaceId,
      @Param("startTime") LocalDate startTime,
      @Param("endTime") LocalDate endTime
  );
}

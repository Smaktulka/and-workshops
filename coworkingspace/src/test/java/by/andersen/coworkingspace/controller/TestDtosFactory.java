package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.dto.PeriodDto;
import by.andersen.coworkingspace.dto.RegisterDto;
import by.andersen.coworkingspace.dto.ReservationDto;
import by.andersen.coworkingspace.entity.RefreshToken;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.enums.UserRole;
import by.andersen.coworkingspace.enums.WorkspaceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;

public class TestDtosFactory {
  public static User userWithInvalidPasswordHash() {
    return User.builder()
        .name(adminRegisterDto().getUserName())
        .role(UserRole.ADMIN)
        .passwordHash("pass hash")
        .build();
  }

  public static User user(String passwordHash) {
    return User.builder()
        .name(adminRegisterDto().getUserName())
        .role(UserRole.ADMIN)
        .passwordHash(passwordHash)
        .build();
  }

  public static User anotherUser() {
    return User.builder()
        .name("another user")
        .role(UserRole.ADMIN)
        .passwordHash("pass hash")
        .build();
  }

  public static RegisterDto adminRegisterDto() {
    return RegisterDto.builder()
        .userName("test-admin")
        .role(UserRole.ADMIN)
        .password("password".toCharArray())
        .build();
  }

  public static RegisterDto customerRegisterDto() {
    return RegisterDto.builder()
        .userName("test-customer")
        .role(UserRole.CUSTOMER)
        .password("password".toCharArray())
        .build();
  }

  public static LoginDto loginDto() {
    return LoginDto.builder()
        .userName(adminRegisterDto().getUserName())
        .password("password".toCharArray())
        .build();
  }

  public static LoginDto loginDtoWithInvalidPassword() {
    return LoginDto.builder()
        .userName(adminRegisterDto().getUserName())
        .password("invalid pass".toCharArray())
        .build();
  }

  public static RefreshToken token(String refreshToken, User user) {
    return RefreshToken.builder()
        .refreshToken(refreshToken)
        .user(user)
        .build();
  }

  public static Workspace workspace() {
    return Workspace.builder()
        .name("test-workspace")
        .type(WorkspaceType.PRIVATE)
        .price(BigDecimal.valueOf(100L))
        .build();
  }

  public static Workspace workspaceWithReservation(Reservation reservation) {
    return Workspace.builder()
        .name("test-workspace")
        .type(WorkspaceType.PRIVATE)
        .price(BigDecimal.valueOf(100L))
        .reservations(new HashSet<>(Collections.singleton(reservation)))
        .build();
  }

  public static Reservation reservation(User owner, Workspace workspace, PeriodDto periodDto) {
    return Reservation.builder()
        .owner(owner)
        .workspace(workspace)
        .startTime(periodDto.getStartTime())
        .endTime(periodDto.getEndTime())
        .build();
  }

  public static Reservation emptyReservation() {
    return Reservation.builder()
        .startTime(periodDto().getStartTime())
        .endTime(periodDto().getEndTime())
        .build();
  }

  public static PeriodDto periodDto() {
    return PeriodDto.builder()
        .startTime(LocalDate.parse("2024-01-02"))
        .endTime(LocalDate.parse("2024-01-03"))
        .build();
  }

  public static ReservationDto reservationDto(Long workspaceId) {
    return ReservationDto.builder()
        .workspaceId(workspaceId)
        .startTime(LocalDate.parse("2024-01-02"))
        .endTime(LocalDate.parse("2024-01-03"))
        .build();
  }
  public static ReservationDto reservationDtoWithPeriodDto(Long workspaceId, PeriodDto periodDto) {
    return ReservationDto.builder()
        .workspaceId(workspaceId)
        .startTime(periodDto.getStartTime())
        .endTime(periodDto.getEndTime())
        .build();
  }
}

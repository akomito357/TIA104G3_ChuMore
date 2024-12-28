package com.chumore.reservation.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    // 查詢相關功能
    List<ReservationVO> findReservationsByCompositeQuery(Map<String, String> params);

    List<ReservationVO> findCurrentDateReservation(int restId);

    List<ReservationVO> findAllRestReservations(int restId);

    List<ReservationVO> findAllMemberReservations(int memberId);

    List<ReservationVO> findReservationsByRestIdAndReservationStatus(int restId, String reservationStatus);

    List<ReservationVO> findReservationsByMemberIdAndReservationStatus(int memberId, String reservationStatus);

    // 更新與操作功能
    ReservationVO cancelReservation(int reservationId);

    ReservationVO processCheckIn(int reservationId);

    ReservationVO addReservation(
            int memberId,
            int restId,
            LocalDate reservationDate,
            LocalTime reservationTime,
            int guestCount,
            String phoneNumber
    );
}
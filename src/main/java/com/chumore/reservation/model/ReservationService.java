package com.chumore.reservation.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    // 查詢相關功能
    ReservationVO findReservationById(int reservationId);

    List<ReservationVO> findReservationsByCompositeQuery(Map<String, String> params);

    List<ReservationVO> findCurrentDateReservation(int restId);

    List<ReservationVO> findReservationsByRestIdAndDate(int restId,LocalDate date);

    List<ReservationVO> findAllRestReservations(int restId);

    List<ReservationVO> findAllMemberReservations(int memberId);

    List<ReservationVO> findReservationsByRestIdAndReservationStatus(int restId, String reservationStatus);

    List<ReservationVO> findReservationsByMemberIdAndReservationStatus(int memberId, String reservationStatus);


    // 更新與操作功能
    ReservationVO cancelReservation(int reservationId);

    ReservationVO processCheckIn(int reservationId);

    ReservationVO recoverReservation(int reservationId);

    ReservationVO addReservation(ReservationVO reservation);

}
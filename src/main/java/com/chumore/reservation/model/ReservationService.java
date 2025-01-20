package com.chumore.reservation.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    // 查詢相關功能
    ReservationVO findReservationById(Integer reservationId);

    List<ReservationVO> findReservationsByCompositeQuery(Map<String, String> params);

    List<ReservationVO> findCurrentDateReservation(Integer restId);

    List<ReservationVO> findReservationsByRestIdAndDate(Integer restId,LocalDate date);

    List<ReservationVO> findAllRestReservations(Integer restId);

    List<ReservationVO> findAllMemberReservations(Integer memberId);

    List<ReservationVO> findReservationsByRestIdAndReservationStatus(Integer restId, String reservationStatus);

    List<ReservationVO> findReservationsByMemberIdAndReservationStatus(Integer memberId, String reservationStatus);


    // 更新與操作功能
    ReservationVO cancelReservation(Integer reservationId);

    ReservationVO processCheckIn(Integer reservationId);

    ReservationVO restoreReservation(Integer reservationId);

    ReservationVO addReservation(ReservationVO reservation);

}
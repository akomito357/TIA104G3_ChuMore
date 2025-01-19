package com.chumore.reservation.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationDAO {

    // 查詢
    ReservationVO findById(int reservationId);
    List<ReservationVO> findAll();
    List<ReservationVO> findAllByMemberId(int memberId);
    List<ReservationVO> findAllByRestId(int restId);
    List<ReservationVO> findByMemberIdAndDate(int memberId, LocalDate date);
    List<ReservationVO> findByRestIdAndDate(int restId, LocalDate date);
    List<ReservationVO> findByRestIdAndReservationStatus(int restId, String reservationStatus);
    List<ReservationVO> findByMemberIdAndReservationStatus(int memberId, String reservationStatus);
    List<ReservationVO> findByCompositeQuery(Map<String, String> params);

    // 新增
    ReservationVO addReservation(ReservationVO reservationVO);

    // 修改
    ReservationVO updateReservation(ReservationVO reservationVO);
}
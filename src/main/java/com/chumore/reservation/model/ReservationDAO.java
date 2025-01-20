package com.chumore.reservation.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationDAO {

    // 查詢
    ReservationVO findById(Integer reservationId);
    List<ReservationVO> findAll();
    List<ReservationVO> findAllByMemberId(Integer memberId);
    List<ReservationVO> findAllByRestId(Integer restId);
    List<ReservationVO> findByMemberIdAndDate(Integer memberId, LocalDate date);
    List<ReservationVO> findByRestIdAndDate(Integer restId, LocalDate date);
    List<ReservationVO> findByRestIdAndReservationStatus(Integer restId, String reservationStatus);
    List<ReservationVO> findByMemberIdAndReservationStatus(Integer memberId, String reservationStatus);
    List<ReservationVO> findByCompositeQuery(Map<String, String> params);

    // 新增
    ReservationVO addReservation(ReservationVO reservationVO);

    // 修改
    ReservationVO updateReservation(ReservationVO reservationVO);
}
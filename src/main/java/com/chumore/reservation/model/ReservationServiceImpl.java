package com.chumore.reservation.model;


import com.chumore.exception.BookingConflictException;
import com.chumore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService{

    @Autowired
    private ReservationDAO reservationDAO;


    @Transactional(readOnly = true)
    @Override
    public ReservationVO findReservationById(int reservationId){
        try{
            return reservationDAO.findById(reservationId);
        }catch(ResourceNotFoundException e){
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByCompositeQuery(Map<String, String> params) {
        List<ReservationVO> reservations = reservationDAO.findByCompositeQuery(params);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for the given query parameters: " + params.toString());
        }
        return reservations;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findCurrentDateReservation(int restId) {
        List<ReservationVO> reservations = reservationDAO.findByRestIdAndDate(restId, LocalDate.now());
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for restId: " + restId + " on today's date.");
        }
        return reservations;
    }


    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByRestIdAndDate(int restId, LocalDate date) {
        List<ReservationVO> reservations = reservationDAO.findByRestIdAndDate(restId, date);
        if(reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for restId: " + restId + " on date: " + date);
        }
        return reservations;
    }



    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findAllRestReservations(int restId) {
        List<ReservationVO> reservations = reservationDAO.findAllByRestId(restId);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No reservations found for restId: " + restId);
        }
        return reservations;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findAllMemberReservations(int memberId) {
        List<ReservationVO> reservations = reservationDAO.findAllByMemberId(memberId);
        return reservations;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByRestIdAndReservationStatus(int restId, String reservationStatus) {
        List<ReservationVO> reservations = reservationDAO.findByRestIdAndReservationStatus(restId, reservationStatus);
        return reservations;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByMemberIdAndReservationStatus(int memberId, String reservationStatus) {
        return reservationDAO.findByMemberIdAndReservationStatus(memberId, reservationStatus);
    }

    @Override
    public ReservationVO cancelReservation(int reservationId) {
        ReservationVO reservation = reservationDAO.findById(reservationId);
        reservation.setReservationStatus(0);
        return reservationDAO.updateReservation(reservation);
    }

    @Override
    public ReservationVO restoreReservation(int reservationId) {
        ReservationVO reservation = reservationDAO.findById(reservationId);
        reservation.setReservationStatus(1);
        return reservationDAO.updateReservation(reservation);
    }

    @Override
    public ReservationVO processCheckIn(int reservationId) {
        ReservationVO reservation = reservationDAO.findById(reservationId);
        reservation.setReservationStatus(2); // 更新狀態為已報到
        return reservationDAO.updateReservation(reservation);
    }



    @Override
    public ReservationVO addReservation(ReservationVO reservation) {
        try {
            reservation.setReservationStatus(1); // 預設狀態為尚未報到
            reservationDAO.addReservation(reservation);
        } catch (BookingConflictException e) {
            throw e;
        }

        return reservation;
    }




}
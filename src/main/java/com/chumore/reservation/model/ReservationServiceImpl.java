package com.chumore.reservation.model;


import com.chumore.exception.BookingConflictException;
import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService{

    @Autowired
    private ReservationDAO reservationDAO;

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByCompositeQuery(Map<String,String> params){
        return reservationDAO.findByCompositeQuery(params);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findCurrentDateReservation(int restId){
        return reservationDAO.findByRestIdAndDate(restId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findAllRestReservations(int restId){
        return reservationDAO.findAllByRestId(restId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findAllMemberReservations(int memberId){
        return reservationDAO.findAllByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByRestIdAndReservationStatus(int restId, String reservationStatus){
        return reservationDAO.findByRestIdAndReservationStatus(restId, reservationStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationVO> findReservationsByMemberIdAndReservationStatus(int memberId, String reservationStatus){
        return reservationDAO.findByMemberIdAndReservationStatus(memberId, reservationStatus);
    }

    @Override
    public ReservationVO cancelReservation(int reservationId){
        ReservationVO reservation = reservationDAO.findById(reservationId);
        reservation.setReservationStatus(0);
        reservationDAO.updateReservation(reservation);
        return reservation;
    }

    @Override
    public ReservationVO processCheckIn(int reservationId){
        ReservationVO reservation = reservationDAO.findById(reservationId);
        reservation.setReservationStatus(2);
        reservationDAO.updateReservation(reservation);
        return reservation;
    }

    @Override
    public ReservationVO addReservation(int memberId, int restId, LocalDate reservationDate, LocalTime reservationTime, int guestCount,String phoneNumber){
        ReservationVO reservation = new ReservationVO();
        try {
            MemberVO member = new MemberVO();
            member.setMemberId(memberId);
            RestVO rest = new RestVO();
            rest.setRestId(restId);

            reservation.setMember(member);
            reservation.setRest(rest);
            reservation.setReservationDate(reservationDate);
            reservation.setReservationTime(reservationTime);
            reservation.setGuestCount(guestCount);
            reservation.setPhoneNumber(phoneNumber);
            reservation.setReservationStatus(1);
        }catch(BookingConflictException e){
            throw e;
        }

        return reservation;

    }








}

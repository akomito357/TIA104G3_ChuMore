package com.chumore.dailyreservation.model;

import com.chumore.util.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DailyReservationServiceImpl implements DailyReservationService {

    @Autowired
    private DailyReservationDAO dailyReservationDAO;

    @Override
    @Transactional(readOnly = true)
    public DailyReservationVO findDailyReservationById(Integer dailyReservationId) {
        return dailyReservationDAO.findById(dailyReservationId);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyReservationVO findDailyReservationByDate(Integer restId, LocalDate date, Integer tableType) {
        return dailyReservationDAO.findByDate(restId, date, tableType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyReservationVO> findDailyReservationsByDate(Integer restId, LocalDate date) {
        List<DailyReservationVO> dailyReservations = dailyReservationDAO.findDailyReservationsByDate(restId, date);
        return dailyReservations;
    }

    @Override
    @Transactional(readOnly = true)
    public DailyReservationVO findDailyReservationByCompositeQuery(Map<String, String> conditions) {
        return dailyReservationDAO.findByCompositeQuery(conditions);
    }


    // 新增每日訂位紀錄表
    @Override
    public DailyReservationVO addDailyReservation(DailyReservationVO dailyReservation) {
        dailyReservationDAO.save(dailyReservation);
        return dailyReservation;
    }


    // 修改每日訂位紀錄表
    @Override
    public DailyReservationVO updateDailyReservation(DailyReservationVO dailyReservation) {
        DailyReservationVO existingReservation = dailyReservationDAO.findById(dailyReservation.getDailyReservationId());
        existingReservation.setReservedTables(dailyReservation.getReservedTables());
        existingReservation.setReservedLimit(dailyReservation.getReservedLimit());

        return dailyReservationDAO.update(existingReservation);
    }

    // 新增可訂位上限
    @Override
    public DailyReservationVO addReservedLimit(Integer dailyReservationId, String time, Integer amount) {
        DailyReservationVO dailyReservation = dailyReservationDAO.findById(dailyReservationId);

        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        int index = Integer.parseInt(time.substring(0, 2)) - 1;
        reservedLimitList.set(index, reservedLimitList.get(index) + amount);

        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        return dailyReservationDAO.update(dailyReservation);
    }

    // 新增可訂位上限 (區間)
    @Override
    public DailyReservationVO addReservedLimitByInterval(DailyReservationVO dailyReservation, String startTime, String endTime, Integer amount) {
        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        int startIndex = Integer.parseInt(startTime.substring(0, 2)) - 1;
        int endIndex = Integer.parseInt(endTime.substring(0, 2));

        for (int i = startIndex; i < endIndex; i++) {
            reservedLimitList.set(i, reservedLimitList.get(i) + amount);
        }

        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        return dailyReservationDAO.update(dailyReservation);
    }

    @Override
    public DailyReservationVO addReservedLimitByCondition(DailyReservationVO dailyReservation, Map<String, String> conditions) {
        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        List<Integer> reservedTablesList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedTables(), 2);

        if (conditions.containsKey("startTime") && conditions.containsKey("endTime")) {
            Integer startTime = Integer.parseInt(conditions.get("startTime").substring(0, 2));
            Integer endTime = Integer.parseInt(conditions.get("endTime").substring(0, 2));

            for (int i = startTime - 1; i < endTime; i++) {
                if ("increase".equals(conditions.get("operation"))) {
                    reservedLimitList.set(i, reservedLimitList.get(i) + Integer.parseInt(conditions.get("amount")));
                } else if ("decrease".equals(conditions.get("operation"))) {
                    int subTotal = reservedLimitList.get(i) - Integer.parseInt(conditions.get("amount"));
                    reservedLimitList.set(i, Math.max(subTotal, Math.max(0, reservedTablesList.get(i))));
                }
            }
        }

        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        dailyReservationDAO.update(dailyReservation);
        return dailyReservation;
    }


    @Override
    public List<DailyReservationVO> setClosedDay(Integer restId, LocalDate date) {
        List<DailyReservationVO> dailyReservations = dailyReservationDAO.findDailyReservationsByDate(restId, date);
        for (DailyReservationVO dailyReservation : dailyReservations) {
            dailyReservation.setReservedLimit("0".repeat(48));
            dailyReservationDAO.update(dailyReservation);
        }
        return dailyReservations;
    }
}

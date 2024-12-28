package com.chumore.dailyreservation.model;

import com.chumore.rest.model.RestVO;
import com.chumore.tabletype.model.TableTypeVO;
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
        return dailyReservationDAO.findByDate(restId,date,tableType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyReservationVO> findDailyReservationsByDate(Integer restId, LocalDate date){
        return dailyReservationDAO.findDailyReservationsByDate(restId,date);
    }

    @Override
    public DailyReservationVO findDailyReservationByCompositeQuery(Map<String,String> conditions){
       return dailyReservationDAO.findByCompositeQuery(conditions);
    }


    // 新增每日訂位紀錄表
    @Override
    public DailyReservationVO addDailyReservation(Integer restId, Integer tableTypeId, LocalDate reservedDate, String reservedTables, String reservedLimit) {
        DailyReservationVO dailyReservation = new DailyReservationVO();
        RestVO rest = new RestVO();
        rest.setRestId(restId);
        TableTypeVO tableType = new TableTypeVO();
        tableType.setTableTypeId(tableTypeId);

        dailyReservation.setRest(rest);
        dailyReservation.setTableType(tableType);
        dailyReservation.setReservedDate(reservedDate);
        dailyReservation.setReservedTables(reservedTables);
        dailyReservation.setReservedLimit(reservedLimit);

        dailyReservationDAO.save(dailyReservation);
        return dailyReservation;
    }
    // 修改每日訂位紀錄表
    @Override
    public void updateDailyReservation(Integer dailyReservationId, String reservedTables, String reservedLimit) {
        DailyReservationVO dailyReservation = dailyReservationDAO.findById(dailyReservationId);
        if (reservedTables != null) {
            dailyReservation.setReservedTables(reservedTables);
        }
        if (reservedLimit != null) {
            dailyReservation.setReservedLimit(reservedLimit);
        }

        dailyReservationDAO.save(dailyReservation);
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
        dailyReservationDAO.update(dailyReservation);

        return dailyReservation;
    }

    // 新增可訂位上限 (區間)
    @Override
    public void addReservedLimitByInterval(Integer dailyReservationId, String startTime, String endTime, Integer amount) {
        DailyReservationVO dailyReservation = dailyReservationDAO.findById(dailyReservationId);

        // 解析 reservedLimit 並對區間內的每個時間段進行更新
        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(), 2);
        int startIndex = Integer.parseInt(startTime.substring(0, 2)) - 1;
        int endIndex = Integer.parseInt(endTime.substring(0, 2));

        for (int i = startIndex; i < endIndex; i++) {
            reservedLimitList.set(i, reservedLimitList.get(i) + amount);
        }

        // 更新 reservedLimit 並保存
        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList, 2);
        dailyReservation.setReservedLimit(reservedLimit);
        dailyReservationDAO.update(dailyReservation);
    }

    // 複合新增 (日期、開始時間、結束時間、桌種、增減) 輸入：map 輸出：更改好的查詢結果
    @Override
    public DailyReservationVO addReservedLimitByCondition(Map<String,String> conditions){
        DailyReservationVO dailyReservation = dailyReservationDAO.findByCompositeQuery(conditions);
        List<Integer> reservedLimitList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedLimit(),2);
        List<Integer> reservedTablesList = ConverterUtil.convertStrToTimeList(dailyReservation.getReservedTables(),2);
        if(conditions.containsKey("startTime") && conditions.containsKey("endTime")){
            Integer startTime = Integer.parseInt(conditions.get("startTime").substring(0,2));
            Integer endTime = Integer.parseInt(conditions.get("endTime").substring(0,2));
            for(int i = startTime-1;i<endTime;i++){
                if(conditions.get("operation").equals("increase")){
                    reservedLimitList.set(i,reservedLimitList.get(i)+Integer.parseInt(conditions.get("amount")));
                }else if(conditions.get("operation").equals("decrease")){
                    //1.該時段可訂位上限修改後不能小於已訂位桌數，若小於的話則設定為已訂位桌數 2.可訂位上限在修改後不能小於0，若小於0則設定為0
                    int subTotal = reservedLimitList.get(i)-Integer.parseInt(conditions.get("amount"));
                    if(subTotal < reservedTablesList.get(i)){
                        reservedLimitList.set(i,reservedTablesList.get(i));
                    }else if(subTotal < 0){
                        reservedLimitList.set(i,0);
                    }else{
                        reservedLimitList.set(i,subTotal);
                    }
                }
            }
        }

        String reservedLimit = ConverterUtil.convertTimeListToStr(reservedLimitList,2);
        dailyReservation.setReservedLimit(reservedLimit);
        dailyReservationDAO.update(dailyReservation);
        return dailyReservation;
    }

    // 設定公休日
    public List<DailyReservationVO> setClosedDay(Integer restId,LocalDate date){
         List<DailyReservationVO>  dailyReservations = dailyReservationDAO.findDailyReservationsByDate(restId, date);
         for(DailyReservationVO dailyReservation : dailyReservations){
             dailyReservation.setReservedLimit("0".repeat(48));
             dailyReservationDAO.update(dailyReservation);
         }
         return dailyReservations;
    }






}

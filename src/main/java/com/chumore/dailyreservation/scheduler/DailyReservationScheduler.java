package com.chumore.dailyreservation.scheduler;

import com.chumore.dailyreservation.model.DailyReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyReservationScheduler {

    @Autowired
    private DailyReservationService dailyReservationService;

//    private static final Logger logger = LoggerFactory.getLogger(DailyReservationScheduler.class);

//    private static LocalDate startDate = LocalDate.now();
//
//    @Scheduled(fixedRate = 1000 * 30)
//    @Async("taskExecutor")
//    public void insertDefaultDailyReservationTask(){
//        try{
//            // 根據環境設定要插入的日期範圍
//            startDate = startDate.plusDays(1);
//            LocalDate endDate = startDate.plusDays(30);
//
//            int rows = dailyReservationService.insertDefaultDailyReservations(startDate, endDate);
//            logger.info("[DailyReservationScheduler] Insert default daily reservations done, rows: {}, startDate: {}, endDate: {}",
//                    rows, startDate, endDate);
//        } catch(Exception e){
//            logger.error("[DailyReservationScheduler] Insert failed: ", e);
//
//    }


    // 每日生成預設daily reservation
    @Scheduled(cron = "0 0 0 * * ?")
    @Async("taskExecutor")
    public void insertDefaultDailyReservationTask(){
        try{
            int rows = dailyReservationService.insertDefaultDailyReservations();
            System.out.println(
                    "[DailyReservationScheduler] Insert default daily reservations done, rows: " + rows
            );
        }catch(Exception e){
            System.err.println("[DailyReservationScheduler] Insert failed: " + e.getMessage());
        }
    }
}

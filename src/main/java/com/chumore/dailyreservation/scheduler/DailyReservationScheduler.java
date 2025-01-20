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

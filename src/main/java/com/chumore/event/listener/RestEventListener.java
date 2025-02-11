package com.chumore.event.listener;


import com.chumore.event.RestChangedEvent;
import com.chumore.rest.model.RestVO;
import com.chumore.search.model.LuceneIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 功能：監聽 RestChangedEvent。
 * 事件：新增、更新、刪除餐廳時同步索引
 */

@Component
public class RestEventListener {

    @Autowired
    private LuceneIndexService luceneIndexService;

    private static final Logger logger = LoggerFactory.getLogger(RestEventListener.class);

    @Async
    @EventListener
    public void handleRestChangedEvent(RestChangedEvent event) {
        logger.info("Received RestChangedEvent: restId={}", event.getRest().getRestId());
        RestVO rest = event.getRest();
        try{
            switch(event.getOperation()){
                case "ADD":
                    luceneIndexService.addOrUpdateDocument(rest);
                    System.out.println("add document: " + rest.getRestId());
                    break;
                case "UPDATE":
                    luceneIndexService.addOrUpdateDocument(rest);
                    System.out.println("update document: " + rest.getRestId());
                    break;
                case "DELETE":
                    luceneIndexService.deleteDocument(rest.getRestId());
                    System.out.println("delete document: " + rest.getRestId());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + event.getOperation());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

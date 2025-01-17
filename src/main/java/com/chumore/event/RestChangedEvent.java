package com.chumore.event;


import com.chumore.rest.model.RestVO;
import org.springframework.context.ApplicationEvent;

/** TODO
 * 1. 功能：當餐廳 (RestVO) 新增或更新時，發送此事件。
 * 2. 搭配 RestEventListener，呼叫 LuceneIndexService 更新索引。
 */

public class RestChangedEvent extends ApplicationEvent {

    private final RestVO rest;
    private final String operation;

    public RestChangedEvent(Object soruce,RestVO rest,String operation){
        super(soruce);
        this.rest = rest;
        this.operation = operation;
    }

    public RestVO getRest() {
        return rest;
    }

    public String getOperation() {
        return operation;
    }

}

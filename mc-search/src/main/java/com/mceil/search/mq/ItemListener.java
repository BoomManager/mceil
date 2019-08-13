package com.mceil.search.mq;

import com.mceil.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ItemListener {
    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.insert.queue",durable = "true"),
            exchange = @Exchange(name = "mc.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.insert"}
    ))
    public void ListenInsertOrUpdate(Long spuId) throws IOException {
        if (spuId ==null){
            return;
        }
        //处理消息，对索引库进行新增或修改
        searchService.createOrUpdateIndex(spuId);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "mc.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void ListenDelete(Long spuId){
        if (spuId ==null){
            return;
        }
        //处理消息，对索引库进行新增或修改
        searchService.deleteIndex(spuId);
    }


}

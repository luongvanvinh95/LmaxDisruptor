package com.example.lmaxdisruptor.consumer;

import com.example.lmaxdisruptor.dto.EventValue;
import com.lmax.disruptor.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class Consumer implements EventHandler<EventValue> {

    @Override
    public void onEvent(EventValue eventValue, long l, boolean b) throws Exception {
//        System.out.println("Process event: " + eventValue);
    }
}

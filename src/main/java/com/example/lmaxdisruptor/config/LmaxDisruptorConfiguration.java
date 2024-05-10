package com.example.lmaxdisruptor.config;

import com.example.lmaxdisruptor.consumer.Consumer;
import com.example.lmaxdisruptor.dto.EventValue;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LmaxDisruptorConfiguration {
    

    @Autowired
    private Consumer consumer;

    @Bean("eventValueRingBuffer")
    public RingBuffer<EventValue> eventValueRingBuffer(Consumer consumer) {
        int bufferSize = (int) Math.pow(2, 8);
        Disruptor<EventValue> disruptor = new Disruptor<>(EventValue::new, bufferSize,
            DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(consumer);
//        disruptor.setDefaultExceptionHandler(transactionExceptionHandler);
        disruptor.start();
        return disruptor.getRingBuffer();
    }


}

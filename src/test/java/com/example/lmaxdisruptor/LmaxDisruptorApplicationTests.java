package com.example.lmaxdisruptor;

import com.example.lmaxdisruptor.dto.EventValue;
import com.lmax.disruptor.RingBuffer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
class LmaxDisruptorApplicationTests {

    @Autowired
    RingBuffer<EventValue> eventValueRingBuffer;

    int capacity = 2_000;
    int numberOfThreads = 3;

    private List<EventValue> eventValueList;

  {
        this.eventValueList = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            EventValue eventValue = new EventValue();
            eventValue.setValue(String.valueOf(i));
            eventValueList.add(eventValue);
        }
    }

    @Test
    void arrayBlockingQueue() {
        ArrayBlockingQueue<EventValue> arrayBlockingQueue = new ArrayBlockingQueue<>(capacity);

        long start = Instant.now().toEpochMilli();
        List<CompletableFuture<EventValue>> futures = new ArrayList<>();
        this.eventValueList.forEach(eventValue -> {
            CompletableFuture<EventValue> future = CompletableFuture.supplyAsync(() -> {
                arrayBlockingQueue.add(eventValue);
                return null;
            }, Executors.newFixedThreadPool(numberOfThreads));
            futures.add(future);
        });

        futures.forEach(eventValueCompletableFuture -> {
            try {
                eventValueCompletableFuture.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


        long end = Instant.now().toEpochMilli();
        System.out.println("Time arrayBlockingQueue: " + String.valueOf(end - start));
    }

    @Test
    void ringBuffer() {

        long start = Instant.now().toEpochMilli();

        List<CompletableFuture<EventValue>> futures = new ArrayList<>();
        this.eventValueList.forEach(eventValue -> {
            CompletableFuture<EventValue> future = CompletableFuture.supplyAsync(() -> {
                long sequence = eventValueRingBuffer.next();
                EventValue eventValue1 = eventValueRingBuffer.get(sequence);
                eventValue1.setValue(eventValue.getValue());
                eventValueRingBuffer.publish(sequence);
                return null;
            }, Executors.newFixedThreadPool(numberOfThreads));
        });

        futures.forEach(eventValueCompletableFuture -> {
            try {
                eventValueCompletableFuture.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        long end = Instant.now().toEpochMilli();
        System.out.println("Time ringBuffer: " + String.valueOf(end - start));
    }
}

package com.shift.akrpc.consumer;

import com.shift.akrpc.common.example.CalcService;
import com.shift.akrpc.consumer.web.controller.TestController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


@WebMvcTest
class AkrpcConsumerApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestController testController;

    @Mock
    CalcService calcService;

    @Test
    void test_test_controller() throws Exception {
        // Set up mock behavior
        Mockito.when(calcService.add(5, 3)).thenReturn(8);
        testController.setCalcService(calcService);

        mockMvc.perform(MockMvcRequestBuilders.get("/test/add").param("a", "5").param("b", "3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(8));
    }

    @Test
    void test_virtual_thread() throws Exception {
        // 创建一个虚拟线程并启动
        ThreadFactory tf = Thread.ofVirtual().factory();
        Thread vThread = tf.newThread(() -> System.out.println("Hello from virtual thread!"));
        vThread.start();

        // 线程安全，AtomicInteger 负责累加，ConcurrentHashMap 负责收集结果
        final AtomicInteger atomic = new AtomicInteger(0);
        final Map<Integer, Integer> map = new ConcurrentHashMap<>();

        // 非线程安全，需要借助线程工具来保证正确性
        final int[] counter = new int[]{0};
        // 线程工具
        ReentrantLock reentrantLock = new ReentrantLock();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 100000; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                reentrantLock.lock();
                counter[0]++;
                reentrantLock.unlock();

                atomic.addAndGet(1);
                map.put(index, index);

                // 模拟一些工作负载
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        System.out.println("await: " +
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS)
        );
        System.out.println("counter: " + counter[0]);
        System.out.println("atomic: " + atomic.get());
        System.out.println("map size: " + map.size());
        Assertions.assertThat(counter[0]).isEqualTo(100000);
    }
}

package com.learning.javabase.lab.concurrent.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * JUC 常用工具实验室
 *
 * 学习目标：
 * 1. CountDownLatch: 等待多线程完成 (一次性)。
 * 2. CyclicBarrier: 多线程相互等待 (可循环)。
 * 3. Semaphore: 控制并发数量 (限流)。
 */
@Slf4j
public class JucToolsStudy {

    public static void main(String[] args) throws InterruptedException {
        // 1. 实验：CountDownLatch (火箭发射倒计时)
        // testCountDownLatch();

        // 2. 实验：CyclicBarrier (集齐七龙珠召唤神龙)
        // testCyclicBarrier();

        // 3. 实验：Semaphore (停车场限流)
        testSemaphore();
    }

    /**
     * 实验 1: CountDownLatch
     * 场景：主线程等待 5 个子线程检查完毕，才能发射火箭。
     */
    private static void testCountDownLatch() throws InterruptedException {
        log.info(">>> 实验 1: CountDownLatch (倒计时) <<<");
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                    log.info("检查员 {} 完成检查", id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 【关键】计数器减 1
                    latch.countDown();
                }
            }).start();
        }

        log.info("主控台：等待所有检查员就位...");
        // 【关键】阻塞等待，直到计数器归零
        latch.await();
        log.info("主控台：所有检查完毕，火箭发射！🚀");
    }

    /**
     * 实验 2: CyclicBarrier
     * 场景：7 个法师收集龙珠，必须等 7 个人都收集到了，才能一起召唤神龙。
     */
    private static void testCyclicBarrier() {
        log.info(">>> 实验 2: CyclicBarrier (循环栅栏) <<<");
        int threadCount = 7;
        
        // 参数 2 是一个 Runnable，当 7 个人都到达屏障时，由最后一个到达的线程执行这个任务
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            log.info("【神龙出现】愿望达成！(屏障被打破)");
        });

        for (int i = 1; i <= threadCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    log.info("法师 {} 出发寻找龙珠...", id);
                    Thread.sleep((long) (Math.random() * 2000));
                    log.info("法师 {} 找到了龙珠，等待其他人...", id);
                    
                    // 【关键】我到了，等其他人。人齐了才能往下走。
                    barrier.await();
                    
                    log.info("法师 {} 看到神龙，回家吃饭。", id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 实验 3: Semaphore
     * 场景：只有 3 个停车位，来了 6 辆车。
     */
    private static void testSemaphore() {
        log.info(">>> 实验 3: Semaphore (信号量限流) <<<");
        int permits = 3; // 3 个许可
        Semaphore semaphore = new Semaphore(permits);

        for (int i = 1; i <= 6; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    // 【关键】抢许可 (抢不到就阻塞)
                    semaphore.acquire();
                    log.info("车辆 {} 抢到了车位，正在停车...", id);
                    
                    Thread.sleep((long) (Math.random() * 2000));
                    
                    log.info("车辆 {} 离开车位。", id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 【关键】释放许可 (让给别人)
                    semaphore.release();
                }
            }).start();
        }
    }
}

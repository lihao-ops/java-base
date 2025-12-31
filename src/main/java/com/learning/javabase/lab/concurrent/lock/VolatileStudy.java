package com.learning.javabase.lab.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * Volatile 关键字深度实验室
 *
 * 学习目标：
 * 1. 【核心】验证可见性 (Visibility): 一个线程改了，另一个线程立马看见。
 * 2. 【核心】验证不保证原子性 (Atomicity): volatile 不能替代 synchronized/Lock。
 */
@Slf4j
public class VolatileStudy {

    // 实验 1 的开关
    // 试着把 volatile 去掉，看看程序会不会死循环？
    private static volatile boolean running = true;

    // 实验 2 的计数器
    private static volatile int count = 0;

    public static void main(String[] args) throws InterruptedException {
        // 1. 实验：可见性验证
        testVisibility();

        // 2. 实验：原子性验证 (打脸现场)
        testAtomicity();
    }

    /**
     * 实验 1: 可见性 (Visibility)
     * 
     * 场景：
     * - 线程 A 不断读取 running 标志位。
     * - 线程 B 修改 running = false。
     * 
     * 预期：
     * - 有 volatile: 线程 A 立马感知，退出循环。
     * - 无 volatile: 线程 A 可能永远卡在循环里 (因为读取的是 CPU 缓存里的旧值)。
     */
    private static void testVisibility() throws InterruptedException {
        log.info(">>> 实验 1: 可见性验证 <<<");
        
        Thread t1 = new Thread(() -> {
            log.info("Reader 线程启动，等待 running 变为 false...");
            while (running) {
                // 空循环，或者做点简单的计算
                // 注意：不要在这里打印日志或 sleep，因为 IO/上下文切换 可能会强制刷新缓存，干扰实验结果
            }
            log.info("Reader 线程感知到了变化！退出循环。");
        }, "Reader");

        t1.start();

        Thread.sleep(1000); // 让 Reader 先跑一会儿
        
        log.info("Main 线程准备修改 running = false");
        running = false;
        log.info("Main 线程修改完成");
    }

    /**
     * 实验 2: 原子性 (Atomicity)
     * 
     * 场景：10 个线程，每个累加 1000 次。
     * 预期：
     * - 如果 volatile 能保证原子性，结果应该是 10000。
     * - 实际上，结果往往小于 10000。
     * 
     * 原因：count++ 不是原子操作！它分为三步：
     * 1. Read: 读取 count
     * 2. Update: count + 1
     * 3. Write: 写入 count
     * volatile 只能保证 Read/Write 是可见的，但不能保证这三步不被打断。
     */
    private static void testAtomicity() throws InterruptedException {
        log.info(">>> 实验 2: 原子性验证 (volatile 的局限性) <<<");
        
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    count++; // 非原子操作！
                }
                latch.countDown();
            }).start();
        }
        
        latch.await();
        log.info("最终 Count: {} (预期: 10000)", count);
        
        if (count < 10000) {
            log.warn("结论验证成功：volatile 不保证原子性！发生了数据丢失。");
        } else {
            log.info("运气真好，居然没丢数据？(建议多跑几次)");
        }
    }
}

package com.learning.javabase.lab.concurrent.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁机制深度对比实验室
 *
 * 学习目标：
 * 1. 掌握 synchronized (内置锁) 的基本用法。
 * 2. 掌握 ReentrantLock (显式锁) 的基本用法。
 * 3. 【核心】对比两者的区别 (灵活性、公平性、中断响应)。
 */
@Slf4j
public class LockStudy {

    // 共享资源 (票数)
    private static int tickets = 100;

    public static void main(String[] args) throws InterruptedException {
        // 1. 实验：使用 synchronized 卖票
        // testSynchronized();

        // 2. 实验：使用 ReentrantLock 卖票
        testReentrantLock();
    }

    /**
     * 实验 1: synchronized (JVM 层面)
     * 
     * 特点：
     * - 自动加锁/释放锁 (代码块结束或异常时)。
     * - 不可中断，非公平。
     * - 锁升级机制 (偏向 -> 轻量 -> 重量)。
     */
    private static void testSynchronized() throws InterruptedException {
        log.info(">>> 实验 1: synchronized 卖票 <<<");
        resetTickets();
        
        Runnable seller = () -> {
            while (true) {
                // 【关键点】锁住 class 对象，保证全局唯一
                synchronized (LockStudy.class) {
                    if (tickets > 0) {
                        try { Thread.sleep(10); } catch (InterruptedException e) {}
                        log.info("{} 卖出一张票，剩余: {}", Thread.currentThread().getName(), --tickets);
                    } else {
                        break; // 卖完了
                    }
                }
            }
        };

        startSellers(seller);
    }

    /**
     * 实验 2: ReentrantLock (JDK 层面)
     * 
     * 特点：
     * - 手动 lock() 和 unlock() (必须在 finally 中释放！)。
     * - 支持公平锁 (new ReentrantLock(true))。
     * - 支持 tryLock() (尝试获取，不行就撤，防止死锁)。
     */
    private static void testReentrantLock() throws InterruptedException {
        log.info(">>> 实验 2: ReentrantLock 卖票 <<<");
        resetTickets();
        
        // 【关键点】创建一个锁对象
        // 参数 true 表示公平锁 (先来后到)，false 表示非公平锁 (默认，性能更好)
        Lock lock = new ReentrantLock(false); 
        
        Runnable seller = () -> {
            while (true) {
                // 1. 手动加锁
                lock.lock();
                try {
                    // 2. 临界区代码
                    if (tickets > 0) {
                        try { Thread.sleep(10); } catch (InterruptedException e) {}
                        log.info("{} 卖出一张票，剩余: {}", Thread.currentThread().getName(), --tickets);
                    } else {
                        break;
                    }
                } finally {
                    // 3. 【绝对重点】必须在 finally 中释放锁！
                    // 否则一旦发生异常，锁永远不会释放，导致死锁。
                    lock.unlock();
                }
            }
        };

        startSellers(seller);
    }

    // --- 辅助方法 ---

    private static void startSellers(Runnable seller) throws InterruptedException {
        Thread t1 = new Thread(seller, "窗口A");
        Thread t2 = new Thread(seller, "窗口B");
        Thread t3 = new Thread(seller, "窗口C");
        
        t1.start(); t2.start(); t3.start();
        
        t1.join(); t2.join(); t3.join();
    }

    private static void resetTickets() {
        tickets = 100;
    }
}

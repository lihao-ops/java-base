package com.learning.javabase.lab.concurrent.lock;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * JOL 锁升级实验室 (Java Object Layout)
 *
 * 学习目标：
 * 1. 亲眼看到对象头 (Mark Word) 的变化。
 * 2. 验证锁升级过程：无锁 -> 轻量级锁 -> 重量级锁。
 * 
 * 注意：JDK 15+ 默认废弃了偏向锁 (Biased Locking)，所以你可能看不到偏向锁状态。
 */
@Slf4j
public class JolLockStudy {

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();

        // 1. 无锁状态 (001)
        log.info(">>> 1. 无锁状态 <<<");
        log.info(ClassLayout.parseInstance(lock).toPrintable());

        // 2. 轻量级锁 (00)
        // 只有一个线程加锁，没有竞争
        log.info(">>> 2. 轻量级锁 (synchronized) <<<");
        synchronized (lock) {
            log.info(ClassLayout.parseInstance(lock).toPrintable());
        }

        // 3. 重量级锁 (10)
        // 制造竞争：两个线程抢同一把锁
        log.info(">>> 3. 重量级锁 (竞争) <<<");
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(2000); // 占着茅坑不拉屎
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        
        // 确保 t1 先拿到锁
        Thread.sleep(100);
        
        // 主线程尝试抢锁 -> 发生竞争 -> 升级为重量级锁
        synchronized (lock) {
            log.info("主线程抢到了锁 (此时应该是重量级锁):");
            log.info(ClassLayout.parseInstance(lock).toPrintable());
        }
    }
}

package com.learning.javabase.lab.concurrent.lock;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * @author hli
 * @program: java-base
 * @Date 2026-01-25 21:14:08
 * @description: synchronize锁升级(JDK17 ： 无锁 — — > 轻量级 — — > 重量级)
 */
public class SynchronizedDeepDive {
    static Object lock;

    public static void main(String[] args) throws InterruptedException {
        // [Step 1] 创建对象
        // 刚 new 出来的对象，没有任何人碰它。
        // 预期状态：无锁 (001)
        lock = new Object();
        System.out.println("====== 1. 新生对象 (无锁) ======");
        // 打印对象头
        System.out.println(ClassLayout.parseInstance(lock).toPrintable());

        // [Step 2] 第一次加锁
        // 主线程自己去拿锁，没有别人跟它抢。
        // JDK 17 预期：直接升级为【轻量级锁】(00) (跳过偏向锁)
        synchronized (lock) {
            System.out.println("====== 2. 主线程加锁 (JDK17 -> 轻量级锁) ======");
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());
        }

        // [Step 3] 制造竞争 (锁膨胀)
        // 我们让子线程先持有锁，主线程再去抢，制造“激烈的竞争”。
        // 预期状态：升级为【重量级锁】(10)
        Thread t = new Thread(() -> {
            synchronized (lock) {
                try {
                    // 子线程拿着锁睡一会，死活不松手
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        // 稍微等子线程先启动并拿到锁
        TimeUnit.MILLISECONDS.sleep(200);

        System.out.println("====== 3. 正在发生竞争 (主线程来抢了...) ======");
        // 主线程也来抢，发现被占用了，只能升级为重量级锁去排队
        synchronized (lock) {
            System.out.println("====== 4. 竞争后的状态 (重量级锁) ======");
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());
        }
    }
}
package com.learning.javabase.lab.collection.map.hashMap;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap 源码调试实验室
 * <p>
 * 核心目标：通过 Debug 亲眼见证 HashMap 的内部运作。
 * <p>
 * 【调试指南】
 * 请根据代码中的 TODO 提示，在 JDK 源码 (java.util.HashMap) 中打上断点。
 */
@Slf4j
public class HashMapStudy {

    public static void main(String[] args) throws InterruptedException {
        // 1. 调试：链表形成 (拉链法)
        debugChaining();

        // 2. 调试：树化过程 (Treeification)
        debugTreeification();

        // 3. 调试：并发冲突 (数据覆盖)
        debugConcurrentRace();
    }

    /**
     * 场景 1: 调试链表形成
     * <p>
     * 目标：观察 putVal 方法中，如何判断 Hash 冲突并追加到链表尾部。
     * <p>
     * 【断点位置】
     * 打开 java.util.HashMap，找到 putVal 方法。
     * 1. 在 `if ((p = tab[i = (n - 1) & hash]) == null)` 处打断点 -> 观察第一次 put。
     * 2. 在 `else { ... if ((e = p.next) == null) { ... p.next = newNode(...) } }` 处打断点 -> 观察链表追加。
     */
    private static void debugChaining() {
        log.info(">>> 1. 准备调试：链表形成 <<<");
        Map<AlwaysCollideKey, String> map = new HashMap<>();

        AlwaysCollideKey k1 = new AlwaysCollideKey("A");
        AlwaysCollideKey k2 = new AlwaysCollideKey("B");

        log.info("Step 1: Put A (首节点)");
        map.put(k1, "Value-A"); // TODO: 此时 tab[i] 为 null，直接存放

        log.info("Step 2: Put B (Hash冲突 -> 链表)");
        map.put(k2, "Value-B"); // TODO: 此时 tab[i] 不为 null，走 else 分支，追加到链表尾部

        log.info("链表调试结束");
    }

    /**
     * 场景 2: 调试树化过程
     * <p>
     * 目标：观察 treeifyBin 方法的触发，以及链表 Node 变为 TreeNode 的过程。
     * <p>
     * 【断点位置】
     * 打开 java.util.HashMap。
     * 1. 在 `putVal` 方法的 `if (binCount >= TREEIFY_THRESHOLD - 1) treeifyBin(tab, hash);` 处打断点。
     * 2. 在 `treeifyBin` 方法内部打断点。
     */
    private static void debugTreeification() {
        log.info(">>> 2. 准备调试：树化过程 <<<");
        Map<Object, Integer> map = new HashMap<>();

        // 这里的 Key 都会发生 Hash 冲突
        // 阈值是 8，所以前 8 个会形成链表
        for (int i = 0; i < 8; i++) {
            map.put(new AlwaysCollideKey(String.valueOf(i)), i);
        }

        log.info("Step 3: Put 第 9 个元素 (触发树化逻辑)");
        // 注意：treeifyBin 不一定会转红黑树，如果数组长度 < 64，它会优先扩容 (resize)。
        // 你可以在 treeifyBin 里看到 `if (n < MIN_TREEIFY_CAPACITY) resize();`
        map.put(new AlwaysCollideKey("Trigger"), 999); // TODO: 在此处 Debug 进入 putVal -> treeifyBin

        log.info("树化调试结束");
    }

    /**
     * 场景 3: 调试并发冲突 (Race Condition)
     * <p>
     * 目标：观察两个线程同时拿到 tab[i] 为 null，然后同时赋值，导致覆盖。
     * <p>
     * 【调试技巧】
     * 这需要“多线程断点”技巧。
     * 1. 在 HashMap.putVal 的 `if ((p = tab[i = (n - 1) & hash]) == null)` 这一行打断点。
     * 2. 右键点击断点，Suspen Policy 选择 "Thread" (而不是 All)，这样一个线程停住不会影响另一个。
     * 3. 启动 Debug。
     * 4. 当 Thread-1 停在断点时，不要放行。
     * 5. 等 Thread-2 也停在断点时（此时它们看到的 tab[i] 都是 null）。
     * 6. 放行 Thread-1 -> 写入数据。
     * 7. 放行 Thread-2 -> 写入数据（覆盖了 Thread-1 的数据！）。
     */
    private static void debugConcurrentRace() throws InterruptedException {
        log.info(">>> 3. 准备调试：并发冲突 <<<");
        final Map<Integer, Integer> map = new HashMap<>();

        // 两个线程都 Put Key=1 (Hash 一样，Index 一样)
        Thread t1 = new Thread(() -> {
            log.info("Thread-1 准备 Put");
            map.put(1, 100); // TODO: 断点 1
            log.info("Thread-1 Put 完成");
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            log.info("Thread-2 准备 Put");
            map.put(1, 200); // TODO: 断点 2
            log.info("Thread-2 Put 完成");
        }, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.info("最终结果: {}", map.get(1)); // 如果是 200 (或 100)，说明发生了覆盖，而不是报错
    }

    // --- 辅助类 ---
    static class AlwaysCollideKey {
        String name;

        public AlwaysCollideKey(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return 1;
        } // 永远冲突

        @Override
        public boolean equals(Object obj) {
            return this.name.equals(((AlwaysCollideKey) obj).name);
        }
    }
}

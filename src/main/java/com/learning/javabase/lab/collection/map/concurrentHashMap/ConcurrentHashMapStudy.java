package com.learning.javabase.lab.collection.map.concurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentHashMap 核心原理实验室
 *
 * 学习目标：
 * 1. 验证线程安全性 (对比 HashMap)。
 * 2. 理解 CAS + synchronized 的锁机制。
 * 3. 掌握原子组合操作 (computeIfAbsent)。
 * 4. 理解 Fail-Safe 迭代器 (弱一致性)。
 * 5. 【调试】全链路源码断点追踪。
 */
@Slf4j
public class ConcurrentHashMapStudy {

    public static void main(String[] args) throws InterruptedException {
        // 1. 实验：并发 Put 安全性验证
        // testThreadSafety();

        // 2. 实验：原子组合操作 (解决 "Check-Then-Act" 问题)
        // testAtomicOperation();

        // 3. 实验：Key/Value 不允许为 Null
        // testNullConstraint();
        
        // 4. 【调试专用】全链路源码断点追踪
        debugPutWorkflow();
    }

    /**
     * 实验 4: 全链路源码断点追踪 (Debug Workflow)
     * 
     * 目标：通过精心构造的 Key，强迫程序走到 putVal 的每一个核心分支。
     * 
     * 【调试步骤】
     * 请在 java.util.concurrent.ConcurrentHashMap 的 putVal 方法中打好以下断点：
     */
    private static void debugPutWorkflow() {
        log.info(">>> 实验 4: 全链路源码断点追踪 <<<");
        
        // 初始容量 16
        ConcurrentHashMap<Object, String> map = new ConcurrentHashMap<>();
        
        // -------------------------------------------------------
        // 场景 A: 桶为空 -> CAS 无锁插入
        // -------------------------------------------------------
        // 预期源码路径：
        // 1. tabAt(tab, i) == null
        // 2. casTabAt(...) 成功
        log.info("Step 1: 插入 Key A (CAS 无锁)");
        map.put("A", "Value-A"); 
        // TODO: 断点位置 -> putVal 源码中 `else if ((f = tabAt(tab, i = (n - 1) & hash)) == null)`
        // 作用：验证当桶为空时，直接利用 CAS 指令插入新节点，无需加锁。

        
        // -------------------------------------------------------
        // 场景 B: 桶不为空 -> Synchronized 加锁插入
        // -------------------------------------------------------
        // 构造一个 Hash 冲突的 Key (假设 "A" 和 "B" 冲突，或者用 AlwaysCollideKey)
        // 这里为了简单，我们用 AlwaysCollideKey
        AlwaysCollideKey k1 = new AlwaysCollideKey("K1");
        AlwaysCollideKey k2 = new AlwaysCollideKey("K2");
        
        map.put(k1, "Value-K1"); // 先占个坑
        
        log.info("Step 2: 插入 Key K2 (Hash冲突 -> Synchronized)");
        map.put(k2, "Value-K2");
        // TODO: 断点位置 -> putVal 源码中 `synchronized (f)`
        // 作用：验证当发生 Hash 冲突时，只锁住当前桶的头节点 (f)，实现分段锁。

        
        // -------------------------------------------------------
        // 场景 C: 遇到扩容 -> 协助扩容 (Help Transfer)
        // -------------------------------------------------------
        // 这是一个高阶场景。当一个线程发现某个桶的节点是 ForwardingNode (hash == MOVED) 时，
        // 它不会傻等，而是会去帮忙一起扩容（搬运数据）。
        
        log.info("Step 3: 触发扩容并协助 (高阶调试)");
        // 1. 先把 map 填满，触发扩容
        // 默认 16，阈值 12。我们插入 12 个元素。
        for (int i = 0; i < 12; i++) {
            map.put(new AlwaysCollideKey("Fill-" + i), "Data");
        }
        
        // 此时 map 应该正在扩容或者已经扩容完成。
        // 为了捕捉 "helpTransfer"，我们需要在扩容进行中插入数据。
        // 这在单线程很难模拟，通常需要多线程配合断点挂起。
        // TODO: 断点位置 -> putVal 源码中 `else if ((fh = f.hash) == MOVED)`
        // 作用：验证当前线程发现 Map 正在扩容时，主动调用 helpTransfer() 加入搬运大军。
    }

    // ... (其他实验方法保持不变) ...
    private static void testThreadSafety() throws InterruptedException {}
    private static void testAtomicOperation() throws InterruptedException {}
    private static void testNullConstraint() {}

    // 辅助类：制造冲突
    static class AlwaysCollideKey {
        String name;
        public AlwaysCollideKey(String name) { this.name = name; }
        @Override public int hashCode() { return 1; } // 永远冲突
        @Override public boolean equals(Object obj) { return this.name.equals(((AlwaysCollideKey) obj).name); }
    }
}

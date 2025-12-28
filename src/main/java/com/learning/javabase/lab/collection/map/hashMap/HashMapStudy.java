package com.learning.javabase.lab.collection.map.hashMap;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * HashMap 核心原理实验室
 *
 * 学习目标：
 * 1. 理解 Hash 算法与索引计算。
 * 2. 【重点】深度观察 Hash 冲突的处理机制 (拉链法)。
 * 3. 【高阶】验证链表转红黑树 (Treeification)。
 * 4. 【高阶】验证多线程 Put 不安全 (数据丢失)。
 * 5. 【陷阱】验证 Key 的可变性导致的内存泄漏。
 */
@Slf4j
public class HashMapStudy {

    public static void main(String[] args) throws InterruptedException {
        // 1. 实验：Hash 算法与索引计算
        // testHashAndIndex();

        // 2. 实验：深度观察 Hash 冲突处理 (拉链法)
        testCollisionHandling();

        // 3. 实验：链表转红黑树
        // testTreeification();

        // 4. 实验：多线程并发 Put 的不安全性
        // testThreadUnsafety();
        
        // 5. 实验：可变 Key 的致命陷阱
        // testMutableKey();
    }

    /**
     * 实验 2: 深度观察 Hash 冲突处理 (拉链法)
     * 
     * 场景：构造多个 hashCode 相同但 equals 不同的 Key。
     * 验证：
     * 1. 它们是否都在同一个 Bucket 里？
     * 2. 后来者是放在链表头还是链表尾？(JDK 1.7 头插法 vs JDK 1.8 尾插法)
     * 3. 如果 Key 的 equals 也相同，是覆盖还是共存？
     */
    private static void testCollisionHandling() {
        log.info(">>> 实验 2: 深度观察 Hash 冲突处理 (拉链法) <<<");
        
        // 1. 准备 3 个 hashCode 一样，但 equals 不一样的对象
        AlwaysCollideKey k1 = new AlwaysCollideKey("A");
        AlwaysCollideKey k2 = new AlwaysCollideKey("B");
        AlwaysCollideKey k3 = new AlwaysCollideKey("C");
        
        // 2. 准备 1 个 hashCode 一样，且 equals 也一样的对象 (模拟重复 Key)
        AlwaysCollideKey k1_duplicate = new AlwaysCollideKey("A");

        Map<AlwaysCollideKey, String> map = new HashMap<>();
        
        // Step 1: 放入第一个
        map.put(k1, "Value-A");
        log.info("Put A 完成");
        
        // Step 2: 放入第二个 (发生碰撞)
        map.put(k2, "Value-B");
        log.info("Put B 完成 (Hash碰撞)");
        
        // Step 3: 放入第三个 (再次碰撞)
        map.put(k3, "Value-C");
        log.info("Put C 完成 (Hash碰撞)");
        
        // Step 4: 放入重复 Key (覆盖)
        map.put(k1_duplicate, "Value-A-Updated");
        log.info("Put A_Duplicate 完成 (覆盖)");

        // --- 验证阶段 ---
        
        log.info("Map Size: {}", map.size()); // 预期：3 (A, B, C)
        log.info("Get A: {}", map.get(k1));   // 预期：Value-A-Updated
        
        // 【黑科技】打印链表结构
        // 我们要亲眼看到：Bucket[1] -> A -> B -> C
        printLinkedListStructure(map);
    }

    // ... (其他实验方法保持不变) ...
    private static void testHashAndIndex() {}
    private static void testTreeification() {}
    private static void testThreadUnsafety() {}
    private static void testMutableKey() {}

    // --- 辅助方法 ---

    /**
     * 【黑科技】打印指定 Map 的内部链表结构
     * 
     * 这是一个非常强大的调试工具，能让你看到 HashMap 内部的真实样子。
     */
    private static void printLinkedListStructure(Map<?, ?> map) {
        try {
            Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            
            for (int i = 0; i < table.length; i++) {
                Object node = table[i];
                if (node == null) continue;
                
                StringBuilder sb = new StringBuilder();
                sb.append("Bucket[").append(i).append("]: ");
                
                // 遍历链表
                while (node != null) {
                    // 反射获取 Key 和 Value
                    Field keyField = node.getClass().getDeclaredField("key");
                    Field valField = node.getClass().getDeclaredField("value");
                    Field nextField = node.getClass().getDeclaredField("next");
                    
                    keyField.setAccessible(true);
                    valField.setAccessible(true);
                    nextField.setAccessible(true);
                    
                    Object key = keyField.get(node);
                    Object val = valField.get(node);
                    
                    sb.append("(").append(key).append("=").append(val).append(") -> ");
                    
                    node = nextField.get(node);
                }
                sb.append("null");
                log.info(sb.toString());
            }
        } catch (Exception e) {
            log.error("打印链表结构失败", e);
        }
    }

    // ... (AlwaysCollideKey 和 Student 类保持不变) ...
    static class AlwaysCollideKey {
        String name;
        public AlwaysCollideKey(String name) { this.name = name; }
        @Override public String toString() { return name; } // 方便打印
        @Override public int hashCode() { return 1; } // 固定 HashCode
        @Override public boolean equals(Object obj) { return this.name.equals(((AlwaysCollideKey) obj).name); }
    }
    
    static class Student { /* ... */ }
}

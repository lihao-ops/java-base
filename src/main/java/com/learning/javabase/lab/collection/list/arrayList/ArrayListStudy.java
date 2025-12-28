package com.learning.javabase.lab.collection.list.arrayList;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ArrayList 深度学习总结
 *
 * 核心知识点：
 * 1. 底层结构：动态数组 (Object[])
 * 2. 扩容机制：1.5倍 (old + old >> 1)
 * 3. 懒加载：JDK 1.8+ 初始为空，第一次 add 才扩容为 10
 * 4. Remove代价：头部/中间删除需要 System.arraycopy 搬运数据，性能差
 * 5. 线程安全：非线程安全 (Fail-Fast)
 */
@Slf4j
public class ArrayListStudy {

    public static void main(String[] args) {
        log.info(">>> ArrayList 学习之旅开始 <<<");

        // 1. 验证懒加载与扩容
        demonstrateGrowth();

        // 2. 演示 Fail-Fast 机制 (并发修改异常)
        demonstrateFailFast();

        // 3. 性能对比：指定容量 vs 默认容量
        demonstrateInitPerformance();

        // 4. 【新增】Remove 性能对比：头部删除 vs 尾部删除
        demonstrateRemovePerformance();
        
        log.info(">>> ArrayList 学习之旅结束 <<<");
    }

    /**
     * 演示 1: 懒加载与扩容机制
     */
    private static void demonstrateGrowth() {
        log.info("--- 1. 验证扩容机制 ---");
        ArrayList<Integer> list = new ArrayList<>();
        log.info("new ArrayList() 完成，当前容量: {}", getCapacity(list)); // 预期 0

        list.add(1);
        log.info("add(1) 完成，当前容量: {}", getCapacity(list)); // 预期 10

        // 填满 10 个
        for (int i = 2; i <= 10; i++) list.add(i);
        log.info("添加10个元素完成，当前容量: {}", getCapacity(list)); // 预期 10

        list.add(11);
        log.info("add(11) 完成 (触发扩容)，当前容量: {}", getCapacity(list)); // 预期 15
    }

    /**
     * 演示 2: Fail-Fast 机制
     * 在遍历过程中修改集合结构，会抛出 ConcurrentModificationException
     */
    private static void demonstrateFailFast() {
        log.info("--- 2. 验证 Fail-Fast 机制 ---");
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        // 错误示范
        try {
            for (String s : list) {
                if ("B".equals(s)) {
                    list.remove(s); // ❌ 错误！在 foreach 中直接调用 list.remove
                }
            }
        } catch (Exception e) {
            log.error("捕获到预期异常: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }

        // 正确示范
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if ("B".equals(s)) {
                it.remove(); // ✅ 正确！使用迭代器的 remove
            }
        }
        log.info("使用 Iterator 安全删除后: {}", list);
    }

    /**
     * 演示 3: 初始化性能对比
     */
    private static void demonstrateInitPerformance() {
        log.info("--- 3. 初始化性能对比: 指定容量 vs 默认容量 ---");
        int count = 1_000_000; // 100 万数据

        // 方式 A: 默认容量 (频繁扩容)
        long start1 = System.currentTimeMillis();
        List<Integer> list1 = new ArrayList<>();
        for (int i = 0; i < count; i++) list1.add(i);
        long end1 = System.currentTimeMillis();
        log.info("默认容量耗时: {} ms (发生了多次数组复制)", (end1 - start1));

        // 方式 B: 指定容量 (零扩容)
        long start2 = System.currentTimeMillis();
        List<Integer> list2 = new ArrayList<>(count);
        for (int i = 0; i < count; i++) list2.add(i);
        long end2 = System.currentTimeMillis();
        log.info("指定容量耗时: {} ms (零扩容，性能更优)", (end2 - start2));
    }

    /**
     * 演示 4: Remove 性能对比 (核心考点)
     * 为什么说 ArrayList 增删慢？其实是“中间/头部”增删慢，尾部增删很快！
     */
    private static void demonstrateRemovePerformance() {
        log.info("--- 4. Remove 性能对比: 头部删除 vs 尾部删除 ---");
        int count = 100_000; // 10万数据 (数据量太大头部删除会非常慢)
        
        // 准备数据
        List<Integer> listA = new ArrayList<>(count);
        for (int i = 0; i < count; i++) listA.add(i);
        List<Integer> listB = new ArrayList<>(listA);

        // 场景 A: 尾部删除 (快，O(1))
        // 就像从书架最末尾拿书，不需要移动其他书
        long start1 = System.currentTimeMillis();
        for (int i = count - 1; i >= 0; i--) {
            listA.remove(i);
        }
        long end1 = System.currentTimeMillis();
        log.info("尾部删除耗时: {} ms (无需搬运数据，极快)", (end1 - start1));

        // 场景 B: 头部删除 (慢，O(n))
        // 就像从书架第一格拿书，后面 99999 本书都要往前挪一格！
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            listB.remove(0); // 每次都删除第一个
        }
        long end2 = System.currentTimeMillis();
        log.info("头部删除耗时: {} ms (每次都触发 System.arraycopy，极慢)", (end2 - start2));
    }

    /**
     * 反射获取容量工具方法
     */
    private static int getCapacity(ArrayList<?> list) {
        try {
            Field dataField = ArrayList.class.getDeclaredField("elementData");
            dataField.setAccessible(true);
            Object[] data = (Object[]) dataField.get(list);
            return data.length;
        } catch (Exception e) {
            return -1;
        }
    }
}

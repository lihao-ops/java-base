package com.learning.javabase.lab.collection;

import com.learning.javabase.lab.collection.list.arrayList.MyArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * MyArrayList (手写版) 扩容机制验证测试
 *
 * 测试目的：
 * 1. 验证手写版的懒加载机制 (Lazy Loading)。
 * 2. 验证手写版的扩容因子 (1.5倍)。
 */
@Slf4j
class MyArrayListGrowTest {

    @Test
    void testMyArrayListGrow() {
        // 场景 1: 无参构造 (懒加载，初始容量为 0)
        MyArrayList<Integer> list = new MyArrayList<>();
        log.info("初始化_无参构造完成|Initialized_default_constructor");

        // 验证初始容量 (预期为 0)
        int initialCapacity = list.getCapacity();
        log.info("初始容量检查|Initial_capacity_check,capacity={}", initialCapacity);
        Assertions.assertEquals(0, initialCapacity, "默认初始容量应为0(懒加载)");

        // 添加第 1 个元素 (触发第一次扩容 -> 10)
        list.add(1);
        int capacityAfterAdd1 = list.getCapacity();
        log.info("添加第1个元素后|After_adding_1st_element,size={},capacity={}", list.size(), capacityAfterAdd1);
        Assertions.assertEquals(10, capacityAfterAdd1, "第一次添加元素后容量应扩为10");

        // 填满 10 个
        for (int i = 2; i <= 10; i++) {
            list.add(i);
        }
        log.info("添加10个元素后|After_adding_10_elements,size={},capacity={}", list.size(), list.getCapacity());

        // 添加第 11 个元素 (触发第二次扩容 -> 15)
        list.add(11);
        int capacityAfterAdd11 = list.getCapacity();
        log.info("添加第11个元素后|After_adding_11th_element,size={},capacity={}", list.size(), capacityAfterAdd11);
        Assertions.assertEquals(15, capacityAfterAdd11, "10扩容后应为15");
    }

    @Test
    void testMyArrayListSpecificCapacity() {
        // 场景 2: 指定初始容量 10 (直接创建长度为10的数组，无懒加载)
        MyArrayList<Integer> list = new MyArrayList<>(10);
        log.info("初始化_指定容量10|Initialized_specific_capacity_10");

        int initialCapacity = list.getCapacity();
        log.info("初始容量检查|Initial_capacity_check,capacity={}", initialCapacity);
        Assertions.assertEquals(10, initialCapacity, "指定容量初始化应直接为10");

        // 填满 10 个
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        // 添加第 11 个 (触发扩容 -> 15)
        list.add(11);
        int finalCapacity = list.getCapacity();
        log.info("添加第11个元素后|After_adding_11th_element,size={},capacity={}", list.size(), finalCapacity);
        Assertions.assertEquals(15, finalCapacity, "10扩容后应为15");
    }
}

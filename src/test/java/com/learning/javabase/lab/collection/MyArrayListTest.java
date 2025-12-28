package com.learning.javabase.lab.collection;

import com.learning.javabase.lab.collection.list.arrayList.MyArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * MyArrayList 测试类
 *
 * 测试目的：
 * 1. 验证动态扩容是否生效。
 * 2. 验证基本增删改查功能。
 */
@Slf4j
class MyArrayListTest {

    @Test
    void testAddAndGrow() {
        // 初始容量为 2
        MyArrayList<String> list = new MyArrayList<>(2);
        
        list.add("A");
        list.add("B");
        log.info("当前大小|Current_size,size={}", list.size());
        Assertions.assertEquals(2, list.size());
        
        // 触发扩容
        list.add("C");
        log.info("触发扩容后大小|Size_after_grow,size={}", list.size());
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals("C", list.get(2));
    }
    
    @Test
    void testGetOutOfBounds() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("A");
        
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(1);
        });
    }
}

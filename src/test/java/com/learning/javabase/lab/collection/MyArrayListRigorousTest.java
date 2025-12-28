package com.learning.javabase.lab.collection;

import com.learning.javabase.lab.collection.list.arrayList.MyArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * MyArrayList 严苛测试 (Rigorous Test)
 *
 * 测试目的：
 * 1. 验证数据完整性 (Data Integrity): 扩容后数据不能丢，顺序不能乱。
 * 2. 验证边界条件 (Boundary): 区分 new MyArrayList(0) 与 new MyArrayList() 的差异。
 * 3. 验证异常处理 (Exception): 越界访问必须抛异常。
 */
@Slf4j
class MyArrayListRigorousTest {

    @Test
    void testDataIntegrityAfterGrow() {
        log.info(">>> 开始测试：数据完整性验证 <<<");
        MyArrayList<String> list = new MyArrayList<>();
        
        // 故意触发多次扩容：0 -> 10 -> 15 -> 22
        int testCount = 20;
        for (int i = 0; i < testCount; i++) {
            list.add("Element-" + i);
        }
        
        log.info("添加完成，当前Size={}, Capacity={}", list.size(), list.getCapacity());
        
        // 验证扩容是否发生
        Assertions.assertTrue(list.getCapacity() >= testCount, "容量必须足够容纳所有元素");
        
        // 【关键】回头检查每一个元素是否还在，且顺序正确
        for (int i = 0; i < testCount; i++) {
            String expected = "Element-" + i;
            String actual = list.get(i);
            if (!expected.equals(actual)) {
                log.error("数据丢失或错乱！Index={}, Expected={}, Actual={}", i, expected, actual);
                Assertions.fail("扩容导致数据错乱");
            }
        }
        log.info("验证通过：所有数据在扩容后依然完好无损。");
    }

    @Test
    void testZeroCapacityVsDefaultCapacity() {
        log.info(">>> 开始测试：空数组差异验证 (new(0) vs new()) <<<");
        
        // 场景 A: 默认构造 (懒加载 -> 10)
        MyArrayList<Integer> defaultList = new MyArrayList<>();
        defaultList.add(1);
        log.info("默认构造 list 添加1个元素后: Capacity={}", defaultList.getCapacity());
        Assertions.assertEquals(10, defaultList.getCapacity(), "默认构造第一次add应扩容为10");

        // 场景 B: 指定0容量 (规矩办事 -> 1)
        // 你的代码逻辑：minCapacity=1, old=0 -> new=0 -> new<min -> new=1
        MyArrayList<Integer> zeroList = new MyArrayList<>(0);
        zeroList.add(1);
        log.info("指定0容量 list 添加1个元素后: Capacity={}", zeroList.getCapacity());
        
        // 这里是 JDK 的特性：如果你明确要 0，它就不会给你 10，而是按需扩容
        Assertions.assertEquals(1, zeroList.getCapacity(), "指定0容量第一次add应扩容为1 (而不是10)");
        
        // 再加一个，触发扩容 1 -> 2 (1 + 0.5取整 = 1, 但不够用，取min=2)
        zeroList.add(2);
        log.info("指定0容量 list 添加第2个元素后: Capacity={}", zeroList.getCapacity());
        Assertions.assertTrue(zeroList.getCapacity() >= 2);
    }

    @Test
    void testOutOfBounds() {
        log.info(">>> 开始测试：越界异常验证 <<<");
        MyArrayList<String> list = new MyArrayList<>();
        list.add("A");

        // 验证 get(-1)
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        
        // 验证 get(size) - 刚好越界
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        
        log.info("验证通过：越界访问正确抛出了异常。");
    }
}

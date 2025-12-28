package com.learning.javabase.lab.collection.list.arrayList;

import java.util.Arrays;

/**
 * 任务：手写 ArrayList 核心逻辑 (JDK 1.8+ 懒加载版)
 * <p>
 * 挑战点：
 * 1. 区分两种空数组 (EMPTY vs DEFAULTCAPACITY_EMPTY)。
 * 2. 实现第一次 add 时的特殊扩容逻辑。
 */
public class MyArrayList<E> {

    // 默认初始容量
    private static final int DEFAULT_CAPACITY = 10;

    // 【JDK源码细节】用于 new ArrayList(0) 的空数组
    private static final Object[] EMPTY_ELEMENTDATA = {};

    // 【JDK源码细节】用于 new ArrayList() 的空数组
    // 作用：为了区分是谁创建的空数组，以便在第一次 add 时知道该扩容多少
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    // 真正存数据的数组
    private Object[] elementData;

    // 实际元素个数
    private int size;

    /**
     * 无参构造：懒加载，只赋值为空数组，不创建实际空间
     */
    public MyArrayList() {
        // TODO: 将 elementData 指向 DEFAULTCAPACITY_EMPTY_ELEMENTDATA
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * 指定容量构造
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            // TODO: 将 elementData 指向 EMPTY_ELEMENTDATA
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    public boolean add(E e) {
        // 1. 确保容量够用
        ensureCapacityInternal(size + 1);

        // 2. 赋值
        elementData[size++] = e;
        return true;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }

    /**
     * 核心扩容入口
     */
    private void ensureCapacityInternal(int minCapacity) {
        // TODO: 【关键逻辑】如果当前是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA (第一次添加)，
        // 那么 minCapacity 应该是 Math.max(DEFAULT_CAPACITY, minCapacity)
        // 也就是至少要扩容到 10。
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        if (minCapacity - elementData.length > 0) {
            grow(minCapacity);
        }
    }

    /**
     * 真正的扩容逻辑
     */
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;

        // TODO: 计算新容量 (1.5倍)
        // 这里的 0 是占位符，请修改为 oldCapacity + (oldCapacity >> 1)
        int newCapacity = oldCapacity + (oldCapacity >> 1);

        // TODO: 检查新容量是否够用 (newCapacity - minCapacity < 0)
        // 如果计算出来的 1.5 倍还不够（比如 addAll 加了很多），就用 minCapacity
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        // 搬家,直接复制所有的元素到扩容的数组中
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private void rangeCheck(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    public int size() {
        return size;
    }

    // 为了方便测试，暴露内部数组长度
    public int getCapacity() {
        return elementData.length;
    }
}

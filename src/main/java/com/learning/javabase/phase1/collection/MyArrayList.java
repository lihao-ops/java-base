package com.learning.javabase.phase1.collection;

import java.util.Arrays;

/**
 * 手写简易版 ArrayList
 *
 * 学习目标 (Phase 1):
 * 1. 理解动态数组的扩容机制。
 * 2. 理解 System.arraycopy 的底层拷贝。
 * 3. 泛型的基本使用。
 *
 * @param <E> 元素类型
 */
public class MyArrayList<E> {

    // 默认初始容量
    private static final int DEFAULT_CAPACITY = 10;
    
    // 存储元素的数组
    private Object[] elementData;
    
    // 当前元素个数
    private int size;

    public MyArrayList() {
        this.elementData = new Object[DEFAULT_CAPACITY];
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = new Object[]{};
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    /**
     * 添加元素
     *
     * 实现思路：
     * 1. 检查是否需要扩容。
     * 2. 插入元素。
     * 3. size + 1。
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);
        elementData[size++] = e;
        return true;
    }

    /**
     * 获取元素
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }

    /**
     * 扩容机制核心
     *
     * 学习重点：
     * - 扩容因子：通常是 1.5 倍 (oldCapacity + oldCapacity >> 1)
     * - 数组拷贝：Arrays.copyOf 底层也是 System.arraycopy
     */
    private void ensureCapacityInternal(int minCapacity) {
        if (minCapacity - elementData.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        // 扩容 1.5 倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        
        // 数组拷贝
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
}

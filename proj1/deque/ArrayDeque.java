package deque;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ArrayDeque<T> {

    private class DequeIterator implements Iterable<T> {

        @Override
        public Iterator<T> iterator() {
            return null;
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Iterable.super.forEach(action);
        }

        @Override
        public Spliterator<T> spliterator() {
            return Iterable.super.spliterator();
        }
    }

    private final static double USER_FACTOR = 0.25;

    private T[] data;

    private int nextFirst;

    private int nextLast;

    private int size;

    public int UtilFunction(int size) {
        if (size < 0) {
            return size + this.data.length;
        }
        if (size >= this.data.length) {
            return size - this.data.length;
        }
        return size;
    }

    public ArrayDeque() {
        /** cast, the way to create reference array */
        data = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public ArrayDeque(ArrayDeque other) {
        data = (T[]) new Object[8];
        size = 0;

        for (int i = 0; i < other.size; i++) {
            addLast((T) other.get(i));
        }
    }

    public void addFirst(T item) {
        if (size == data.length) {
            resize(size + 8);
        }

        data[nextFirst] = item;
        nextFirst = UtilFunction(nextFirst - 1);
        size++;
    }

    /** Resizes the underlying array to the target capacity */
    public void resize(int capacity) {
        T[] newArray = null;
        if (capacity < 16) {
            newArray = (T[]) new Object[capacity];  /**  change capacity */
            System.arraycopy(data, 0, newArray, 0, size);
            data = newArray;
        }else if (size / (double)capacity < 0.25) {
            int newCapacity = (int) (capacity * USER_FACTOR);
            newArray = (T[]) new Object[newCapacity];
            System.arraycopy(data, 0, newArray, 0, size);
            data = newArray;
        }
    }

    public void addLast(T item) {
        if (size == data.length) {
            resize(size + 8);
        }
        data[nextLast] = item;
        nextLast = UtilFunction(nextLast + 1);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void printDeque() {
        int i = nextFirst + 1;
        while (i != nextLast) {
            System.out.println(data[i]);
            i = UtilFunction(i - 1);
        }
    }

    public T removeFirst() {
        nextFirst = UtilFunction(nextFirst + 1);
        T item = data[nextFirst];
        data[nextFirst] = null;
        size--;
        resize(data.length);
        return item;
    }

    public T removeLast() {
        nextLast = UtilFunction(nextLast - 1);
        T item = data[nextLast];
        data[nextLast] = null;
        size--;
        resize(data.length);
        return item;
    }

    public T get(int index) {
        return data[index];
    }

}

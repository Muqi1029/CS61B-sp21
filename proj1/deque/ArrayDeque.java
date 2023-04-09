package deque;

import java.util.Iterator;


public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    /**
     * the iterator Object
     */
    private class ArrayDequeIterator implements Iterator<T> {
        int wizPos = utilFunction(nextFirst + 1);

        @Override
        public boolean hasNext() {
            return wizPos != nextLast;
        }

        @Override
        public T next() {
            T item = data[wizPos];
            wizPos = utilFunction(wizPos + 1);
            return item;
        }
    }

    private static final double USER_FACTOR = 0.25;
    private static final int BASIC_SIZE = 16;

    private T[] data;

    private int nextFirst;

    private int nextLast;

    private int size;

    private int utilFunction(int a) {
        if (a < 0) {
            return a + data.length;
        }
        if (a >= data.length) {
            return a - data.length;
        }
        return a;
    }

    public ArrayDeque() {
        /** cast, the way to create reference array,
         * the starting size of array is 8
         * */
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

    @Override
    public void addFirst(T item) {
        if (size == data.length) {
            resize(size * 2);
        }
        data[nextFirst] = item;
        nextFirst = utilFunction(nextFirst - 1);
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == data.length) {
            resize(size * 2);
        }
        data[nextLast] = item;
        nextLast = utilFunction(nextLast + 1);
        size++;
    }

    /**
     * Resizes the underlying array to the target capacity
     */
    private void resize(int capacity) {
        T[] newData = (T[]) new Object[capacity];
        int k = nextFirst + 1;
        for (int i = 1; i <= size; i++) {
            newData[i] = data[utilFunction(k)];
            k++;
        }
        nextFirst = 0;
        nextLast = size + 1;
        data = newData;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int i = utilFunction(nextFirst + 1);
        while (i != nextLast) {
            System.out.println(data[i]);
            i = utilFunction(i + 1);
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = utilFunction(nextFirst + 1);
        T item = data[nextFirst];
        data[nextFirst] = null;
        size--;
        if (size >= BASIC_SIZE && size < data.length * USER_FACTOR) {
            resize((int) (data.length * USER_FACTOR));
        }
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = utilFunction(nextLast - 1);
        T item = data[nextLast];
        data[nextLast] = null;
        size--;
        if (size >= BASIC_SIZE && size < data.length * USER_FACTOR) {
            resize((int) (data.length * USER_FACTOR));
        }
        return item;
    }

    @Override
    public T get(int index) {
        int i = utilFunction(nextFirst + index + 1);
        return data[i];
    }

    public boolean equals(Object o) {
        if (o instanceof ArrayDeque) {
            o = (ArrayDeque) o;
            if (((ArrayDeque<?>) o).size == size) {
                for (int i = 0; i < size; i++) {
                    if (!(((ArrayDeque<?>) o).get(i) == get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }


}

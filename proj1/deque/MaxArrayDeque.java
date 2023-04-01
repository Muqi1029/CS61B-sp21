package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    /**
     * creates a MaxArrayDeque with the given Comparator
     * @param c The Comparator Object
     */
    public MaxArrayDeque(Comparator<T> c){
        comparator = c;
    }

    /**
     *
     * @param c the comparator Object
     * @return
     * the maximum element in the ArrayDeque
     */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxValue = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), maxValue) > 0) {
                maxValue = get(i);
            }
        }
        return maxValue;
    }

    /**
     *
     * @return
     * the maximum element in the deque as governed by the parameter Comparator c
     * if MaxArrayDeque is empty, simply return null
     */
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maxValue = get(0);
        for (int i = 0; i < size(); i++) {
            if (comparator.compare(get(i), maxValue) > 0) {
                maxValue = get(i);
            }
        }
       return maxValue;
    }
}

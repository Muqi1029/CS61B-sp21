package deque;
public interface Deque<T> {

    /**
     * add an item of type T to the front of the deque
     */
    void addFirst(T item);

    /**
     * add an item of type T to the back of the deque
     * @param item
     */
    void addLast(T item);


    /**
     *
     * @return the number of items in the deque
     */
    int size();

    /**
     * prints the items in the deque from first to last,
     * separate by a space.
     * Once all the items have been printed, print out a new line
     */
    void printDeque();

    /**
     * removes and returns the item at the front of the deque.
     * if no such item exists, returns null
     * @return
     */
    T removeFirst();

    /**
     * removes and returns the item at the back of the deque.
     * if no such item exists, returns null
     * @return
     */
    T removeLast();

    /**
     *
     * @return true if deque is empty, false otherwise
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    T get(int index);

}

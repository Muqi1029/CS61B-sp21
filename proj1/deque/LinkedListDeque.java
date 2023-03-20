package deque;

public class LinkedListDeque<T> {

    public LinkedListDeque() {
        this.sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(LinkedListDeque other) {
        this.sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;

        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }

    }


    public T getRecursive(int index) {
        return getRecursive(index, sentinel.next);
    }

    public T getRecursive(int index, Node next) {
        if (index == 0) {
            return next.item;
        }
        return getRecursive(index - 1, next.next);
    }

    private class Node {
        T item;

        Node prev;

        Node next;

        public Node() {
            this.item = null;
        }

        public Node(T item) {
            this.item = item;
        }
    }

    private int size;
    private Node sentinel;

    public void addFirst(T item) {
        Node node = new Node(item);

        node.next = sentinel.next;
        sentinel.next.prev = node;

        sentinel.next = node;
        node.prev = sentinel;

        size++;
    }

    public void addLast(T item) {
        size++;
        Node node = new Node(item);

        sentinel.prev.next = node;
        node.prev = sentinel.prev;

        node.next = sentinel;
        sentinel.prev = node;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node node = sentinel;
        while (node.next.item != null) {
            node = node.next;
            System.out.println(node.item);
        }
    }

    public T removeFirst() {
        if (size == 0) return null;
        Node first = sentinel.next;

        sentinel.next = first.next;
        first.next.prev = sentinel;
        size--;
        return first.item;
    }

    public T removeLast() {
        if (size == 0) return null;
        Node last = sentinel.prev;

        sentinel.prev = last.prev;
        last.prev.next = sentinel;

        size--;
        return last.item;
    }


    public T get(int index) {
        int i = 0;
        Node node = sentinel;
        while (i <= index) {
            node = node.next;
            i++;
        }
        return node.item;
    }
}

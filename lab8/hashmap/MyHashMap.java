package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size;

    private int initialSize = 16;

    private double loadFactor = 0.75;

    private final HashSet<K> ks = new HashSet<>();

    /**
     * Constructors
     */
    public MyHashMap() {
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        this.buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            this.buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    @Override
    public void clear() {
        size = 0;
        ks.clear();
        for (int i = 0; i < buckets.length; i++) {
            buckets[i].clear();
        }
    }

    /**
     * -------------------- containsKey(key) -------------------------
     */
    @Override
    public boolean containsKey(K key) {
        int index = key.hashCode();
        index = index < 0 ? -index : index;
        for (Node node : buckets[index % initialSize]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ---------------------- get(key) -----------------------
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key can't be null");
        }
        int index = key.hashCode();
        index = index < 0 ? -index : index;

        for (Node node :
                buckets[index % initialSize]) {
            if (key.equals(node.key)) {
                return node.value;
            }
        }
        return null;
    }

    /**
     * ----------------------- size() ------------------------
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * ---------------------- put(key, value) ---------------------
     */
    //TODO increase the size of your MyHashMap when the load factor exceeds the set loadFactor
    // multiplicative increase
    @Override
    public void put(K key, V value) {

        /** basic judge */
        if (key == null) {
            throw new IllegalArgumentException("Key is allowed to be null!");
        }

        /** 1. get index of buckets */
        int index = key.hashCode() % initialSize;
        index = index < 0 ? -index : index;

        /** 2. search in the relevant bucket */
        for (Node node : buckets[index]) {
            if (key.equals(node.key)) {
                node.value = value;
                return;
            }
        }
        /** 3. if there is no findings in the bucket, directly add the node into the bucket */
        ks.add(key);
        buckets[index].add(createNode(key, value));
        size++;

        if (size / (double) buckets.length > loadFactor) {
            resizeCapacity(buckets.length * 2);
        }
    }

    /** resize the capacity */
    private void resizeCapacity(int size) {
        Collection<Node>[] newBuckets = new Collection[size];
        for (int i = 0; i < size; i++) {
            newBuckets[i] = createBucket();
        }

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int index = node.key.hashCode() % size;
                index = index < 0 ? -index : index;
                newBuckets[index].add(node);
            }
        }

        buckets = newBuckets;
    }

    /**
     * ------------------------ keySet() --------------------------
     */
    @Override
    public Set<K> keySet() {
        return ks;
    }

    /**
     * ------------------------ remove() --------------------------
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * ---------------------- iterator() --------------------------
     */
    @Override
    public Iterator<K> iterator() {
        return new hashMapIterator();
    }

    private class hashMapIterator implements Iterator<K> {
        private Set<K> keys;
        private int pos;

        private Iterator<K> iterator;

        public hashMapIterator() {
            keys = ks;
            iterator = keys.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public K next() {
            return iterator.next();
        }
    }
}

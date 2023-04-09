package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.lang.Comparable;

/**
 *
 * @param <K>
 * @param <V>
 * @author mq
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;
    private int size;


    /**
     * Node
     */
    private class BSTNode {
        private BSTNode left, right;
        private K key;
        private V value;

        private int size;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "BSTNode{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return containsKey(node.right, key);
        }else if (cmp < 0) {
            return containsKey(node.left, key);
        }else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {

        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);

        if (cmp > 0) {
            return get(node.right, key);
        } else if (cmp < 0) {
            return get(node.left, key);
        } else {
            return node.value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key mustn't be null");
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value) {

        if (node == null) {
            size++;
            return new BSTNode(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }


    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * there are totally 3 situations about deletion
     * @param key the key
     * @return the key's value
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * prints out BSTMap in order of increasing Key
     */
    public void printInOrder() {
        print(root);
    }


    private void print(BSTNode node) {
        if (node == null) {
            return;
        }
        print(node.left);
        System.out.println(node.toString());
        print(node.right);
    }
}

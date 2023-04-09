package bstmap;

import java.util.HashSet;
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

    HashSet<K> ks = new HashSet<>();


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

        public BSTNode(K key, V value, int i) {
            this.key = key;
            this.value = value;
            this.size = i;
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
        return size(root);
    }

    private int size(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key mustn't be null");
        }
        ks.add(key);
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value) {

        if (node == null) {
            return new BSTNode(key, value, 1);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }


    @Override
    public Set<K> keySet() {
        return ks;
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
        System.out.println(node);
        print(node.right);
    }
}

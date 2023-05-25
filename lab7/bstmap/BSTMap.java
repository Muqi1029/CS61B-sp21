package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 * @param <K>
 * @param <V>
 * @author mq
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    /**
     * the root the binary search tree
     */
    private BSTNode root;

    /**
     * internal Node
     */
    private class BSTNode {
        private BSTNode left, right; // left and right subtrees
        private K key; // associated key
        private V value;
        private int size; // number of nodes in subtree

        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }

        @Override
        public String toString() {
            return "BSTNode{" + "key=" + key + ", value=" + value + '}';
        }
    }

    @Override
    public void clear() {
        root = null;
    }

    /** ============== containsKey(key) ============================= */
    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }
    private boolean containsKey(BSTNode node, K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is not allowed to be null.");
        }

        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return containsKey(node.right, key);
        } else if (cmp < 0) {
            return containsKey(node.left, key);
        } else {
            return true;
        }
    }

    /** ====================== get(key) ======================== */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is allowed to be null.");
        }
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

    /** ================ size =============================*/
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

    /** ================= put ======================= */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key isn't allowed to be null.");
        }
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
            // equal
            node.value = value;
        }
        node.size = size(node.left) + 1 + size(node.right);
        return node;
    }


    /** ================= keySet() ============================ */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }


    /** ==================== remove(key) ======================= */
    /**
     * there are totally 3 situations about deletion
     *
     * @param key the key
     * @return the key's value
     */
    @Override
    public V remove(K key) {
        /** return null if the key does not exist in the BSTMap */
        if (!containsKey(key)) {
            return null;
        }
        V value = get(key);
        root = remove(key, root);
        return value;
    }

    private BSTNode remove(K key, BSTNode node) {
        // exit
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            // right subtree
            node.right = remove(key, node.right);
        } else if (cmp < 0) {
            // left subtree
            node.left = remove(key, node.left);
        } else {
            // find!  IMPORTANT POINT: node is the final return value
            /** situation 1: this node is a leaf node */
            if (node.left == null && node.right == null) {
                node = null;
            } else if (node.left == null) {
                /** situation 2: there is only a child of this node */
                node = node.right;
            } else if (node.right == null) {
                node = node.left;
            } else {
                /** situation 3: there are two children of this node */
                /** 1. here we choose to find the successor of this node to replace itself */
                BSTNode successor = node.right;
                while (successor.left != null) {
                    successor = successor.left;
                }
                /** 2. delete this successor, this situation is similar to the first 2 situations */
                node.right = deleteMin(node.right);

                /** 3. replace node with its successor  */
                successor.left = node.left;
                successor.right = node.right;

                node = successor;
            }
        }
        if (node != null) {
            node.size = size(node.left) + size(node.right) + 1;
        }
        return node;
    }

    private BSTNode deleteMin(BSTNode node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return node;
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        }
        return remove(key);
    }

    /** =================== iterator  ============================== */
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /** ====================== print in order ===========================  */
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

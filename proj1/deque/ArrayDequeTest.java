package deque;

import org.junit.Test;

import java.util.Random;

public class ArrayDequeTest {

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        arrayDeque.addFirst(2);
        arrayDeque.addFirst(3);
        for (int item : arrayDeque) {
            System.out.println(item);
        }
    }

    @Test
    public void randomTest() {
        Random random = new Random();

        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();

        int N = 10000;
        for (int i = 0; i < N; i++) {
            int k = random.nextInt(4);
            int randomNum = random.nextInt(1000);
            switch (k) {
                case 0 -> {
                    arrayDeque.addLast(randomNum);
                    linkedListDeque.addLast(randomNum);
                }
                case 1 -> {
                    if (arrayDeque.size() > 0) {
                        Integer integer1 = arrayDeque.removeLast();
                        Integer integer2 = linkedListDeque.removeLast();
                        assert integer2.equals(integer1);
                    }
                }
                case 2 -> {
                    arrayDeque.addFirst(randomNum);
                    linkedListDeque.addFirst(randomNum);
                }
                case 3 -> {
                    if (arrayDeque.size() > 0) {
                        Integer integer1 = arrayDeque.removeFirst();
                        Integer integer2 = linkedListDeque.removeFirst();
                        assert integer1.equals(integer2);
                    }
                }
            }
        }
    }


}

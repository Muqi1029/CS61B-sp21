package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void removeLastTest() {
        AListNoResizing<Integer> alist = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        for (int i = 0; i < 4; i++) {
            alist.addLast(i);
            buggyAList.addLast(i);
        }

        for (int i = 0; i < alist.size(); i++) {
            Integer i1 = alist.removeLast();
            Integer i1_ = buggyAList.removeLast();
            assertTrue(i1.equals(i1_));
        }
    }

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        correct.addLast(5);
        correct.addLast(10);
        correct.addLast(15);

        broken.addLast(5);
        broken.addLast(10);
        broken.addLast(15);

        assertEquals(correct.size(), broken.size());

        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }
        }
    }

    @Test
    public void moreRandomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<Integer>();
        int N = 500;
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0,3);
            if (operationNumber == 0) {
                if (L.size() > 0 ){
                    Integer integer = L.removeLast();
                    System.out.println("removeLast(" + integer + ")");
                }
            }else if (operationNumber == 1) {
                if (L.size() > 0) {
                    Integer last = L.getLast();
                    System.out.println("getLast(" + last + ")");
                }
            }else {
                int randomizedNumber = StdRandom.uniform(0, 100);
                L.addLast(randomizedNumber);
                System.out.println("addLast(" + randomizedNumber + ")");
            }
        }
    }

    @Test
    public void randomizedComparisons() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0,3);
            if (operationNumber == 0) {
                if (L.size() > 0 ){
                    Integer integer = L.removeLast();
                    Integer binteger = B.removeLast();
                    assertTrue(integer.equals(binteger));
                    System.out.println("removeLast(" + integer + ")");
                }
            }else if (operationNumber == 1) {
                if (L.size() > 0) {
                    Integer last = L.getLast();
                    Integer blast = B.getLast();
                    assertTrue(last.equals(blast));
                    System.out.println("getLast(" + last + ")");
                }
            }else {
                int randomizedNumber = StdRandom.uniform(0, 100);
                L.addLast(randomizedNumber);
                B.addLast(randomizedNumber);
                System.out.println("addLast(" + randomizedNumber + ")");
            }
        }

    }
}



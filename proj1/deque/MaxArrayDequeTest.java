package deque;

import org.junit.Test;

import java.util.Comparator;

public class MaxArrayDequeTest<T> {

    @Test
    public void MaxArrayDequeComparatorTest() {
        Dog dog1 = new Dog("dog1", 12);
        Dog dog2 = new Dog("dog2", 13);
        Dog dog3 = new Dog("dog3", 1);

        Comparator<Dog> nc = Dog.getNameComparator();
        Comparator sc = Dog.getSizeComparator();

        MaxArrayDeque<Dog> dogs = new MaxArrayDeque<Dog>(nc);
        dogs.addFirst(dog1);
        dogs.addFirst(dog2);
        dogs.addFirst(dog3);

        System.out.println(dogs.max(nc));

    }


}

package deque;

import java.util.Comparator;

public class Dog implements Comparable<Dog> {

    private String name;

    private Integer size;

    public Dog(String name, Integer size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public int compareTo(Dog o) {
        return this.size - o.size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", size=" + size +
                '}';
    }

    private static class NameComparator implements Comparator<Dog> {
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.name.compareTo(o2.name);
        }
    }


    public static Comparator getNameComparator() {
        return new NameComparator();
    }


    private static class SizeComparator implements Comparator<Dog>{
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.size - o2.size;
        }
    }

    public static Comparator getSizeComparator() {
        return new SizeComparator();
    }


}

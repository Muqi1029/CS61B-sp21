package test;

public class Sort {

    public static void sort(String[] x) {

        /** 1. find the smallest item */
//        int smallestIndex = findSmallest(x);

        /** 2. move it to front */
//        swap(x, 0, smallestIndex);

        /** 3. Selection sort the remaining N-1 items */
        sort(x, 0);

    }

    public static void sort(String[] x, int start) {
        if (start < x.length) {
            int smallestIndex = findSmallest(x, start);
            swap(x, start, smallestIndex);
            sort(x, start + 1);
        }
    }


    /** swap item a and b */
    public static void swap(String[] x, int a, int b) {
        String temp = x[b];
        x[b] = x[a];
        x[a] = temp;
    }

    /** return the smallest String in x */
    public static int findSmallest(String[] x, int start) {
        int smallestIndex = start;

        for (int i = start; i < x.length; i++) {
            int cmp = x[i].compareTo(x[smallestIndex]);
            /** from the Internet, if x[i] < x[smallestIndex], return -1  */
            if (cmp < 0) {
                smallestIndex = i;
            }
        }
        return smallestIndex;
    }
}

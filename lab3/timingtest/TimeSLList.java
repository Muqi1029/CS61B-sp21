package timingtest;
import edu.princeton.cs.algs4.Stopwatch;




/**
 * Created by hug.
 */
public class TimeSLList {


    private static AList<Integer> aList = new AList<Integer>(); /** record the ops */
    private static AList<Double> doubleAList = new AList<Double>();/** record the time */
    private static final int M = 10000;

    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {

        aList.of(1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000);
        AList<Integer> alistM = new AList<>();
        for (int i = 0; i < aList.size(); i++) {
            alistM.addLast(M);
        }

        timeGetLast();
        printTimingTable(aList, doubleAList, alistM);

    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE

        int i = 0;

        while (i < aList.size()) {
            /** get the time of invoking getLast */
            int N = aList.get(i);
            i++;

            /** step1: create a slist */
            SLList<Integer> slist = new SLList<>();

            /** step2: add N times to the SLList */
            for (int j = 0; j < N; j++) {
                slist.addLast(i);
            }

            /** step3: start the timer */
            Stopwatch sw = new Stopwatch();

            /** step4: perform M getLast operations */
            for (int j = 0; j < M; j++) {
                slist.getLast();
            }
            double v = sw.elapsedTime();
            doubleAList.addLast(v);

        }

    }

}

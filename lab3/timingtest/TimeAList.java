package timingtest;

import edu.princeton.cs.algs4.Stopwatch;



/**
 * Created by hug.
 */
public class TimeAList {


    private static AList<Integer> aList = new AList<Integer>(); /** record the ops */
    private static AList<Double> doubleAList = new AList<Double>();



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
        timeAListConstruction();
        printTimingTable(aList, doubleAList, aList);
    }

    public static void timeAListConstruction() {

        int i = 0;
        while (i < aList.size()) {
            int N = aList.get(i);
            i++;
            AList<Integer> Ns = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < N; j++) {
                Ns.addLast(1);
            }
            double v = sw.elapsedTime();
            doubleAList.addLast(v);
        }

    }
}

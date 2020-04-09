package cloneDetection;

public class CloneSample2 {
    public void sample() {
        final int a = 10;
        final int[] b = new int[a];
        final PrintStream out = System.out;
        for (int i = 0; i < a; i++) {
            b[i] = i;
            out.println(b[i]);
        }
    }
}

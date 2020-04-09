package cloneDetection;

// Four ForStatements are detected as a clone set in this and CloneSample2.java file.
public class CloneSample1 {
    private static final int ARRAY_LENGTH = 10;

    public void sample() {
        final int a = 10;
        final int[] b = new int[a];
        for (int i = 0; i < a; i++) {
            b[i] = i;
            System.out.println(b[i]);
        }

        final int[] c = new int[11];
        for (int j = 0; j < c.length; j++) {
            c[j] = j;
            System.out.println(c[j]);
        }

        final int[] d = new int[ARRAY_LENGTH];
        for (int k = 0; k < this.ARRAY_LENGTH; k++) {
            d[k] = k;
            System.out.println(d[k]);
        }
    }
}

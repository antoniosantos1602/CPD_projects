import java.io.*;

import javax.swing.plaf.synth.SynthLookAndFeel;

class MatrixMultiplication {

    int n_cols;
    double[] pha;
    double[] phb;
    double[] phc;

    public MatrixMultiplication(int n_cols) {
        this.n_cols = n_cols;
    }

    public void OnMult() {
        this.createMatrix();
        phc = new double[this.n_cols * this.n_cols];

        long start = System.currentTimeMillis();
        for (int i = 0; i < n_cols; i++) {
            for (int j = 0; j < n_cols; j++) {
                for (int k = 0; k < n_cols; k++) {
                    phc[i * n_cols + j] += pha[i * n_cols + k] * phb[k * n_cols + j];
                }
            }
        }

        long finish = System.currentTimeMillis();

        double elapsedTime = (double) (finish - start) / 1000;

        /*
         * System.out.print("Result matrix with " + this.n_cols + " size: ");
         * for (int i = 0; i < 1; i++) {
         * for (int j = 0; j < 10; j++)
         * System.out.print(phc[j] + " ");
         * }
         * System.out.println(" ");
         * System.out.println("Time: " + elapsedTime + " seconds");
         * System.out.println(" ");
         */

        System.out.println(this.n_cols + " " + elapsedTime);
    }

    public void OnMultLine() {
        this.createMatrix();
        phc = new double[this.n_cols * this.n_cols];

        long start = System.currentTimeMillis();
        for (int i = 0; i < n_cols; i++) {
            for (int k = 0; k < n_cols; k++) {
                for (int j = 0; j < n_cols; j++) {
                    phc[i * n_cols + j] += pha[i * n_cols + k] * phb[k * n_cols + j];
                }
            }
        }
        long finish = System.currentTimeMillis();

        double elapsedTime = (double) (finish - start) / 1000;

        /*
         * System.out.print("Result matrix with " + this.n_cols + " size: ");
         * for (int i = 0; i < 1; i++) {
         * for (int j = 0; j < 10; j++)
         * System.out.print(phc[j] + " ");
         * }
         * System.out.println(" ");
         * System.out.println("Time: " + elapsedTime + " seconds");
         * System.out.println(" ");
         */

        System.out.println(this.n_cols + " " + elapsedTime);

    }

    public void createMatrix() {
        pha = new double[this.n_cols * this.n_cols];
        phb = new double[this.n_cols * this.n_cols];

        for (int i = 0; i < this.n_cols; i++)
            for (int j = 0; j < this.n_cols; j++)
                pha[i * this.n_cols + j] = 1.0;

        for (int i = 0; i < this.n_cols; i++)
            for (int j = 0; j < this.n_cols; j++)
                phb[i * this.n_cols + j] = i + 1.0;
    }

    public static void main(String[] args) {

        System.out.println("Normal multiplication: ");
        for (int i = 600; i <= 3000; i += 400) {
            MatrixMultiplication m = new MatrixMultiplication(i);
            m.OnMult();
        }

        System.out.println("Line multiplication: ");
        for (int i = 600; i <= 3000; i += 400) {
            MatrixMultiplication m = new MatrixMultiplication(i);
            m.OnMultLine();
        }
    }

}

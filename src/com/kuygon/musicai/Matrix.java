package com.kuygon.musicai;

public class Matrix {
    private double[][] m;

    public Matrix(double[][] matrix) {
        this.m = matrix;
    }

    public Matrix(Matrix matrix) {
        this.m = matrix.m;
    }

    public void scale (double scalar) {
        for (int row = 0; row < m.length; row++) {
            for (int col = 0; col < m[0].length; col++) {
                m[row][col] *= scalar;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int row = 0; row < m.length; row++) {
            if (row > 0)
                s.append(",");
            s.append("[");
            for (int col = 0; col < m[0].length; col++) {
                if (col > 0)
                    s.append(", ");
                s.append(m[row][col]);
            }
            s.append("]");
        }
        s.append("]");
        return s.toString();
    }
}

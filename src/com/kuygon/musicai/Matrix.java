package com.kuygon.musicai;

import java.util.function.Function;

public class Matrix {
    private double[][] m;

    public Matrix(double[][] matrix) {
        this.m = matrix;
    }

    public Matrix(Matrix matrix) {
        int rowLength = matrix.m[0].length;
        this.m = new double[matrix.m.length][rowLength];
        for(int i = 0; i < matrix.m.length; i++)
        {
            System.arraycopy(matrix.m[i], 0, this.m[i], 0, rowLength);
        }
    }

    public Matrix(int rows, int cols) {
        m = new double[rows][cols];
    }

    public int getRows() {
        return m.length;
    }

    public int getCols() {
        return m[0].length;
    }

    public void setCell(int row, int col, double value) {
        m[row][col] = value;
    }

    public double getCell(int row, int col) {
        return m[row][col];
    }

    public void scale (double scalar) {
        for (int row = 0; row < m.length; row++) {
            for (int col = 0; col < m[0].length; col++) {
                m[row][col] *= scalar;
            }
        }
    }

    public void applyFunction (Function<Double, Double> f) {
        for (int row = 0; row < m.length; row++) {
            for (int col = 0; col < m[0].length; col++) {
                m[row][col] = f.apply(m[row][col]);
            }
        }
    }

    public static Matrix add (Matrix m1, Matrix m2) throws Exception {
        if (m1.m.length != m2.m.length || m1.m[0].length != m2.m[0].length)
            throw new Exception("M1 and M2 are not the same size");
        double[][] m3 = new double[m1.m.length][m1.m[0].length];

        for (int row = 0; row < m1.m.length; row++) {
            for (int col = 0; col < m1.m[0].length; col++) {
                m3[row][col] = m1.m[row][col] + m2.m[row][col];
            }
        }
        return new Matrix(m3);
    }

    public static Matrix subtract (Matrix m1, Matrix m2) throws Exception {
        if (m1.m.length != m2.m.length || m1.m[0].length != m2.m[0].length)
            throw new Exception("M1 and M2 are not the same size");
        double[][] m3 = new double[m1.m.length][m1.m[0].length];

        for (int row = 0; row < m1.m.length; row++) {
            for (int col = 0; col < m1.m[0].length; col++) {
                m3[row][col] = m1.m[row][col] - m2.m[row][col];
            }
        }
        return new Matrix(m3);
    }

    public static Matrix product (Matrix m1, Matrix m2) throws Exception {
        if (m1.m[0].length != m2.m.length)
            throw new Exception("M1 columns doesn't match M2 rows");
        double[][] m3 = new double[m1.m.length][m2.m[0].length];

        for (int row = 0; row < m1.m.length; row++) {
            for (int col = 0; col < m2.m[0].length; col++) {
                double cellValue = 0.0;
                for (int cell = 0; cell < m1.m[row].length; cell++) {
                    cellValue += m1.m[row][cell] * m2.m[cell][col];
                }
                m3[row][col] = cellValue;
            }
        }
        return new Matrix(m3);
    }

    public static Matrix transpose (Matrix m1) {
        double[][] m2 = new double[m1.m[0].length][m1.m.length];
        for (int row = 0; row < m1.m.length; row++) {
            for (int col = 0; col < m1.m[0].length; col++) {
                m2[col][row] = m1.m[row][col];
            }
        }
        return new Matrix(m2);
    }



    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int row = 0; row < m.length; row++) {
            if (row > 0)
                s.append(", ");
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

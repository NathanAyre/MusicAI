package com.kuygon.musicai;

public class Test {
    public static void main(String[] args) {
        double[][] m = {{1, 2, 3}, {4, 5, 6}};
        Matrix testMatrix = new Matrix(m);
        System.out.println(testMatrix);

        testMatrix.scale(10);
        System.out.println(testMatrix);
    }
}

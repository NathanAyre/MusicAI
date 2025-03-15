package com.kuygon.musicai;

public class Test {
    public static void main(String[] args) throws Exception {
        double[][] m = {{1, 2, 3}, {4, 5, 6}};
        Matrix testMatrix = new Matrix(m);
        System.out.println(testMatrix + "\n");

        testMatrix.scale(10);
        System.out.println(testMatrix + "\n");
        Matrix testMatrix2 = Matrix.transpose(testMatrix);
        System.out.println(testMatrix2 + "\n");
        Matrix productResult = Matrix.product(testMatrix, testMatrix2);
        System.out.println(productResult + "\n");
        productResult.applyFunction(Test::myFunction);
        System.out.println(productResult + "\n");

        Layer myLayer = new Layer(2, 1);
        myLayer.setInput(0, 1.0);
        myLayer.setInput(1, 0.5);
        myLayer.propagate();
        System.out.println(myLayer);
    }

    public static Double myFunction(double value) {
        value = Math.sqrt(value);
        return value;
    }
}

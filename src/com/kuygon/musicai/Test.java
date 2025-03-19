package com.kuygon.musicai;

public class Test {
    public static void main(String[] args) throws Exception {
        matrixTest();
        layerTest();
        leftRightTest();
    }

    public static void layerTest() throws Exception {
        Layer myLayer = new Layer(2, 1);
        myLayer.setInputs(new Matrix(new double[][]{{1.0, 0.5}}));
        myLayer.propagate();
        System.out.println(myLayer+"\n");

        myLayer.setErrors(new Matrix(new double[][]{{-1.0}}));
        myLayer.backPropagate(0.1);
        myLayer.propagate();
        System.out.println(myLayer+"\n");
    }

    public static void matrixTest() throws Exception {
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
    }

    public static void leftRightTest() throws Exception {
        Layer myLayer = new Layer(2, 1);
        Matrix left = new Matrix(new double[][]{{1.0, 0.0}});
        Matrix right = new Matrix(new double[][]{{0.0, 1.0}});
        for (int step = 0; step < 100; step++) {
            myLayer.setInputs(left);
            myLayer.propagate();
            double output = myLayer.getOutput(0);
            double error1 = output - 1.0;
            myLayer.setErrors(new Matrix(new double[][]{{error1}}));
            myLayer.backPropagate(2);

            myLayer.setInputs(right);
            myLayer.propagate();
            output = myLayer.getOutput(0);
            double error2 = output;
            myLayer.setErrors(new Matrix(new double[][]{{error2}}));
            myLayer.backPropagate(2);

            double avgError = Math.sqrt((error1*error1 + error2*error2) / 2);
            System.out.println(step + ": " + avgError);
        }

        myLayer.setInputs(left);
        myLayer.propagate();
        double output = myLayer.getOutput(0);
        System.out.println(left + ": " + output);

        myLayer.setInputs(right);
        myLayer.propagate();
        output = myLayer.getOutput(0);
        System.out.println(right + ": " + output);

        myLayer.setInputs(Matrix.add(left, right));
        myLayer.propagate();
        output = myLayer.getOutput(0);
        System.out.println(Matrix.add(left, right) + ": " + output);

        System.out.println(myLayer);
    }

    public static Double myFunction(double value) {
        value = Math.sqrt(value);
        return value;
    }
}

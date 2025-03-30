package com.kuygon.musicai;

public class Test {
    public static void main(String[] args) throws Exception {
        matrixTest();
        layerTest();
        leftRightLayerTest();
        leftRightNetworkTest();
        dftTest();
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
        Matrix productResult = Matrix.multiply(testMatrix, testMatrix2);
        System.out.println(productResult + "\n");
        productResult.applyFunction(Test::myFunction);
        System.out.println(productResult + "\n");
    }

    public static void leftRightLayerTest() throws Exception {
        Layer myLayer = new Layer(2, 1);
        Matrix left = new Matrix(new double[][]{{1.0, 0.0}});
        Matrix right = new Matrix(new double[][]{{0.0, 1.0}});
        for (int step = 0; step < 100; step++) {
            myLayer.setInputs(left);
            myLayer.propagate();
            double output = myLayer.getOutput(0);
            double error1 = output - 1.0;
            myLayer.setErrors(new Matrix(new double[][]{{error1}}));
            myLayer.backPropagate(0.1);

            myLayer.setInputs(right);
            myLayer.propagate();
            output = myLayer.getOutput(0);
            double error2 = output;
            myLayer.setErrors(new Matrix(new double[][]{{error2}}));
            myLayer.backPropagate(0.1);

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

    public static void leftRightNetworkTest() throws Exception {
        Network myNetwork = new Network(2, 1, new int[]{4});
        Matrix left = new Matrix(new double[][]{{1.0, 0.0}});
        Matrix right = new Matrix(new double[][]{{0.0, 1.0}});
        for (int step = 0; step < 1000; step++) {
            myNetwork.setInputs(left);
            myNetwork.propagate();
            double output = myNetwork.getOutput(0);
            double error1 = output - 1.0;
            myNetwork.setErrors(new Matrix(new double[][]{{error1}}));
            myNetwork.backPropagate(0.1);

            myNetwork.setInputs(right);
            myNetwork.propagate();
            output = myNetwork.getOutput(0);
            double error2 = output;
            myNetwork.setErrors(new Matrix(new double[][]{{error2}}));
            myNetwork.backPropagate(0.1);

            double avgError = Math.sqrt((error1*error1 + error2*error2) / 2);
            System.out.println(step + ": " + avgError);
        }

        myNetwork.setInputs(left);
        myNetwork.propagate();
        double output = myNetwork.getOutput(0);
        System.out.println(left + ": " + output);

        myNetwork.setInputs(right);
        myNetwork.propagate();
        output = myNetwork.getOutput(0);
        System.out.println(right + ": " + output);

        myNetwork.setInputs(Matrix.add(left, right));
        myNetwork.propagate();
        output = myNetwork.getOutput(0);
        System.out.println(Matrix.add(left, right) + ": " + output);

        System.out.println(myNetwork);
    }

    public static void dftTest() {
        double[] data = new double[256];
        for (int i = 0; i < data.length; i++) {
            //data[i] = (i < 128 ? -1 : +1);
            //data[i] = Math.sin(2 * Math.PI * i / data.length);
            data[i] = Math.random() * 2 - 1;
        }

        Complex[] results = AudioProcessor.discreteFourierTransform(data);
        for (Complex result : results) {
            System.out.println(result.getLength());
        }
    }

    public static Double myFunction(double value) {
        value = Math.sqrt(value);
        return value;
    }
}

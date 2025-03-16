package com.kuygon.musicai;

public class Layer {
    private Matrix weights;
    private Matrix input;
    private Matrix output;
    private Matrix error;

    public Layer(int inputs, int outputs) {
        input = new Matrix(1, inputs);
        output = new Matrix(1, outputs);
        error = new Matrix(1, outputs);
        weights = new Matrix(inputs, outputs);
        weights.applyFunction(Layer::randomise);
    }

    public void setInputs(Matrix input) {
        this.input = input;
    }

    public Matrix getOutputs() {
        return output;
    }

    public void setInput(int inputNum, double value) {
        input.setCell(0, inputNum, value);
    }

    public double getOutput(int outputNum) {
        return output.getCell(0, outputNum);
    }

    public void setErrors(Matrix error) {
        this.error = error;
    }

    public static Double randomise(Double value) {
        return Math.random() * 2.0 - 1.0;
    }

    // it gets real here

    public void propagate() throws Exception {
        output = Matrix.product(input, weights);
        output.applyFunction(Layer::sigmoid);
    }

    private static Double sigmoid(Double value) {
        return (Math.tanh(2.0*value)+1.0)/2.0;
    }

    @Override
    public String toString() { // todo: make toString for Layer prettier
        return "Layer{" +
                "weights=" + weights +
                ", input=" + input +
                ", output=" + output +
                '}';
    }
}

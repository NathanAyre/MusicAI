package com.kuygon.musicai;

public class Layer {
    private Matrix weights;
    private Matrix input;
    private Matrix output;
    private Matrix error;
    private Matrix delta;

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

    public int getSize() {
        return output.getCols();
    }

    public Matrix getOutputs() {
        return output;
    }

    public Matrix getDeltas() {
        return delta;
    }

    public Matrix getWeights() {
        return weights;
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

    public void backPropagate(double rate) throws Exception {
        delta = new Matrix(output);
        delta.applyFunction(Layer::sigmoidGrad);
        for (int col = 0; col < delta.getCols(); col++) {
            delta.setCell(0, col, delta.getCell(0, col) * error.getCell(0, col));
        }

        Matrix tempInput = Matrix.transpose(input);
        Matrix deltaW = Matrix.product(tempInput, delta);
        deltaW.scale(rate);
        weights = Matrix.subtract(weights, deltaW);
        weights.applyFunction(Layer::limit);
    }

    private static Double sigmoid(Double value) {
        return (Math.tanh(1.5*value)+1.0)/2.0;
    }

    private static Double limit(Double value) {
        return Math.clamp(value, -1.0, 1.0);
    }

    private static Double sigmoidGrad(Double value) {
        return 4.0*value*(1.0-value);
    }

    @Override
    public String toString() {
        return "Layer{" +
                "\nweights=" + weights +
                ",\ninput=" + input +
                ",\noutput=" + output +
                '}';
    }
}

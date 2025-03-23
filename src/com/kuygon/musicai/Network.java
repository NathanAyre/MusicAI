package com.kuygon.musicai;

import java.util.Arrays;

public class Network {
    Layer[] layers;

    public Network(int numInputs, int numOutputs, int[] layerSizes) {
        int inputs = numInputs;
        layers = new Layer[layerSizes.length + 1];
        for (int layer = 0; layer < layers.length - 1; layer++) {
            layers[layer] = new Layer(inputs, layerSizes[layer]);
            inputs = layerSizes[layer];
        }

        layers[layers.length - 1] = new Layer(inputs, numOutputs);
    }

    public void setInputs(Matrix input) {
        this.layers[0].setInputs(input);
    }

    public double getOutput(int outputNum) {
        return getOutputs().getCell(0, outputNum);
    }

    public Matrix getOutputs() {
        return layers[layers.length - 1].getOutputs();
    }

    public void propagate() throws Exception {
        for (int layer = 0; layer < layers.length; layer++) {
            layers[layer].propagate();
            if (layer < layers.length - 1) {
                Matrix outputs = layers[layer].getOutputs();
                layers[layer + 1].setInputs(outputs);
            }
        }
    }

    public void setErrors(Matrix errors) {
        layers[layers.length - 1].setErrors(errors);
    }

    public void backPropagate(double rate) throws Exception {
        for (int layer = layers.length - 1; layer >= 0; layer--) {
            layers[layer].backPropagate(rate);
            Matrix delta = layers[layer].getDeltas();
            if (layer != 0) {
                Matrix weights = layers[layer].getWeights();
                Matrix errors = new Matrix(1, layers[layer - 1].getSize());
                for (int col = 0; col < errors.getCols(); col++) {
                    double value = 0;
                    for (int outputNeuron = 0; outputNeuron < weights.getCols(); outputNeuron++) {
                        value += weights.getCell(col, outputNeuron) * delta.getCell(0, outputNeuron);
                    }
                    errors.setCell(0, col, value);
                }

                layers[layer - 1].setErrors(errors);
            }
        }
    }

    @Override
    public String toString() {
        return "Network{" +
                "layers=" + Arrays.toString(layers) +
                '}';
    }
}

package com.kuygon.musicai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Network {
    Layer[] layers;

    public Network(int numInputs, int numOutputs, int[] numOfNeurons) {
        int inputs = numInputs;
        layers = new Layer[numOfNeurons.length + 1];
        for (int layer = 0; layer < layers.length - 1; layer++) {
            layers[layer] = new Layer(inputs, numOfNeurons[layer]);
            inputs = numOfNeurons[layer];
        }

        layers[layers.length - 1] = new Layer(inputs, numOutputs);
    }

    public Network(Matrix[] weights) {
        int numInputs = weights[0].getRows();
        int numOutputs = weights[weights.length - 1].getCols();

        int[] numOfNeurons = new int[weights.length - 1];
        for (int i = 0; i < weights.length - 1; i++) {
            numOfNeurons[i] = weights[i].getCols();
        }

        this(numInputs, numOutputs, numOfNeurons);

        for (int i = 0; i < weights.length; i++) {
            layers[i].setWeights(weights[i]); // :D
        }
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

    public Matrix getLayerOutputs(int layerNum) {
        return layers[layerNum].getOutputs();
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
        return Arrays.toString(layers);
    }

    public void saveToFile(String filename) throws IOException {
        File f = new File(filename);
        FileOutputStream os = new FileOutputStream(f);
        os.write(this.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static Network loadFromFile(String filename) throws IOException {
        ArrayList<Matrix> weights = new ArrayList<>();
        FileReader f = new FileReader(filename);
        BufferedReader reader = new BufferedReader(f);
        String line = reader.readLine();
        line = reader.readLine();
        while (line != null) {
            line = line.strip();
            String subline = line.substring(1, line.length() - 2);
            int start = -1;
            int end = -1;
            ArrayList<double[]> rows = new ArrayList<>();
            for (int i = 0; i < subline.length(); i++) {
                if (subline.charAt(i) == '[') start = i + 1;
                if (subline.charAt(i) == ']') {
                    end = i - 1;
                    String[] values = subline.substring(start, end).split(",");
                    double[] actualValues = new double[values.length];
                    for (int j = 0; j < values.length; j++) {
                        actualValues[j] = Double.parseDouble(values[j]);
                    }
                    rows.add(actualValues);
                }
            }

            double[][] m = new double[rows.size()][];
            m = rows.toArray(m);
            Matrix weightMatrix = new Matrix(m);
            weights.add(weightMatrix);

            line = reader.readLine();
        }

        return new Network(weights.toArray(new Matrix[0]));
    }
}

package com.kuygon.musicai;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {
//        matrixTest();
//        layerTest();
//        leftRightLayerTest();
//        Network network = leftRightNetworkTest(null);
//        double time = System.currentTimeMillis();
//        dftTest();
//        System.out.println(System.currentTimeMillis() - time);
//        System.out.println();
//        fftTest();
//        // sampleTest();
//        // spectrumTest();
//        AudioProcessor.Notes notes = notesTest();
//        // saveNetworkTest(network);
//        wordsTest(notes);
//        embeddingsModelTest();
//        retrieveEmbeddingsTest();
        compareEmbeddingsTest();
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

    public static Network leftRightNetworkTest(Network myNetwork) throws Exception {
        Matrix left = new Matrix(new double[][]{{1.0, 0.0}});
        Matrix right = new Matrix(new double[][]{{0.0, 1.0}});

        if (myNetwork == null) {
            myNetwork = new Network(2, 1, new int[]{4});
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

                double avgError = Math.sqrt((error1 * error1 + error2 * error2) / 2);
                System.out.println(step + ": " + avgError);
            }
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
        return myNetwork;
    }

    public static void saveNetworkTest(Network network) throws Exception {
        network.saveToFile("results/test.txt");
        network = Network.loadFromFile("results/test.txt");
        leftRightNetworkTest(network);
    }

    public static void dftTest() {
        double[] data = new double[1024];
        for (int i = 0; i < data.length; i++) {
            //data[i] = (i < 128 ? -1 : +1);
            data[i] = Math.sin(2 * Math.PI * i / data.length);
            //data[i] = Math.random() * 2 - 1;
        }

        Complex[] results = AudioProcessor.discreteFourierTransform(data);
        for (Complex result : results) {
            //System.out.println(result.getLength());
        }
    }

    public static void fftTest() {
        double[] data = new double[1024];
        for (int i = 0; i < data.length; i++) {
            //data[i] = (i < 128 ? -1 : +1);
            data[i] = Math.sin(2 * Math.PI * i / data.length);
            //data[i] = Math.random() * 2 - 1;
        }

        double time = System.currentTimeMillis();

        Complex[] results = AudioProcessor.fastFourierTransform(data);
        time = System.currentTimeMillis() - time;
        for (Complex result : results) {
            System.out.println(result.getLength());
        }

        System.out.println(time);
    }

    public static void sampleTest() throws UnsupportedAudioFileException, IOException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File("resources/440Hz.wav"));
        AudioProcessor.Sample sample = AudioProcessor.readSample(stream, 512);
        System.out.println(sample);
    }

    public static void spectrumTest() throws UnsupportedAudioFileException, IOException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File("resources/dtmf-3.wav"));
        AudioProcessor.Sample sample = AudioProcessor.readSample(stream, 4096);
        assert sample != null;
        AudioProcessor.Spectrum spectrum = AudioProcessor.sampleToSpectrum(sample);
        System.out.println(spectrum);
    }

    public static AudioProcessor.Notes notesTest() throws UnsupportedAudioFileException, IOException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File("resources/Wagner_Tristan_opening_(orchestral).wav"));
        AudioProcessor.Sample sample = AudioProcessor.readSample(stream, 4096*2*16);
        assert sample != null;
        AudioProcessor.Spectrum spectrum = AudioProcessor.sampleToSpectrum(sample);
        AudioProcessor.Notes notes = AudioProcessor.spectrumToNotes(spectrum);
        System.out.println(notes);

        return notes;
    }

    public static void wordsTest(AudioProcessor.Notes notes) {
        AudioProcessor.MusicalWord musicalWord = AudioProcessor.notesToMusicalWord(notes);
        System.out.println(musicalWord);
    }

    public static void embeddingsModelTest() throws Exception {
        AudioEmbeddings.createModel("test_files/training", "results/musicEmbeddings.txt");
    }

    public static void retrieveEmbeddingsTest() throws Exception {
        AudioEmbeddings model = new AudioEmbeddings("results/musicEmbeddings.txt");
        double[] embeddings = model.generateEmbeddings("test_files/training/01 Francesca da Rimini - Prologue Op.25.wav");
        System.out.println(Arrays.toString(embeddings));
    }

    public static void compareEmbeddingsTest() throws Exception {
        AudioEmbeddings model = new AudioEmbeddings("results/musicEmbeddings.txt");
        File directory = new File("test_files/training");
        File[] files = directory.listFiles();

        assert files != null;
        ArrayList<double[]> allEmbeddings = new ArrayList<>();

        for (File file : files) {
            double[] embeddings = model.generateEmbeddings(file.getPath());
            allEmbeddings.add(embeddings);
            System.out.println(file.getPath());
        }

        for (double[] embeddings1 : allEmbeddings) {
            StringBuffer s = new StringBuffer();
            for (double[] embeddings2 : allEmbeddings) {
                double dotProduct = 0;
                double m1 = 0;
                double m2 = 0;
                for (int i = 0; i < embeddings1.length; i++) {
                    dotProduct += embeddings1[i] * embeddings2[i];
                    m1 += Math.pow(embeddings1[i], 2.0);
                    m2 += Math.pow(embeddings2[i], 2.0);
                }

                dotProduct /= Math.sqrt(m1) * Math.sqrt(m2);
                s.append(Math.round(dotProduct * 1000.0) / 1000.0).append(", ");
            }

            System.out.println(s);
        }
    }

    public static Double myFunction(double value) {
        value = Math.sqrt(value);
        return value;
    }
}

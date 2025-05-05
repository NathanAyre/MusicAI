package com.kuygon.musicai;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class AudioEmbeddings {
    Network model;

    public record Pair(AudioProcessor.MusicalWord word1, AudioProcessor.MusicalWord word2) {

    }

    public AudioEmbeddings(String filename) throws IOException {
        model = Network.loadFromFile(filename);
    }

    public double[] generateEmbeddings(String musicFilename) throws Exception {
        // open the music file as an AudioInputStream
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(musicFilename));

        // read a set of samples from the file
        AudioProcessor.fastForward(stream, 20.0); // fast-forward 20 seconds into the file to avoid silence
        int bytes = AudioProcessor.timeAsSampleSize(stream.getFormat(), 0.25);
        bytes = Integer.highestOneBit(bytes); // fft needs a power of 2 to work

        Matrix totalEmbeddings = new Matrix(1, model.layers[0].getSize());

        for (int i = 0; i < 120; i++) {
            AudioProcessor.Sample sample = AudioProcessor.readSample(stream, bytes);
            if (sample == null) break;

            // convert each sample into a musical word
            AudioProcessor.Spectrum spectrum = AudioProcessor.sampleToSpectrum(sample);
            AudioProcessor.Notes notes = AudioProcessor.spectrumToNotes(spectrum);
            AudioProcessor.MusicalWord word = AudioProcessor.notesToMusicalWord(notes);

            // feed the word to our model as inputs (it's hungry)
            double[][] m = new double[1][120];
            m[0] = word.amplitudes();

            model.setInputs(new Matrix(m));

            // get the outputs from the first layer of the model and return (these are the embeddings)
            model.propagate();
            Matrix embeddings = model.getLayerOutputs(0);

            // add this to the composite embeddings vector
            totalEmbeddings = Matrix.add(totalEmbeddings, embeddings);
        }

        // scale the embeddings vector by 1 / (number of words) to get the average
        totalEmbeddings.scale(1.0 / 120.0);

        return totalEmbeddings.getRow(0);
    }

    public static void createModel(String musicLocation, String modelFilename) throws Exception {
        // get a list of all files in the music location
        File directory = new File(musicLocation);
        File[] files = directory.listFiles();

        ArrayList<Pair> wordPairs = new ArrayList<>();

        // for each file
        for (File file : files) {
            // open file and skip forward
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            System.out.println(file.getPath());
            AudioProcessor.fastForward(stream, 20.0); // fast-forward 20 seconds into the file to avoid silence
            int bytes = AudioProcessor.timeAsSampleSize(stream.getFormat(), 0.25);
            bytes = Integer.highestOneBit(bytes); // fft needs a power of 2 to work

            AudioProcessor.MusicalWord previousWord = null;

            // for each quarter-second of music for 30 seconds in total
            for (int i = 0; i < 120; i++) {
                AudioProcessor.Sample sample = AudioProcessor.readSample(stream, bytes);
                if (sample == null) break;

                // convert into a music word
                AudioProcessor.Spectrum spectrum = AudioProcessor.sampleToSpectrum(sample);
                AudioProcessor.Notes notes = AudioProcessor.spectrumToNotes(spectrum);
                AudioProcessor.MusicalWord word = AudioProcessor.notesToMusicalWord(notes);

                // if it isn't the first word in the music
                if (i != 0) {
                    // pair it with the preceding word and add to the training set
                    wordPairs.add(new Pair(previousWord, word));
                }
                previousWord = word;
            }
        }


        // create a new model with random weights
        Network model = new Network(120, 120, new int[]{50});
        double avgError = Double.MAX_VALUE;
        double prevAvgError = 0;

        // while model error is greater than target threshold
        while (Math.abs(avgError - prevAvgError) > 0.0001) {
            // for each word pair
            prevAvgError = avgError;
            avgError = 0;
            for (Pair pair : wordPairs) {
                // set the first word as the input to the model
                double[][] m = new double[1][120];
                m[0] = pair.word1.amplitudes();

                model.setInputs(new Matrix(m));

                // propagate the word through the model
                model.propagate();

                // get the output and compare with the expected word to create an error
                Matrix out = model.getOutputs();
                Matrix errorMatrix = new Matrix(out.getRows(), out.getCols());
                m[0] = pair.word2.amplitudes();
                errorMatrix = Matrix.subtract(out, new Matrix(m));
                double error = 0;

                for (int i = 0; i < errorMatrix.getCols(); i++) {
                    error += Math.pow(errorMatrix.getCell(0, i), 2);
                }

                error = Math.sqrt(error / errorMatrix.getCols());
                avgError += error;
                // back propagate this error through the model
                model.setErrors(errorMatrix);
                model.backPropagate(0.01);
            }
            avgError /= wordPairs.size();
            System.out.println(avgError);
        }

        // SAVE THE MODEL!!!! (VERY IMPORTANT)
        model.saveToFile(modelFilename);
    }
}

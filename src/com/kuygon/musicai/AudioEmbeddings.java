package com.kuygon.musicai;

import java.io.IOException;

public class AudioEmbeddings {
    Network model;

    public AudioEmbeddings(String filename) throws IOException {
        model = Network.loadFromFile(filename);
    }

    public double[] generateEmbeddings(String musicFilename) {
        // open the music file as an AudioInputStream

        // read a set of samples from the file

        // convert each sample into a musical word

        // feed the word to our model as inputs (it's hungry)

        // get the outputs from the first layer of the model and return (these are the embeddings)

        return null;
    }

    public static void createModel(String musicLocation, String modelFilename) {
        // get a list of all files in the music location

        // for each file

            // read 30 seconds from middle of music file

            // for each quarter-second of music

                // convert into a music word

                // if it isn't the first word in the music

                    // pair it with the preceding word and add to the training set

        // create a new model with random weights

        // while model error is greater than target threshold

            // for each word pair

                // set the first word as the input to the model

                // propagate the word through the model

                // get the output and compare with the expected word to create an error

                // back propagate this error through the model

        // SAVE THE MODEL!!!! (VERY IMPORTANT)
    }
}

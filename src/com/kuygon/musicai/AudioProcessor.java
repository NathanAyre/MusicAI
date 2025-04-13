package com.kuygon.musicai;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioProcessor {
    public record Notes(double[][] amplitudes) {
        public static String[] NOTES = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

        public static int frequencyToNoteNum(double frequency) {
            return (int) Math.round(12*Math.log(frequency/440) / Math.log(2) + 57);
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int octaveNum = 0; octaveNum < 10; octaveNum++) {
                for (int noteNum = 0; noteNum < 12; noteNum++) {
                    s.append(NOTES[noteNum] + octaveNum + ": " + amplitudes[octaveNum][noteNum] + "\n");
                }
            }
            return s.toString();
        }
    }

    public record Sample(double[] amplitudes, double sampleFreq) {
        public String toString() {
            StringBuilder s = new StringBuilder("Sample frequency = " + sampleFreq + "\n");
            for (int i = 0; i < amplitudes.length; i++) {
                s.append(amplitudes[i]).append("\n");
            }
            return s.toString();
        }
    }
    public record Spectrum(double[] amplitudes, double sampleFreq) {
        public String toString() {
            StringBuilder s = new StringBuilder("Sample frequency = " + sampleFreq + "\n");
            for (int i = 0; i < amplitudes.length; i++) {
                s.append(amplitudes[i]).append("\n");
            }
            return s.toString();
        }
    }

    public static Spectrum sampleToSpectrum(Sample sample) {
        Complex[] complexValues = fastFourierTransform(sample.amplitudes);
        double[] amplitudes = new double[complexValues.length];
        for (int i = 0; i < amplitudes.length; i++) {
            amplitudes[i] = complexValues[i].getLength();
        }

        return new Spectrum(amplitudes, sample.sampleFreq);
    }

    public static Notes spectrumToNotes(Spectrum spectrum) {
        double[][] notes = new double[10][12];
        //for each frequency in the spectrum
        for (int frequencyInterval = 0; frequencyInterval < spectrum.amplitudes.length; frequencyInterval++) {
            //calculate the frequency of this note and convert to a noteNum and octaveNum
            double freq = spectrum.sampleFreq * frequencyInterval / spectrum.amplitudes.length;
            int noteNum = Notes.frequencyToNoteNum(freq);
            int octaveNum = noteNum / 12;
            noteNum = noteNum % 12;

            //if we've reached the end of the audio spectrum, break out of the loop
            if (octaveNum >= 10) break;

            //add its amplitude to the appropriate note in the notes array
            if (noteNum >= 0 && octaveNum >= 0) notes[octaveNum][noteNum] += spectrum.amplitudes[frequencyInterval];
        }

        //return the notes array as a new Notes record :D
        return new Notes(notes);
    }

    public static Sample readSample(AudioInputStream inputStream, int sampleSize) {
        double[] data = new double[sampleSize];
        AudioFormat format = inputStream.getFormat();
        byte[] bytes = null;
        try {
            bytes = inputStream.readNBytes(sampleSize * format.getFrameSize());
        } catch (IOException e) {
            return null;
        }

        if (bytes.length < sampleSize * format.getFrameSize()) return null;

        ByteOrder order = (format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

        int valueSize = format.getFrameSize() / format.getChannels();

        for (int i = 0; i < data.length; i++) {
            for (int channel = 0; channel < format.getChannels(); channel++) {
                byte[] sampleBytes = new byte[valueSize];
                System.arraycopy(bytes, (i * format.getChannels() + channel) * valueSize, sampleBytes, 0, valueSize);

                if (valueSize == 1)
                    data[i] += (double) (sampleBytes[0] - Byte.MAX_VALUE) / Byte.MAX_VALUE;
                else if (valueSize == 2)
                    data[i] += (double) ByteBuffer.wrap(sampleBytes).order(order).getShort() / Short.MAX_VALUE;
                else if (valueSize == 4)
                    data[i] += (double) ByteBuffer.wrap(sampleBytes).order(order).getInt() / Integer.MAX_VALUE;
                else if (valueSize == 8)
                    data[i] += (double) ByteBuffer.wrap(sampleBytes).order(order).getLong() / Long.MAX_VALUE;
            }

            data[i] /= format.getChannels();
        }

        return new Sample(data, format.getSampleRate());
    }

    public static Complex[] discreteFourierTransform(double[] data) {
        int n = data.length;
        Complex[] results = new Complex[n];
        for (int step = 0; step < n; step++) {
            results[step] = new Complex();
            for (int sample = 0; sample < n; sample++) {
                Complex item = new Complex(data[sample] / n, -2 * Math.PI * step * sample / n);
                results[step] = Complex.add(results[step], item);
            }
        }
        return results;
    }

    public static Complex[] fastFourierTransform(double[] data) {
        int n = data.length;
        Complex[] zData = new Complex[n];
        for (int i = 0; i < n; i++) {
            zData[i] = new Complex(data[i] / n, 0);
        }

        return rawFFT(zData);
    }

    private static Complex[] rawFFT(Complex[] zData) {
        int n = zData.length;
        if (n == 1) return zData;
        if (n % 2 == 1) throw new RuntimeException("n is not a power of 2 :(");

        Complex[] odds = new Complex[n / 2];
        Complex[] evens = new Complex[n / 2];

        for (int i = 0; i < n / 2; i++) {
            odds[i] = zData[2 * i + 1];
            evens[i] = zData[2 * i];
        }

        odds = rawFFT(odds);
        evens = rawFFT(evens);

        Complex[] results = new Complex[n];

        for (int i = 0; i < n / 2; i++) {
            double theta = - 2 * i * Math.PI / n;
            Complex zOdd = Complex.multiply(odds[i], new Complex(1, theta));
            results[i] = Complex.add(evens[i], zOdd);
            results[i + n / 2] = Complex.subtract(evens[i], zOdd);
        }

        return results;
    }
}

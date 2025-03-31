package com.kuygon.musicai;

public class AudioProcessor {
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

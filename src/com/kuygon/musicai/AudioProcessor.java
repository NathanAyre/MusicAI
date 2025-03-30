package com.kuygon.musicai;

public class AudioProcessor {
    public static Complex[] discreteFourierTransform(double[] data) {
        int n = data.length;
        Complex[] results = new Complex[n];
        for (int step = 0; step < n; step++) {
            results[step] = new Complex();
            for (int sample = 0; sample < n; sample++) {
                Complex item = new Complex(data[sample] / n, 2 * Math.PI * step * sample / n);
                results[step] = Complex.add(results[step], item);
            }
        }
        return results;
    }
}

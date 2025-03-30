package com.kuygon.musicai;

public class Complex {
    double re;
    double im;

    public Complex() {
        re = 0;
        im = 0;
    }

    public Complex(Complex z) {
        this.im = z.im;
        this.re = z.re;
    }

    public Complex(double r, double theta) {
        this.im = r * Math.sin(theta);
        this.re = r * Math.cos(theta);
    }

    public double getRe() {
        return re;
    }

    public void setRe(double re) {
        this.re = re;
    }

    public double getIm() {
        return im;
    }

    public void setIm(double im) {
        this.im = im;
    }

    public double getAngle() {
        return Math.atan2(im, re);
    }

    public double getLength() {
        return Math.hypot(im, re);
    }

    public static Complex add(Complex z1, Complex z2) {
        Complex result = new Complex();
        result.re = z1.re + z2.re;
        result.im = z1.im + z2.im;
        return result;
    }

    public static Complex subtract(Complex z1, Complex z2) {
        Complex result = new Complex();
        result.re = z1.re - z2.re;
        result.im = z1.im - z2.im;
        return result;
    }

    public static Complex multiply(Complex z1, Complex z2) {
        return new Complex(z1.getLength() * z2.getLength(), z1.getAngle() + z2.getAngle());
    }

    public static Complex divide(Complex z1, Complex z2) {
        return new Complex(z1.getLength() / z2.getLength(), z1.getAngle() - z2.getAngle());
    }
}

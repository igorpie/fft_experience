package helper;

public class Complex {
    public float re;
    public float im;

    public Complex() {
        this(0, 0);
    }

    public Complex(float r, float i) {
        re = r;
        im = i;
    }

    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }

    public Complex sub(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }

    public Complex mult(Complex b) {
        return new Complex(this.re * b.re - this.im * b.im,
                this.re * b.im + this.im * b.re);
    }


    public static Complex conjugate(Complex b) {
        return new Complex(b.re, -b.im);
    }

    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
}

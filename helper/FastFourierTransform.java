package helper;
import java.util.List;
import static java.lang.Math.*;

public class FastFourierTransform {
    public static void fft(Complex[] buffer) {

        int bits = (int) (log(buffer.length) / log(2));
        for (int j = 1; j < buffer.length / 2; j++) {

            int swapPos = FftHelper.bitReverse(j, bits);
            Complex temp = buffer[j];
            buffer[j] = buffer[swapPos];
            buffer[swapPos] = temp;
        }

        for (int N = 2; N <= buffer.length; N <<= 1) {
            for (int i = 0; i < buffer.length; i += N) {
                for (int k = 0; k < N / 2; k++) {

                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer[evenIndex];
                    Complex odd = buffer[oddIndex];

                    double term = (-2 * PI * k) / (double) N;
                    Complex exp = (new Complex((float) cos(term), (float) sin(term)).mult(odd));

                    buffer[evenIndex] = even.add(exp);
                    buffer[oddIndex] = even.sub(exp);
                }
            }
        }
    }

    public static void ifft(Complex[] buffer) {
        FftHelper.conjugateArray(buffer);
        fft(buffer);
        for (Complex c : buffer) {
            c.re /= buffer.length;
        }

    }
    public static void print(Complex[] buffer) {
        System.out.println("\nResults:");
        for (Complex c : buffer) {
            System.out.println(c);
        }
    }



}
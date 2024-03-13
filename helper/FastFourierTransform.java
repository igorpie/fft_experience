package helper;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class FastFourierTransform {

    public static int bitReverse(int n, int bits) {
        int reversedN = n;
        int count = bits - 1;

        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }

        return ((reversedN << count) & ((1 << bits) - 1));
    }

    public static void fft(Complex[] buffer) {

        int bits = (int) (log(buffer.length) / log(2));
        for (int j = 1; j < buffer.length / 2; j++) {

            int swapPos = bitReverse(j, bits);
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

    public static void conjugate(Complex[] buffer) {
        for (Complex complex : buffer) complex.im = -complex.im;
    }

    public static void ifft(Complex[] buffer) {
        conjugate(buffer);
        fft(buffer);
    }
    public static void print(Complex[] buffer) {
        System.out.println("\nResults:");
        for (Complex c : buffer) {
            System.out.println(c);
        }
    }


    public static void fftList(List<Complex> buffer) {
        int bits = (int) (log(buffer.size()) / log(2));
        for (int j = 1; j < buffer.size() / 2; j++) {
            int swapPos = bitReverse(j, bits);
            Complex temp = buffer.get(j);
            buffer.set(j, buffer.get(swapPos));
            buffer.set(swapPos, temp);
        }

        for (int N = 2; N <= buffer.size(); N <<= 1) {
            for (int i = 0; i < buffer.size(); i += N) {
                for (int k = 0; k < N / 2; k++) {
                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer.get(evenIndex);
                    Complex odd = buffer.get(oddIndex);
                    double term = (-2 * PI * k) / (double) N;
                    Complex exp = (new Complex((float) cos(term), (float) sin(term)).mult(odd));
                    buffer.set(evenIndex, even.add(exp));
                    buffer.set(oddIndex, even.sub(exp));
                }
            }
        }
    }

    public static void conjugateList(List<Complex> buffer) {
        for (Complex complex : buffer) complex.im = -complex.im;
    }

    public static void ifftList(List<Complex> buffer) {
        conjugateList(buffer);
        fftList(buffer);
    }

    public static void printList(List<Complex> buffer) {
        System.out.println("\nResults List:");
        for (Complex c : buffer) {
            System.out.println(c);
        }
    }

}
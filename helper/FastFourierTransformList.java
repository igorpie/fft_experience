package helper;

import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public class FastFourierTransformList {

    public static void fftList(List<Complex> buffer) {
        int bits = (int) (log(buffer.size()) / log(2));
        for (int j = 1; j < buffer.size() / 2; j++) {
            int swapPos = FftHelper.bitReverse(j, bits);
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


    public static void ifftList(List<Complex> buffer) {
        FftHelper.conjugateList(buffer);
        fftList(buffer);
        for (Complex c : buffer) {
            c.re /= buffer.size();
        }
    }

    public static void printList(List<Complex> buffer) {
        System.out.println("\nResults List:");
        for (Complex c : buffer) {
            System.out.println(c);
        }
    }
}

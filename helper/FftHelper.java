package helper;

import java.util.List;

public class FftHelper {
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

    public static void conjugateArray(Complex[] buffer) {
        for (Complex complex : buffer) complex.im = -complex.im;
    }

    public static void conjugateList(List<Complex> buffer) {
        for (Complex complex : buffer) complex.im = -complex.im;
    }

}

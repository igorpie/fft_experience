import helper.Complex;
import helper.FastFourierTransform;

//import static helper.Complex.conj;
import java.util.ArrayList;
import java.util.List;

import static helper.FastFourierTransform.fft;
import static helper.FastFourierTransform.fftList;

public class Fft {
    public static void main(String[] args) {
        float[] input = {1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};

        List<Complex> inArray = new ArrayList<>();
        Complex[] cinput = new Complex[input.length];
        for (int i = 0; i < input.length; i++) {
            cinput[i] = new Complex(input[i], 0.0f);
            inArray.add(new Complex(input[i], 0.0f));
        }

        FastFourierTransform.fft(cinput);
        FastFourierTransform.print(cinput);

        FastFourierTransform.fftList(inArray);
        FastFourierTransform.printList(inArray);

        FastFourierTransform.ifft(cinput);
        FastFourierTransform.print(cinput);

        FastFourierTransform.ifftList(inArray);
        FastFourierTransform.printList(inArray);
    }
}

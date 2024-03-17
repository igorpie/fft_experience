import helper.*;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import recursivefft.Fft2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
//        FftLeadingZeroes.do_fir();
//        FftPaddingZeroes.do_fir();
        FftPaddingZeroesOptimize1.do_fir();
    }
}

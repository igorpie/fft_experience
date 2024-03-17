import helper.Complex;
import helper.FftHelper;
import helper.Gfx2;
import recursivefft.Fft2;

import java.io.IOException;
import java.util.List;

public class FftPaddingZeroesOptimize1 {
    public static void do_fir() throws IOException {

// подготовка файлов
        List<Complex> impulse = FftHelper.textColumnToComplexDataLoader("fender2048.txt");
        //List<Complex> impulse = FftHelper.textColumnToComplexDataLoader("fender.txt");
        System.out.println("\nimpulse size = " + impulse.size());
        Fft2.Complex2[] impulseFft = Fft2.calcFft2(FftHelper.complexListToVectorArray(impulse));
        List<Complex> wav = FftHelper.textColumnToComplexDataLoader("wav2048.txt");
        //List<Complex> wav = FftHelper.textColumnToComplexDataLoader("input.txt");

        Fft2.Complex2[] wavFft = Fft2.calcFft2(FftHelper.complexListToVectorArray(wav));

        // в лоб
        Fft2.Complex2[] conv1 = Fft2.Complex2.arrayComplex2Mul(impulseFft, wavFft);
        Fft2.Complex2[] result = Fft2.calcIfft2(conv1);

        /*
        var ex = new Gfx2();
        ex.setDataset(ex.createDatasetOfArray(FftHelper.complexListToVectorArray(wav)
                , Fft2.Complex2.arrayComplex2ToDouble(result)));
        ex.makeGfx();
*/

        //куски
        final int IMPULSE_NUM_PARTS = 4;    // P
        final int IMPULSE_PART_SIZE = 256;  // K
        final int IMPULSE_OVERLAPPED_SIZE = 2 * IMPULSE_PART_SIZE; // L

        // prepare impulse
        double[] impulseArray = FftHelper.complexListToVectorArray(impulse);
        double[] temp = new double[IMPULSE_OVERLAPPED_SIZE];
        FftHelper.clearArrayPart(temp, 0, IMPULSE_OVERLAPPED_SIZE);

        // импульс нарезается на 4 части , в конце каждой части - нули
        Fft2.Complex2[][] impulseFftParts = new Fft2.Complex2[IMPULSE_NUM_PARTS][];

        for (int i = 0; i < IMPULSE_NUM_PARTS; i++) {
            FftHelper.replaceArrayPart(impulseArray, i * IMPULSE_PART_SIZE, temp, 0, IMPULSE_PART_SIZE); //  impulse[256] appedn 0[256]
            impulseFftParts[i] = Fft2.calcFft2(temp);
        }

        final int WAV_NUM_PARTS = 8;  // K
        final int WAV_PART_SIZE = 256;  // K
        final int WAV_OVERLAPPED_SIZE = 2 * WAV_PART_SIZE; // L

        // prepare wav
        double[] wavArray = FftHelper.complexListToVectorArray(wav);
        double[] tempWav = new double[WAV_OVERLAPPED_SIZE];
        FftHelper.clearArrayPart(tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[][] wavFftParts = new Fft2.Complex2[WAV_NUM_PARTS][WAV_OVERLAPPED_SIZE];

        for (int i = 0; i < WAV_NUM_PARTS; i++) {
            if (i == 0) {
                FftHelper.replaceArrayPart(wavArray, 0, tempWav, WAV_PART_SIZE, WAV_PART_SIZE); // 0[256] + wav[256]
            } else {
                FftHelper.replaceArrayPart(wavArray, (i - 1) * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE); // wav[256] + next wav[256]
            }
            wavFftParts[i] = Fft2.calcFft2(tempWav);
        }

        // process
        double[][] processOut = new double[WAV_NUM_PARTS][IMPULSE_PART_SIZE];
        int c = 0; //column

        // apply impulse
        Fft2.Complex2[] c0r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[0]);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c0r0));

        Fft2.Complex2[] c1r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[0]);
        Fft2.Complex2[] c1r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[1]);
        Fft2.Complex2.arrayComplex2AddTo1(c1r0, c1r1);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c1r0));

        Fft2.Complex2[] c2r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[2], wavFftParts[0]);
        Fft2.Complex2[] c2r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[1]);
        Fft2.Complex2[] c2r2 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[2]);
        Fft2.Complex2.arrayComplex2AddTo1(c2r0, c2r1);
        Fft2.Complex2.arrayComplex2AddTo1(c2r0, c2r2);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c2r0));

        Fft2.Complex2[] c3r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[3], wavFftParts[0]);
        Fft2.Complex2[] c3r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[2], wavFftParts[1]);
        Fft2.Complex2[] c3r2 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[2]);
        Fft2.Complex2[] c3r3 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[3]);
        Fft2.Complex2.arrayComplex2AddTo1(c3r0, c3r1);
        Fft2.Complex2.arrayComplex2AddTo1(c3r0, c3r2);
        Fft2.Complex2.arrayComplex2AddTo1(c3r0, c3r3);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c3r0));


        Fft2.Complex2[] c4r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[4]);
        Fft2.Complex2[] c4r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[3], wavFftParts[1]);
        Fft2.Complex2[] c4r2 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[2], wavFftParts[2]);
        Fft2.Complex2[] c4r3 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[3]);
        Fft2.Complex2.arrayComplex2AddTo1(c4r0, c4r1);
        Fft2.Complex2.arrayComplex2AddTo1(c4r0, c4r2);
        Fft2.Complex2.arrayComplex2AddTo1(c4r0, c4r3);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c4r0));

        Fft2.Complex2[] c5r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[4]);
        Fft2.Complex2[] c5r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[5]);
        Fft2.Complex2[] c5r2 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[3], wavFftParts[2]);
        Fft2.Complex2[] c5r3 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[2], wavFftParts[3]);
        Fft2.Complex2.arrayComplex2AddTo1(c5r0, c5r1);
        Fft2.Complex2.arrayComplex2AddTo1(c5r0, c5r2);
        Fft2.Complex2.arrayComplex2AddTo1(c5r0, c5r3);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c5r0));

        Fft2.Complex2[] c6r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[2], wavFftParts[4]);
        Fft2.Complex2[] c6r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[5]);
        Fft2.Complex2[] c6r2 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[6]);
        Fft2.Complex2[] c6r3 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[3], wavFftParts[3]);
        Fft2.Complex2.arrayComplex2AddTo1(c6r0, c6r1);
        Fft2.Complex2.arrayComplex2AddTo1(c6r0, c6r2);
        Fft2.Complex2.arrayComplex2AddTo1(c6r0, c6r3);
        processOut[c++] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c6r0));

        Fft2.Complex2[] c7r0 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[3], wavFftParts[4]);
        Fft2.Complex2[] c7r1 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[2], wavFftParts[5]);
        Fft2.Complex2[] c7r2 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[1], wavFftParts[6]);
        Fft2.Complex2[] c7r3 = Fft2.Complex2.arrayComplex2Mul(impulseFftParts[0], wavFftParts[7]);
        Fft2.Complex2.arrayComplex2AddTo1(c7r0, c7r1);
        Fft2.Complex2.arrayComplex2AddTo1(c7r0, c7r2);
        Fft2.Complex2.arrayComplex2AddTo1(c7r0, c7r3);
        processOut[c] = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(c7r0));

        double[] result1 = new double[WAV_NUM_PARTS * WAV_PART_SIZE];

        // как в доке, берется вторая половина кажого сегмента и копируется в итоговый вектор
        for (int i = 0; i < WAV_NUM_PARTS; i++) {
            FftHelper.replaceArrayPart(processOut[i], IMPULSE_PART_SIZE, result1, i * IMPULSE_PART_SIZE, WAV_PART_SIZE);
        }

        var ex = new Gfx2();
        ex.setDataset(ex.createDatasetOfArray(result1,
                Fft2.Complex2.arrayComplex2ToDouble(result),
                FftHelper.complexListToVectorArray(wav)));

//        System.out.println("\r\nPadding zeroes");
//        System.out.println(FftHelper.report3(result1,
//                Fft2.Complex2.arrayComplex2ToDouble(result),
//                FftHelper.complexListToVectorArray(wav)));

        //ex.createDatasetOfArray(Fft2.Complex2.arrayComplex2ToDouble(result)));
        //ex.createDatasetOfArray(result1, FftHelper.complexListToVectorArray(wav)));
        ex.makeGfx();
    }
}

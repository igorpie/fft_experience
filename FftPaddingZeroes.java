import helper.Complex;
import helper.FftHelper;
import helper.Gfx2;
import recursivefft.Fft2;

import java.io.IOException;
import java.util.List;

public class FftPaddingZeroes {
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

        // импульс нарезается на 4 части , в конце каждой части - нули
        FftHelper.clearArrayPart(temp, 0, IMPULSE_OVERLAPPED_SIZE);
        FftHelper.replaceArrayPart(impulseArray, 0 * IMPULSE_PART_SIZE, temp, 0, IMPULSE_PART_SIZE); //  impulse[256] appedn 0[256]
        Fft2.Complex2[] s1 = Fft2.calcFft2(temp);

        FftHelper.replaceArrayPart(impulseArray, 1 * IMPULSE_PART_SIZE, temp, 0, IMPULSE_PART_SIZE);
        Fft2.Complex2[] s2 = Fft2.calcFft2(temp);

        FftHelper.replaceArrayPart(impulseArray, 2 * IMPULSE_PART_SIZE, temp, 0, IMPULSE_PART_SIZE);
        Fft2.Complex2[] s3 = Fft2.calcFft2(temp);

        FftHelper.replaceArrayPart(impulseArray, 3 * IMPULSE_PART_SIZE, temp, 0, IMPULSE_PART_SIZE);
        Fft2.Complex2[] s4 = Fft2.calcFft2(temp);

        final int WAV_NUM_PARTS = 8;  // K
        final int WAV_PART_SIZE = 256;  // K
        final int WAV_OVERLAPPED_SIZE = 2 * WAV_PART_SIZE; // L

        // prepare impulse
        double[] wavArray = FftHelper.complexListToVectorArray(wav);
        double[] tempWav = new double[WAV_OVERLAPPED_SIZE];

        FftHelper.clearArrayPart(tempWav, 0, WAV_OVERLAPPED_SIZE);
        FftHelper.replaceArrayPart(wavArray, 0 * WAV_PART_SIZE, tempWav, WAV_PART_SIZE, WAV_PART_SIZE); // 0[256] + wav[256]
        Fft2.Complex2[] wav1fft = Fft2.calcFft2(tempWav);

        FftHelper.replaceArrayPart(wavArray, 0 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE); // wav[256] + next wav[256]
        Fft2.Complex2[] wav2fft = Fft2.calcFft2(tempWav);

        FftHelper.replaceArrayPart(wavArray, 1 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[] wav3fft = Fft2.calcFft2(tempWav);

        FftHelper.replaceArrayPart(wavArray, 2 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[] wav4fft = Fft2.calcFft2(tempWav);


        FftHelper.replaceArrayPart(wavArray, 3 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[] wav5fft = Fft2.calcFft2(tempWav);
        FftHelper.replaceArrayPart(wavArray, 4 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[] wav6fft = Fft2.calcFft2(tempWav);
        FftHelper.replaceArrayPart(wavArray, 5 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[] wav7fft = Fft2.calcFft2(tempWav);
        FftHelper.replaceArrayPart(wavArray, 6 * WAV_PART_SIZE, tempWav, 0, WAV_OVERLAPPED_SIZE);
        Fft2.Complex2[] wav8fft = Fft2.calcFft2(tempWav);

        // process
        double[] empty = new double[IMPULSE_OVERLAPPED_SIZE];
        FftHelper.clearArrayPart(empty, 0, IMPULSE_OVERLAPPED_SIZE);
        Fft2.Complex2[] emptyFft = Fft2.calcFft2(empty);


        // apply impulse
        Fft2.Complex2[] inBlock1S1 = Fft2.Complex2.arrayComplex2Mul(s1, wav1fft);
        Fft2.Complex2[] sum1 = Fft2.Complex2.arrayComplex2Add(inBlock1S1, emptyFft);
        double[] rsum1 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sum1));

        Fft2.Complex2[] inBlock1S2 = Fft2.Complex2.arrayComplex2Mul(s2, wav1fft);
        Fft2.Complex2[] inBlock2S1 = Fft2.Complex2.arrayComplex2Mul(s1, wav2fft);
        Fft2.Complex2[] sum2 = Fft2.Complex2.arrayComplex2Add(inBlock1S2, inBlock2S1);
        double[] rsum2 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sum2));

        Fft2.Complex2[] inBlock1S3 = Fft2.Complex2.arrayComplex2Mul(s3, wav1fft);
        Fft2.Complex2[] inBlock2S2 = Fft2.Complex2.arrayComplex2Mul(s2, wav2fft);
        Fft2.Complex2[] inBlock3S1 = Fft2.Complex2.arrayComplex2Mul(s1, wav3fft);
        Fft2.Complex2[] sum3 = Fft2.Complex2.arrayComplex2Add(inBlock1S3, inBlock2S2);
        Fft2.Complex2[] sum3a = Fft2.Complex2.arrayComplex2Add(sum3, inBlock3S1);
        double[] rsum3 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sum3a));

        Fft2.Complex2[] inBlock1S4 = Fft2.Complex2.arrayComplex2Mul(s4, wav1fft);
        Fft2.Complex2[] inBlock2S3 = Fft2.Complex2.arrayComplex2Mul(s3, wav2fft);
        Fft2.Complex2[] inBlock3S2 = Fft2.Complex2.arrayComplex2Mul(s2, wav3fft);
        Fft2.Complex2[] inBlock4S1 = Fft2.Complex2.arrayComplex2Mul(s1, wav4fft);
        Fft2.Complex2[] sum4 = Fft2.Complex2.arrayComplex2Add(inBlock1S4, inBlock2S3);
        Fft2.Complex2[] sum4a = Fft2.Complex2.arrayComplex2Add(inBlock3S2, inBlock4S1);
        Fft2.Complex2[] sum4f = Fft2.Complex2.arrayComplex2Add(sum4, sum4a);
        double[] rsum4 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sum4f));


        Fft2.Complex2[] c5r1 = Fft2.Complex2.arrayComplex2Mul(s1, wav5fft);
        Fft2.Complex2[] c5r2 = Fft2.Complex2.arrayComplex2Mul(s4, wav2fft);
        Fft2.Complex2[] c5r3 = Fft2.Complex2.arrayComplex2Mul(s3, wav3fft);
        Fft2.Complex2[] c5r4 = Fft2.Complex2.arrayComplex2Mul(s2, wav4fft);
        Fft2.Complex2[] sumC5a = Fft2.Complex2.arrayComplex2Add(c5r1, c5r2);
        Fft2.Complex2[] sumC5b = Fft2.Complex2.arrayComplex2Add(c5r3, c5r4);
        Fft2.Complex2[] sumC5 = Fft2.Complex2.arrayComplex2Add(sumC5a, sumC5b);
        double[] rsum5 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sumC5));

        Fft2.Complex2[] c6r1 = Fft2.Complex2.arrayComplex2Mul(s2, wav5fft);
        Fft2.Complex2[] c6r2 = Fft2.Complex2.arrayComplex2Mul(s1, wav6fft);
        Fft2.Complex2[] c6r3 = Fft2.Complex2.arrayComplex2Mul(s4, wav3fft);
        Fft2.Complex2[] c6r4 = Fft2.Complex2.arrayComplex2Mul(s3, wav4fft);
        Fft2.Complex2[] sumC6a = Fft2.Complex2.arrayComplex2Add(c6r1, c6r2);
        Fft2.Complex2[] sumC6b = Fft2.Complex2.arrayComplex2Add(c6r3, c6r4);
        Fft2.Complex2[] sumC6 = Fft2.Complex2.arrayComplex2Add(sumC6a, sumC6b);
        double[] rsum6 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sumC6));

        Fft2.Complex2[] c7r1 = Fft2.Complex2.arrayComplex2Mul(s3, wav5fft);
        Fft2.Complex2[] c7r2 = Fft2.Complex2.arrayComplex2Mul(s2, wav6fft);
        Fft2.Complex2[] c7r3 = Fft2.Complex2.arrayComplex2Mul(s1, wav7fft);
        Fft2.Complex2[] c7r4 = Fft2.Complex2.arrayComplex2Mul(s4, wav4fft);
        Fft2.Complex2[] sumC7a = Fft2.Complex2.arrayComplex2Add(c7r1, c7r2);
        Fft2.Complex2[] sumC7b = Fft2.Complex2.arrayComplex2Add(c7r3, c7r4);
        Fft2.Complex2[] sumC7 = Fft2.Complex2.arrayComplex2Add(sumC7a, sumC7b);
        double[] rsum7 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sumC7));

        Fft2.Complex2[] c8r1 = Fft2.Complex2.arrayComplex2Mul(s4, wav5fft);
        Fft2.Complex2[] c8r2 = Fft2.Complex2.arrayComplex2Mul(s3, wav6fft);
        Fft2.Complex2[] c8r3 = Fft2.Complex2.arrayComplex2Mul(s2, wav7fft);
        Fft2.Complex2[] c8r4 = Fft2.Complex2.arrayComplex2Mul(s1, wav8fft);
        Fft2.Complex2[] sumC8a = Fft2.Complex2.arrayComplex2Add(c8r1, c8r2);
        Fft2.Complex2[] sumC8b = Fft2.Complex2.arrayComplex2Add(c8r3, c8r4);
        Fft2.Complex2[] sumC8 = Fft2.Complex2.arrayComplex2Add(sumC8a, sumC8b);
        double[] rsum8 = Fft2.Complex2.arrayComplex2ToDouble(Fft2.calcIfft2(sumC8));

        double[] result1 = new double[WAV_NUM_PARTS * WAV_PART_SIZE];

        // как в доке, берется вторая половина кажого сегмента только картинка плохая

        FftHelper.replaceArrayPart(rsum1, WAV_PART_SIZE, result1, 0 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum2, WAV_PART_SIZE, result1, 1 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum3, WAV_PART_SIZE, result1, 2 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum4, WAV_PART_SIZE, result1, 3 * WAV_PART_SIZE, WAV_PART_SIZE);

        FftHelper.replaceArrayPart(rsum5, WAV_PART_SIZE, result1, 4 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum6, WAV_PART_SIZE, result1, 5 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum7, WAV_PART_SIZE, result1, 6 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum8, WAV_PART_SIZE, result1, 7 * WAV_PART_SIZE, WAV_PART_SIZE);

        /*
        // берется первая половина каждого сегмента и картинка  - лучше

        FftHelper.replaceArrayPart(rsum2, 0, result1, 1 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum1, 0, result1, 0 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum3, 0, result1, 2 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum4, 0, result1, 3 * WAV_PART_SIZE, WAV_PART_SIZE);

        FftHelper.replaceArrayPart(rsum5, 0, result1, 4 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum6, 0, result1, 5 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum7, 0, result1, 6 * WAV_PART_SIZE, WAV_PART_SIZE);
        FftHelper.replaceArrayPart(rsum8, 0, result1, 7 * WAV_PART_SIZE, WAV_PART_SIZE);
*/

        var ex = new Gfx2();
        /*
        ex.setDataset( ex.createDatasetOfArray(result1,
                        Fft2.Complex2.arrayComplex2ToDouble(result)));

*/
//        ex.setDataset(ex.createDatasetOfArray(result1,
//                Fft2.Complex2.arrayComplex2ToDouble(result),
//                FftHelper.complexListToVectorArray(wav)));

        System.out.println("\r\nPadding zeroes");
        System.out.println(FftHelper.report3(result1,
                Fft2.Complex2.arrayComplex2ToDouble(result),
                FftHelper.complexListToVectorArray(wav)));

        //ex.createDatasetOfArray(Fft2.Complex2.arrayComplex2ToDouble(result)));
        //ex.createDatasetOfArray(result1, FftHelper.complexListToVectorArray(wav)));
//        ex.makeGfx();

        //List<Complex> impulseFft = new ArrayList<>(impulse);
        //FastFourierTransformList.fftList(impulseFft);
    }
}

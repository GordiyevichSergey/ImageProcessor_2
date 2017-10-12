package sample;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

/**
 * Created by gardi on 13.09.2017.
 */
public class ImageConverter {
    public Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int[] dataBuf;

        out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
        data = new byte[in.getHeight() * in.getWidth() * (int)out.elemSize()];
        dataBuf = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
        for(int i = 0; i < dataBuf.length; i++) {
            data[i * 3] = (byte) ((dataBuf[i] >> 16) & 0xff);
            data[i * 3 + 1] = (byte) ((dataBuf[i] >> 8) & 0xff);
            data[i * 3 + 2] = (byte) (dataBuf[i] & 0xff);
        }

        out.put(0, 0, data);

        return out;
    }

    public BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        int type;

        byte[] data = new byte[in.rows() * in.cols() * (int)in.elemSize()];
        in.get(0, 0 ,data);

        type = (in.channels() == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        out = new BufferedImage(in.cols(), in.rows(), type);
        out.getRaster().setDataElements(0, 0 , in.cols(), in.rows(), data);

        return out;
    }

    public BufferedImage rgb2Grayscale(BufferedImage in) {
        BufferedImage out;
        Mat mIn = img2Mat(in);

        for(int i = 0; i < mIn.rows(); i++) {
            for(int j = 0; j < mIn.cols(); j++) {
                double[] pixel = mIn.get(i, j);
                double[] nPixel = new double[3];
                for(int k = 0; k < 3; k++) {
                    nPixel[k] = pixel[0] * 0.3 + pixel[1] * 0.59 + pixel[2] * 0.11;
                }
                mIn.put(i, j, nPixel);
            }
        }

        out = mat2Img(mIn);

        return out;
    }

    public int[][] grayscale2Binary(BufferedImage in) {
        int[][] out = new int[in.getHeight()][in.getWidth()];
        Mat mIn = img2Mat(in);

        int threshold = ImageProcessor.getOtsuThreshold(mIn);

        for(int i = 0; i < mIn.rows(); i++) {
            for(int j = 0; j < mIn.cols(); j++) {
                double[] pixel = mIn.get(i, j);
                if(pixel[0] <= threshold) {
                    out[i][j] = 0;
                }
                else {
                    out[i][j] = 1;
                }
            }
        }

        return out;
    }

    public BufferedImage bin2img(int[][] in) {
        BufferedImage out;
        Mat mIn = new Mat(in.length, in[0].length, CvType.CV_8UC1);

        for(int i = 0; i < mIn.rows(); i++) {
            for(int j = 0; j < mIn.cols(); j++) {
                double[] pixel = new double[3];
                double p = (in[i][j] == 0) ? 0.0 : 255.0;
                pixel[0] = pixel[1] = pixel[2] = p;
                mIn.put(i, j, p);
            }
        }

        out = mat2Img(mIn);

        return out;
    }
}
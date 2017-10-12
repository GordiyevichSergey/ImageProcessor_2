package sample;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by gardi on 13.09.2017.
 */
public class ImageProcessor {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final int PIXELS = 256;

    private static final int[][] DISK = {
            { 0, 0, 1, 1, 1, 1, 1, 0 ,0 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 0 ,0 }
    };
    private static final int DISK_R = DISK[0].length / 2;

    private static final int MAX_HITS = 69;
    private static final double[][] COLORS = {
            {160, 0, 0}, {0, 160, 0}, {0, 0, 160},
            {160, 160, 0}, {160, 0, 160}, {0, 160, 160}
    };
    public static final int TOTAL_PROPERTIES = 2;
    private int label;

    private ImageConverter imageConverter;

    public ImageProcessor() {
        imageConverter = new ImageConverter();
    }

    public static int getOtsuThreshold(Mat img) {
        int[] hist = new int[PIXELS];

        for(int i = 0; i < img.rows(); i++) {
            for(int j = 0; j < img.cols(); j++) {
                double[] pixel = img.get(i, j);
                hist[(int)pixel[0]]++;
            }
        }

        double sum = 0;
        for(int i = 0; i < PIXELS; i++) {
            sum += i * hist[i];
        }

        int wB = 0, wF = 0, threshold = 0;
        double sumB = 0, maxBetween = 0;
        for(int i = 0; i < PIXELS; i++) {
            wB += hist[i];
            if(wB == 0) continue;

            wF = img.cols() * img.rows() - wB;
            if(wF == 0) break;

            sumB += (double)(i * hist[i]);

            double mB = sumB / wB;
            double mF = (sum - sumB) / wF;
            double between = (double)wB * (double)wF * (mB - mF) * (mB - mF);

            if(between > maxBetween) {
                maxBetween = between;
                threshold = i;
            }
        }

        return threshold;
    }

    private int[][] dilation(int[][] in) {
        int[][] out = new int[in.length][in[0].length];

        for(int i = DISK_R; i < in.length - DISK_R; i++) {
            for(int j = DISK_R; j < in[0].length - DISK_R; j++) {
                int pixel = in[i][j];
                if(pixel == 1) {
                    for(int i1 = -DISK_R; i1 <= DISK_R; i1++) {
                        for(int j1 = -DISK_R; j1 <= DISK_R; j1++) {
                            if(DISK[i1 + DISK_R][j1 + DISK_R] == 1 && out[i + i1][j + j1] == 0) {
                                out[i + i1][j + j1] = DISK[i1 + DISK_R][j1 + DISK_R];
                            }
                        }
                    }
                }
            }
        }

        return out;
    }

    private int[][] errosion(int[][] in) {
        int[][] out = new int[in.length][in[0].length];

        for(int i = DISK_R; i < in.length - DISK_R; i++) {
            for(int j = DISK_R; j < in[0].length - DISK_R; j++) {
                int pixel;
                int hits = 0;
                for(int i1 = -DISK_R; i1 <= DISK_R; i1++) {
                    for(int j1 = -DISK_R; j1 <= DISK_R; j1++) {
                        pixel = in[i + i1][j + j1];
                        if(pixel == DISK[i1 + DISK_R][j1 + DISK_R] && DISK[i1 + DISK_R][j1 + DISK_R] == 1) {
                            hits++;
                        }
                    }
                }

                if(hits == MAX_HITS)
                    out[i][j] = 1;
                else
                    out[i][j] = 0;
            }
        }

        return out;
    }

    private int[][] labelImage(int[][] img) {
        int label = 0;
        Stack stack = new Stack();
        int[][] map = new int[img.length][img[0].length];

        for(int i = 1; i < img.length - 1; i++) {
            for(int j = 1; j < img[0].length - 1; j++) {
                if(img[i][j] == 0) continue;
                if(map[i][j] > 0) continue;

                stack.push(new int[] {i, j});
                label++;
                map[i][j] = label;

                int[] pos;
                while(!stack.isEmpty()) {
                    pos = (int[])stack.pop();
                    int i1 = pos[0], j1 = pos[1];

                    if(i1 == 0 || j1 == 0 || i1 == img.length - 1 || j1 == img[0].length - 1)
                        continue;

                    if(img[i1 - 1][j1 - 1] == 1 && map[i1 - 1][j1 - 1] == 0) {
                        stack.push(new int[] {i1 - 1, j1 - 1});
                        map[i1 - 1][j1 - 1] = label;
                    }
                    if(img[i1 - 1][j1] == 1 && map[i1 - 1][j1] == 0) {
                        stack.push(new int[] {i1 - 1, j1});
                        map[i1 - 1][j1] = label;
                    }
                    if(img[i1 - 1][j1 + 1] == 1 && map[i1 - 1][j1 + 1] == 0) {
                        stack.push(new int[] {i1 - 1, j1 + 1});
                        map[i1 - 1][j1 + 1] = label;
                    }
                    if(img[i1][j1 - 1] == 1 && map[i1][j1 - 1] == 0) {
                        stack.push(new int[] {i1, j1 - 1});
                        map[i1][j1 - 1] = label;
                    }
                    if(img[i1][j1 + 1] == 1 && map[i1][j1 + 1] == 0) {
                        stack.push(new int[] {i1, j1 + 1});
                        map[i1][j1 + 1] = label;
                    }
                    if(img[i1 + 1][j1 - 1] == 1 && map[i1 + 1][j1 - 1] == 0) {
                        stack.push(new int[] {i1 + 1, j1 - 1});
                        map[i1 + 1][j1 - 1] = label;
                    }
                    if(img[i1 + 1][j1] == 1 && map[i1 + 1][j1] == 0) {
                        stack.push(new int[] {i1 + 1, j1});
                        map[i1 + 1][j1] = label;
                    }
                    if(img[i1 + 1][j1 + 1] == 1 && map[i1 + 1][j1 + 1] == 0) {
                        stack.push(new int[] {i1 + 1, j1 + 1});
                        map[i1 + 1][j1 + 1] = label;
                    }
                }
            }
        }

        this.label = label;

        return map;
    }

    private int[][] getObjectsProperties(int[][] labImg) {
        int[][] out = new int[this.label][TOTAL_PROPERTIES];
        int[] area = new int[this.label];
        int[] perimeter = new int[this.label];

        for(int i = 0; i < labImg.length; i++) {
            for(int j = 0; j < labImg[0].length; j++) {
                int pixel = labImg[i][j];
                if(pixel != 0) {
                    area[pixel - 1]++;

                    boolean stop = false;
                    for(int i1 = -1; i1 <= 1; i1++) {
                        if(i == 0 || i == labImg.length - 1) {
                            continue;
                        }
                        for(int j1 = -1; j1 <= 1; j1++) {
                            if(j == 0 || j == labImg[0].length - 1 || (i1 == 0 && j1 ==0)) {
                                continue;
                            }
                            if(labImg[i + i1][j + j1] == 0) {
                                perimeter[pixel - 1]++;
                                stop = true;
                                break;
                            }
                        }
                        if(stop) {
                            break;
                        }
                    }
                }
            }
        }

        for(int i = 0; i < out.length; i++) {
            out[i][0] = area[i];
            out[i][1] = perimeter[i];
        }

        return out;
    }

    public BufferedImage findObjects(BufferedImage normalImage, int[][] binaryImage) {
        BufferedImage out;
        int[][] temp = binaryImage;
        temp = errosion(temp);
        temp = dilation(temp);
        temp = labelImage(temp);

        KMeans kMeans = new KMeans(getObjectsProperties(temp));
        ArrayList<KMeans.Data> list = kMeans.cluster();

        for(int i = 0; i < temp.length; i++) {
            for(int j = 0; j < temp[0].length; j++) {
                int pixel = temp[i][j];
                if(pixel != 0) {
                    temp[i][j] = list.get(pixel - 1).getCluster() + 1;
                }
            }
        }

        out = colorizeObjects(normalImage, temp);

        return out;
    }

    private BufferedImage colorizeObjects(BufferedImage normalImage, int[][] labeledImage) {
        BufferedImage out;
        Mat mIn = imageConverter.img2Mat(normalImage);

        for(int i = 0; i < labeledImage.length; i++) {
            for(int j = 0; j < labeledImage[0].length; j++) {
                int pixel = labeledImage[i][j];
                if(pixel != 0) {
                    mIn.put(i, j, COLORS[pixel - 1]);
                }
            }
        }

        out = imageConverter.mat2Img(mIn);
        return out;
    }
}
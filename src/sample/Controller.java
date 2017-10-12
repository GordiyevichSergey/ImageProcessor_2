package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private Button convertToGrayScaleButton, convertToBinaryButton, selectObjectsButton;
    @FXML
    private Button showNormalImageButton, showBinaryImageButton, showLabeledImageButton;

    private ImageProcessor imageProcessor;
    private ImageConverter imageConverter;
    private BufferedImage normalImage, processedImage, bufferedBinaryImage, grayScaleImage;
    private int[][] binaryImg;

    public void initialize() {
        imageProcessor = new ImageProcessor();
        imageConverter = new ImageConverter();
        setDisable(true);
    }

    private void setDisable(boolean  disable) {
        convertToGrayScaleButton.setDisable(disable);
        convertToBinaryButton.setDisable(disable);
        selectObjectsButton.setDisable(disable);
        showNormalImageButton.setDisable(disable);
        showBinaryImageButton.setDisable(disable);
        showLabeledImageButton.setDisable(disable);
    }

    @FXML
    public void onLoadImageButtonClicked() {
        setDisable(true);

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        try {
            normalImage = ImageIO.read(file);
        } catch (IOException ex) {
            normalImage = null;
        }

        showNormalImageButton.setDisable(false);
        convertToGrayScaleButton.setDisable(false);
    }

    @FXML
    public void onShowNormalImageButtonClicked(){
        Image image = SwingFXUtils.toFXImage(normalImage, null);
        ImageStage stage = new ImageStage(image);
        stage.show();
    }

    @FXML
    public void onConvertToGrayScaleButtonClicked() {
        grayScaleImage = imageConverter.rgb2Grayscale(normalImage);

        convertToBinaryButton.setDisable(false);
    }

    @FXML
    public void onConvertToBinaryButtonClicked() {
        binaryImg = imageConverter.grayscale2Binary(grayScaleImage);
        bufferedBinaryImage = imageConverter.bin2img(binaryImg);

        showBinaryImageButton.setDisable(false);
        selectObjectsButton.setDisable(false);
    }

    @FXML
    public void onShowBinaryImageButtonClicked() {
        Image image = SwingFXUtils.toFXImage(bufferedBinaryImage, null);
        ImageStage stage = new ImageStage(image);
        stage.show();
    }

    @FXML
    public void onShowLabeledImageButtonClicked() {
        Image image = SwingFXUtils.toFXImage(processedImage, null);
        ImageStage stage = new ImageStage(image);
        stage.show();
    }

    @FXML
    public void onSelectObjectsButtonClicked() {
        processedImage = imageProcessor.findObjects(normalImage, binaryImg);
        showLabeledImageButton.setDisable(false);
    }
}
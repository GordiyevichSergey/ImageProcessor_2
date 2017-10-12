package sample;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by gardi on 09.09.2017.
 */
public class ImageStage extends Stage{

    private Image image;
    private static final int MAX_WIDTH = 1067;
    private static final int MAX_HEIGHT = 600;

    public ImageStage(Image image) {
        super();
        this.image = image;
        addScene();
    }

    private void addScene() {
        VBox rootBox = new VBox();
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        double width = (image.getWidth() > MAX_WIDTH) ? MAX_WIDTH : image.getWidth();
        double height = (image.getHeight() > MAX_HEIGHT) ? MAX_HEIGHT : image.getHeight();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        rootBox.getChildren().add(imageView);

        Scene scene = new Scene(rootBox);
        this.setScene(scene);
    }
}

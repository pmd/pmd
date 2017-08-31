/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sourceforge.pmd.PMD;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Clément Fournier
 */
public class Designer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("designer.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("PMD Rule Designer (v " + PMD.VERSION + ')');
        setIcons(stage);

        stage.setScene(scene);
        stage.show();
    }


    private void setIcons(Stage primaryStage) {
        ObservableList<Image> icons = primaryStage.getIcons();
        final String dirPrefix = "icons/app/";
        List<String> imageNames = Arrays.asList("pmd-logo.png",
                                                "pmd-logo_small.png",
                                                "pmd-logo_tiny.png",
                                                "pmd-logo_big.png");

        List<Image> images = imageNames.stream()
                                       .map(s -> dirPrefix + s)
                                       .map(s -> getClass().getResourceAsStream(s))
                                       .filter(Objects::nonNull)
                                       .map(Image::new)
                                       .collect(Collectors.toList());


        icons.addAll(images);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

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
 * Main class for the designer.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class Designer extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // System.err.close();

        FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("fxml/designer.fxml"));

        DesignerApp owner = new DesignerApp(stage);
        NodeInfoPanelController nodeInfoPanelController = new NodeInfoPanelController(owner);
        XPathPanelController xpathPanelController = new XPathPanelController(owner);
        SourceEditorController sourceEditorController = new SourceEditorController(owner);
        EventLogController eventLogController = new EventLogController(owner);

        MainDesignerController mainController = new MainDesignerController(owner);

        loader.setControllerFactory(type -> {
            if (type == MainDesignerController.class) {
                return mainController;
            } else if (type == NodeInfoPanelController.class) {
                return nodeInfoPanelController;
            } else if (type == XPathPanelController.class) {
                return xpathPanelController;
            } else if (type == SourceEditorController.class) {
                return sourceEditorController;
            } else if (type == EventLogController.class) {
                return eventLogController;
            } else {
                // default behavior for controllerFactory:
                try {
                    return type.newInstance();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(exc); // fatal, just bail...
                }
            }
        });

        stage.setOnCloseRequest(e -> mainController.shutdown());

      //  loader.setLocation(getClass().getResource("fxml/designer.xml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("PMD Rule Designer (v " + PMD.VERSION + ')');
        setIcons(stage);

        stage.setScene(scene);
        stage.show();
    }


    private void setIcons(Stage primaryStage) {
        ObservableList<Image> icons = primaryStage.getIcons();
        final String dirPrefix = "icons/app/";
        List<String> imageNames = Arrays.asList("designer_logo.jpeg");

        // TODO make more icon sizes

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

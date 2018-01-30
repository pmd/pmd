/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sourceforge.pmd.PMDVersion;

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

    private void parseParameters(Parameters params) {
        List<String> raw = params.getRaw();
        if (!raw.contains("-v")
            && !raw.contains("--verbose")) {
            // error output is disabled by default
            
            System.err.close();
        }

    }


    @Override
    public void start(Stage stage) throws IOException {
        
        parseParameters(getParameters());

        FXMLLoader loader
            = new FXMLLoader(getClass().getResource("fxml/designer.fxml"));

        DesignerRoot owner = new DesignerRoot(stage);
        MainDesignerController mainController = new MainDesignerController(owner);

        NodeInfoPanelController nodeInfoPanelController = new NodeInfoPanelController(owner, mainController);
        XPathPanelController xpathPanelController = new XPathPanelController(owner, mainController);
        SourceEditorController sourceEditorController = new SourceEditorController(owner, mainController);
        EventLogController eventLogController = new EventLogController(owner, mainController);

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

        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("PMD Rule Designer (v " + PMDVersion.VERSION + ')');
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

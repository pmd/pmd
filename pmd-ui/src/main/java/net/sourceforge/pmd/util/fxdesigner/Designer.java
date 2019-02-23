/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.util.fxdesigner.app.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;

import com.sun.javafx.fxml.builder.ProxyBuilder;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * Main class for the designer, launched only if {@link DesignerStarter} detected JavaFX support.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class Designer extends Application {

    private boolean isDeveloperMode;
    private long initStartTimeMillis;

    public Designer() {
        initStartTimeMillis = System.currentTimeMillis();
    }

    private void parseParameters(Parameters params) {
        List<String> raw = params.getRaw();
        // error output is disabled by default
        if (raw.contains("-v") || raw.contains("--verbose")) {
            isDeveloperMode = true;
        }
    }


    @Override
    public void start(Stage stage) throws IOException {
        parseParameters(getParameters());

        stage.setTitle("PMD Rule Designer (v " + PMDVersion.VERSION + ')');
        setIcons(stage);

        System.out.print(stage.getTitle() + " initializing... ");

        FXMLLoader loader = new FXMLLoader(DesignerUtil.getFxml("designer.fxml"));

        DesignerRoot owner = new DesignerRoot(stage, isDeveloperMode);
        MainDesignerController mainController = new MainDesignerController(owner);

        NodeInfoPanelController nodeInfoPanelController = new NodeInfoPanelController(mainController);
        XPathPanelController xpathPanelController = new XPathPanelController(mainController);
        SourceEditorController sourceEditorController = new SourceEditorController(mainController);

        loader.setBuilderFactory(type -> {

            boolean needsRoot = Arrays.stream(type.getConstructors()).anyMatch(it -> ArrayUtils.contains(it.getParameterTypes(), DesignerRoot.class));

            if (needsRoot) {
                // Controls that need the DesignerRoot can declare a constructor
                // with a parameter w/ signature @NamedArg("designerRoot") DesignerRoot
                // to be injected with the relevant instance of the app.
                // TODO Not everything has been refactored to use this mechanism for now
                ProxyBuilder<Object> builder = new ProxyBuilder<>(type);
                builder.put("designerRoot", owner);
                return builder;
            } else {
                return null; //use default
            }
        });

        loader.setControllerFactory(type -> {
            if (type == MainDesignerController.class) {
                return mainController;
            } else if (type == NodeInfoPanelController.class) {
                return nodeInfoPanelController;
            } else if (type == XPathPanelController.class) {
                return xpathPanelController;
            } else if (type == SourceEditorController.class) {
                return sourceEditorController;
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

        stage.setScene(scene);

        if (owner.isDeveloperMode()) {
            // only close after initialization succeeded.
            // but before stage.show to reduce unwanted noise
            System.err.close();
        }

        stage.show();

        long initTime = System.currentTimeMillis() - initStartTimeMillis;

        System.out.println("done in " + initTime + "ms.");
        if (!owner.isDeveloperMode()) {
            System.out.println("Run with --verbose parameter to enable error output.");
        }
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

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;

import net.sourceforge.pmd.PMD;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Clément Fournier
 */
public class Designer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("designer.fxml"));

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("PMD Rule Designer (v " + PMD.VERSION + ')');
        stage.setScene(scene);
        stage.show();
    }
}

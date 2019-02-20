/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.popups;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;


/**
 * @author Clément Fournier
 */
public final class SimplePopups {

    private static final String LICENSE_FILE_PATH = "/net/sourceforge/pmd/util/fxdesigner/LICENSE";


    private SimplePopups() {

    }


    public static void showLicensePopup() {
        Alert licenseAlert = new Alert(AlertType.INFORMATION);
        licenseAlert.setWidth(500);
        licenseAlert.setHeaderText("License");

        ScrollPane scroll = new ScrollPane();
        try {
            scroll.setContent(new TextArea(IOUtils.toString(SimplePopups.class.getResourceAsStream(LICENSE_FILE_PATH), StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        licenseAlert.getDialogPane().setContent(scroll);
        licenseAlert.showAndWait();
    }

}

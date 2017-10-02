/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.util.fxdesigner.model.EventLogger;

import javafx.stage.Stage;

/**
 * Interface for the singleton of the app.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerApp {


    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
        + System.getProperty("file.separator") + ".pmd_new_designer.xml";

    private final Stage mainStage;
    private final EventLogger logger = new EventLogger();


    public DesignerApp(Stage mainStage) {
        this.mainStage = mainStage;
    }


    /**
     * Gets the logger of the application.
     *
     * @return The logger
     */
    public EventLogger getLogger() {
        return logger;
    }


    /**
     * Gets the main stage of the application.
     *
     * @return The main stage
     */
    public Stage getMainStage() {
        return mainStage;
    }

}

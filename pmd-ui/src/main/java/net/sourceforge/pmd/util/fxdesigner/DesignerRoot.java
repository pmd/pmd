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
public final class DesignerRoot {


    private final Stage mainStage;
    private final EventLogger logger = new EventLogger();
    private final boolean developerMode;


    public DesignerRoot(Stage mainStage, boolean developerMode) {
        this.mainStage = mainStage;
        this.developerMode = developerMode;
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


    public boolean isDeveloperMode() {
        return developerMode;
    }

}

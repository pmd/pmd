/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.util.fxdesigner.util.EventLogger;

import javafx.stage.Stage;

/**
 * Interface for the singleton of the app.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface DesignerApp {


    /**
     * Gets the logger of the application.
     *
     * @return The logger
     */
    EventLogger getLogger();


    /**
     * Gets the main stage of the application.
     *
     * @return The main stage
     */
    Stage getMainStage();

}

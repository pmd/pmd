/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import java.util.function.Supplier;

import net.sourceforge.pmd.util.fxdesigner.SourceEditorController;
import net.sourceforge.pmd.util.fxdesigner.app.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource.NodeSelectionEvent;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.controls.AstTreeView;

import javafx.scene.control.Control;
import javafx.stage.Stage;


/**
 * Some part of the application, e.g. a controller. Components in an instance of the app are all linked
 * to the same {@link DesignerRoot}, which hosts utilities globally available to the app, e.g. the logger.
 *
 * <p>Components that are not controllers, e.g. {@link Control}s, should be injected with the designer
 * root at initialization time, eg what {@link SourceEditorController} does with {@link AstTreeView}.
 *
 * <p>Some more specific cross-cutting structures for the internals of the app are the {@link SettingsOwner}
 * tree, which is more or less identical to the {@link AbstractController} tree. {@link NodeSelectionSource}s
 * form yet another similar tree of related components.
 *
 * @author Clément Fournier
 */
public interface ApplicationComponent {


    DesignerRoot getDesignerRoot();


    /**
     * A debug name for this component, used in developer mode to e.g. trace events
     * handling paths.
     */
    default String getDebugName() {
        return getClass().getSimpleName();
    }


    /**
     * Gets the logger of the application. Events pushed to the logger
     * are filtered then forwarded to the Event Log control.
     *
     * @return The logger
     */
    default EventLogger getLogger() {
        return getDesignerRoot().getLogger();
    }


    /**
     * Gets the main stage of the application.
     */
    default Stage getMainStage() {
        return getDesignerRoot().getMainStage();
    }


    /**
     * If true, some more events are pushed to the event log, and
     * console streams are open. This is enabled by the -v or --verbose
     * option on command line for now.
     */
    default boolean isDeveloperMode() {
        return getDesignerRoot().isDeveloperMode();
    }


    /**
     * Notify the logger of an exception that somewhere in PMD logic. Exceptions raised
     * by the app logic are considered internal and should be forwarded to the logger
     * using {@link #logInternalException(Throwable)}. If we're not in developer mode
     * they will be ignored.
     */
    default void logUserException(Throwable throwable, Category category) {
        getLogger().logEvent(LogEntry.createUserExceptionEntry(throwable, category));
    }


    /**
     * Notify the logger that XPath parsing succeeded and that the last recent failure may be thrown away.
     * Only logged in developer mode.
     */
    default void raiseParsableXPathFlag() {
        getLogger().logEvent(LogEntry.createUserFlagEntry(Category.XPATH_OK));
    }


    /**
     * Notify the logger that source code parsing succeeded and that the last recent failure may be thrown away.
     * Only logged in developer mode.
     */
    default void raiseParsableSourceFlag() {
        getLogger().logEvent(LogEntry.createUserFlagEntry(Category.PARSE_OK));
    }

    // Internal log handlers


    /** Logs an exception that occurred somewhere in the app logic. */
    default void logInternalException(Throwable throwable) {
        if (isDeveloperMode()) {
            getLogger().logEvent(LogEntry.createInternalExceptionEntry(throwable));
        }
    }


    /** Logs an exception that occurred somewhere in the app logic. */
    default void logInternalDebugInfo(Supplier<String> shortMessage, Supplier<String> details) {
        if (isDeveloperMode()) {
            getLogger().logEvent(LogEntry.createInternalDebugEntry(shortMessage.get(), details.get()));
        }
    }


    /** Logs a tracing event pushed by a {@link NodeSelectionSource}. */
    default void logSelectionEventTrace(NodeSelectionEvent event, Supplier<String> details) {
        if (isDeveloperMode()) {
            getLogger().logEvent(LogEntry.createNodeSelectionEventTraceEntry(event, details.get()));
        }
    }

}

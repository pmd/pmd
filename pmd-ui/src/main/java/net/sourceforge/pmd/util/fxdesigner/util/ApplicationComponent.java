/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.function.Supplier;

import net.sourceforge.pmd.util.fxdesigner.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.model.EventLogger;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.util.NodeSelectionSource.NodeSelectionEvent;


/**
 * Some part of the application, linked to the {@link DesignerRoot}.
 *
 * @author Clément Fournier
 */
public interface ApplicationComponent {


    DesignerRoot getDesignerRoot();


    default String getDebugName() {
        return getClass().getSimpleName();
    }


    default EventLogger getLogger() {
        return getDesignerRoot().getLogger();
    }


    default boolean isDeveloperMode() {
        return getDesignerRoot().isDeveloperMode();
    }


    default void logUserException(Throwable throwable, Category category) {
        getLogger().logEvent(LogEntry.createUserExceptionEntry(throwable, category));
    }


    default void raiseParsableXPathFlag() {
        getLogger().logEvent(LogEntry.createUserFlagEntry(Category.XPATH_OK));
    }


    default void raiseParsableSourceFlag() {
        getLogger().logEvent(LogEntry.createUserFlagEntry(Category.PARSE_OK));
    }

    // Internal log handlers


    default void logInternalException(Throwable throwable) {
        if (isDeveloperMode()) {
            getLogger().logEvent(LogEntry.createInternalExceptionEntry(throwable));
        }
    }


    default void logInternalDebugInfo(Supplier<String> shortMessage, Supplier<String> details) {
        if (isDeveloperMode()) {
            getLogger().logEvent(LogEntry.createInternalDebugEntry(shortMessage.get(), details.get()));
        }
    }


    default void logSelectionEventTrace(NodeSelectionEvent event, Supplier<String> details) {
        if (isDeveloperMode()) {
            getLogger().logEvent(LogEntry.createNodeSelectionEventTraceEntry(event, details.get()));
        }
    }

}

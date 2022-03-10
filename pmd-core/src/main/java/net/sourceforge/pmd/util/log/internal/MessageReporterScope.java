/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * A logger that prefixes a scope name to log messages. Also keeps a
 * separate error count.
 *
 * @author Clément Fournier
 */
@InternalApi
public final class MessageReporterScope extends MessageReporterBase {

    private final MessageReporter backend;
    private final String scopePrefix;

    public MessageReporterScope(String scopeName, MessageReporter backend) {
        this.backend = backend;
        this.scopePrefix = "[" + scopeName + "] ";
    }

    @Override
    protected void logImpl(Level level, String message, Object[] formatArgs) {
        backend.log(level, scopePrefix + message, formatArgs);
    }
}

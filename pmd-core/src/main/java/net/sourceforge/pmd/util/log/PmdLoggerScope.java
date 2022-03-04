/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import org.slf4j.event.Level;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * A logger that prefixes a scope name to log messages. Also keeps a
 * separate error count.
 *
 * @author Clément Fournier
 */
@InternalApi
public final class PmdLoggerScope extends PmdLoggerBase {

    private final PmdLogger backend;
    private final String scopePrefix;

    public PmdLoggerScope(String scopeName, PmdLogger backend) {
        this.backend = backend;
        this.scopePrefix = "[" + scopeName + "] ";
    }

    @Override
    protected void logImpl(Level level, String message, Object[] formatArgs) {
        backend.log(level, scopePrefix + message, formatArgs);
    }
}

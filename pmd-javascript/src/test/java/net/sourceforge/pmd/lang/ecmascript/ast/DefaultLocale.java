/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.Locale;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit rule to change the system locale during a test.
 */
public class DefaultLocale implements TestRule {

    private boolean statementIsExecuting = false;
    private Locale loc = Locale.getDefault();

    /** Set the locale value (overwrites previously set value). */
    public void set(Locale locale) {
        if (statementIsExecuting) {
            Locale.setDefault(locale);
        } else {
            this.loc = locale;
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new EnvironmentVariablesStatement(base);
    }

    private class EnvironmentVariablesStatement extends Statement {

        final Statement baseStatement;

        EnvironmentVariablesStatement(Statement baseStatement) {
            this.baseStatement = baseStatement;
        }

        @Override
        public void evaluate() throws Throwable {
            Locale prev = Locale.getDefault();
            statementIsExecuting = true;
            try {
                Locale.setDefault(loc);
                baseStatement.evaluate();
            } finally {
                statementIsExecuting = false;
                Locale.setDefault(prev);
            }
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * An exception that is thrown when something wrong occurs while
 * {@linkplain RuleSetLoader loading rulesets}. This may be because the
 * XML is not well-formed, does not respect the ruleset schema, is
 * not a valid ruleset or is otherwise unparsable.
 */
public final class RuleSetLoadException extends RuntimeException {

    /** Constructors are internal. */
    @InternalApi
    public RuleSetLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructors are internal. */
    @InternalApi
    public RuleSetLoadException(String message) {
        super(message);
    }

}

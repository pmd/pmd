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
 *
 * <p>In the new {@link RuleSetLoader} API, this is thrown instead of
 * {@link RuleSetNotFoundException}.
 */
public final class RulesetLoadException extends RuntimeException {

    /** Constructors are internal. */
    @InternalApi
    public RulesetLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructors are internal. */
    @InternalApi
    public RulesetLoadException(String message) {
        super(message);
    }

}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import org.checkerframework.checker.nullness.qual.NonNull;

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
    public RuleSetLoadException(RuleSetReferenceId rsetId, @NonNull Throwable cause) {
        super("Cannot load ruleset " + rsetId + ": " + cause.getMessage(), cause);
    }

    /** Constructors are internal. */
    @InternalApi
    public RuleSetLoadException(RuleSetReferenceId rsetId, String message) {
        super("Cannot load ruleset " + rsetId + ": " + message);
    }

}

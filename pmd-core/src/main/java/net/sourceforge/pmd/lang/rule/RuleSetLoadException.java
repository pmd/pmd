/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.rule.internal.RuleSetReferenceId;

/**
 * An exception that is thrown when something wrong occurs while
 * {@linkplain RuleSetLoader loading rulesets}. This may be because the
 * XML is not well-formed, does not respect the ruleset schema, is
 * not a valid ruleset or is otherwise unparsable.
 */
public class RuleSetLoadException extends RuntimeException {

    /**
     * @apiNote Internal API.
     */
    RuleSetLoadException(RuleSetReferenceId rsetId, @NonNull Throwable cause) {
        super("Cannot load ruleset " + rsetId + ": " + cause.getMessage(), cause);
    }

    /**
     * @apiNote Internal API.
     */
    RuleSetLoadException(RuleSetReferenceId rsetId, String message) {
        super("Cannot load ruleset " + rsetId + ": " + message);
    }

}

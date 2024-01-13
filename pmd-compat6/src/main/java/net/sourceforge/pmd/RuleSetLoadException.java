/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RuleSetLoadException extends net.sourceforge.pmd.lang.rule.RuleSetLoadException {
    public RuleSetLoadException(RuleSetReferenceId rsetId, @NonNull Throwable cause) {
        super(rsetId, cause);
    }

    public RuleSetLoadException(RuleSetReferenceId rsetId, String message) {
        super(rsetId, message);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.function.Function;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.reporting.Reportable;
import net.sourceforge.pmd.reporting.ViolationSuppressor.SuppressionCommentWrapper;

/**
 * Simple implementation of {@link SuppressionCommentWrapper}.
 *
 * @param <T> Type of Reportable (node, token, lambda)
 *
 * @experimental Since 7.14.0. See <a href="https://github.com/pmd/pmd/pull/5609">[core] Add rule to report unnecessary suppression comments/annotations #5609</a>
 */
@Experimental
public class SuppressionCommentImpl<T extends Reportable> implements SuppressionCommentWrapper {
    private final T token;
    private final Function<? super T, String> messageGetter;

    /** Create an instance where the message is computed on-demand. */
    public SuppressionCommentImpl(T token, Function<? super T, String> messageGetter) {
        this.token = token;
        this.messageGetter = messageGetter;
    }

    /** Create an instance with a precomputed message. */
    public SuppressionCommentImpl(T token, String message) {
        this(token, t -> message);
    }

    @Override
    public String getUserMessage() {
        return messageGetter.apply(token);
    }

    @Override
    public Reportable getLocation() {
        return token;
    }
}

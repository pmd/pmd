package net.sourceforge.pmd.lang.ast.impl;

import java.util.function.Function;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.reporting.ViolationSuppressor.SuppressionCommentWrapper;
import net.sourceforge.pmd.reporting.Reportable;

@Experimental
public class SuppressionCommentImpl<T extends Reportable> implements SuppressionCommentWrapper {
    private final T token;
    private final Function<? super T, String> messageGetter;

    public SuppressionCommentImpl(T token, Function<? super T, String> messageGetter) {
        this.token = token;
        this.messageGetter = messageGetter;
    }

    public SuppressionCommentImpl(T token, String message) {
        this.token = token;
        this.messageGetter = t -> message;
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

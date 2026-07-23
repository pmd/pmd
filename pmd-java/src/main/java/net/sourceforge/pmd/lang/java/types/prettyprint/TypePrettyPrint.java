/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.prettyprint;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeVisitable;

/**
 * Pretty-printing methods to display types. The current API is only
 * offered for debugging, not for displaying types to users.
 */
public final class TypePrettyPrint {

    private TypePrettyPrint() {

    }

    public static @NonNull String prettyPrint(@NonNull JTypeVisitable t) {
        return prettyPrint(t, new TypePrettyPrinter());
    }

    public static @NonNull String prettyPrintWithSimpleNames(@NonNull JTypeVisitable t) {
        return prettyPrint(t, new TypePrettyPrinter().qualifyNames(false));
    }

    public static String prettyPrint(@NonNull JTypeVisitable t, TypePrettyPrinter prettyPrinter) {
        t.acceptVisitor(DefaultVisitor.INSTANCE, prettyPrinter);
        return prettyPrinter.consumeResult();
    }

    static final class DefaultVisitor extends PrettyPrintVisitor<TypePrettyPrinter> {
        static final DefaultVisitor INSTANCE = new DefaultVisitor();
    }
}

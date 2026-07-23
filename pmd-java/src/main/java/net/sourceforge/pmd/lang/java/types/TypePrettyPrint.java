/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Pretty-printing methods to display types. The current API is only
 * offered for debugging, not for displaying types to users.
 *
 * @deprecated Use {@link net.sourceforge.pmd.lang.java.types.prettyprint.TypePrettyPrint}
 */
@Deprecated
public final class TypePrettyPrint {

    private TypePrettyPrint() {

    }

    public static @NonNull String prettyPrint(@NonNull JTypeVisitable t) {
        return net.sourceforge.pmd.lang.java.types.prettyprint.TypePrettyPrint.prettyPrint(t);
    }

    public static @NonNull String prettyPrintWithSimpleNames(@NonNull JTypeVisitable t) {
        return net.sourceforge.pmd.lang.java.types.prettyprint.TypePrettyPrint.prettyPrintWithSimpleNames(t);
    }

    public static String prettyPrint(@NonNull JTypeVisitable t, TypePrettyPrinter prettyPrinter) {
        return net.sourceforge.pmd.lang.java.types.prettyprint.TypePrettyPrint.prettyPrint(t, prettyPrinter);
    }

    /**
     * @deprecated Use {@link net.sourceforge.pmd.lang.java.types.prettyprint.TypePrettyPrinter}
     */
    @Deprecated
    public static class TypePrettyPrinter extends net.sourceforge.pmd.lang.java.types.prettyprint.TypePrettyPrinter {

        /**
         * {@inheritDoc}
         */
        @Override
        public TypePrettyPrinter printMethodHeader(boolean printMethodHeader) {
            this.printMethodHeader = printMethodHeader;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypePrettyPrinter printMethodResult(boolean printMethodResult) {
            this.printMethodReturnType = printMethodResult;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypePrettyPrinter qualifyTvars(boolean qualifyTvars) {
            this.qualifyTvars = qualifyTvars;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypePrettyPrinter qualifyAnnotations(boolean qualifyAnnotations) {
            this.qualifyAnnotations = qualifyAnnotations;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypePrettyPrinter printAnnotations(boolean printAnnotations) {
            this.printTypeAnnotations = printAnnotations;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypePrettyPrinter qualifyNames(boolean qualifyNames) {
            this.qualifyNames = qualifyNames;
            return this;
        }
    }

}

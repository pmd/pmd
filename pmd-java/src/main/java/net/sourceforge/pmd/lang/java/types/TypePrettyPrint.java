/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.util.OptionalBool;

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
        t.acceptVisitor(PrettyPrintVisitor.INSTANCE, prettyPrinter);
        return prettyPrinter.consumeResult();
    }

    /**
     * Options to pretty print a type. Cannot be used concurrently.
     */
    public static class TypePrettyPrinter {

        private final StringBuilder sb = new StringBuilder();

        private boolean printMethodHeader = true;
        private boolean printMethodReturnType = true;
        private OptionalBool printTypeVarBounds = UNKNOWN;
        private boolean qualifyTvars = false;
        private boolean qualifyNames = true;
        private boolean isVarargs = false;
        private boolean printTypeAnnotations = true;
        private boolean qualifyAnnotations = false;

        /** Create a new pretty printer with the default configuration. */
        public TypePrettyPrinter() { // NOPMD
            // default
        }

        StringBuilder append(char o) {
            return sb.append(o);
        }

        StringBuilder append(String o) {
            return sb.append(o);
        }

        /**
         * Print the declaring type of the method and its type parameters.
         * Default: true.
         */
        public TypePrettyPrinter printMethodHeader(boolean printMethodHeader) {
            this.printMethodHeader = printMethodHeader;
            return this;
        }

        /**
         * Print the return type of methods (as postfix).
         * Default: true.
         */
        public TypePrettyPrinter printMethodResult(boolean printMethodResult) {
            this.printMethodReturnType = printMethodResult;
            return this;
        }

        /**
         * Print the bounds of type variables.
         * Default: false.
         */
        public void printTypeVarBounds(OptionalBool printTypeVarBounds) {
            this.printTypeVarBounds = printTypeVarBounds;
        }

        /**
         * Qualify type variables with the name of the declaring symbol.
         * Eg {@code Foo#T} for {@code class Foo<T>}}.
         * Default: false.
         */
        public TypePrettyPrinter qualifyTvars(boolean qualifyTvars) {
            this.qualifyTvars = qualifyTvars;
            return this;
        }

        /**
         * Whether to print the binary name of a type annotation or
         * just the simple name if false.
         * Default: false.
         */
        public TypePrettyPrinter qualifyAnnotations(boolean qualifyAnnotations) {
            this.qualifyAnnotations = qualifyAnnotations;
            return this;
        }

        /**
         * Whether to print the type annotations.
         * Default: true.
         */
        public TypePrettyPrinter printAnnotations(boolean printAnnotations) {
            this.printTypeAnnotations = printAnnotations;
            return this;
        }

        /**
         * Use qualified names for class types instead of simple names.
         * Default: true.
         */
        public TypePrettyPrinter qualifyNames(boolean qualifyNames) {
            this.qualifyNames = qualifyNames;
            return this;
        }

        String consumeResult() {
            // The pretty printer might be reused by another call,
            // delete the buffer.
            String result = sb.toString();
            this.sb.setLength(0);
            return result;
        }

        private void printTypeAnnotations(PSet<SymAnnot> annots) {
            if (this.printTypeAnnotations) {
                for (SymAnnot annot : annots) {
                    String name = this.qualifyAnnotations ? annot.getBinaryName()
                                                          : annot.getSimpleName();
                    append('@').append(name).append(' ');
                }
            }
        }
    }

    private static final class PrettyPrintVisitor implements JTypeVisitor<Void, TypePrettyPrinter> {

        static final PrettyPrintVisitor INSTANCE = new PrettyPrintVisitor();

        @Override
        public Void visit(JTypeMirror t, TypePrettyPrinter sb) {
            sb.printTypeAnnotations(t.getTypeAnnotations());
            sb.append(t.toString());
            return null;
        }

        @Override
        public Void visitClass(JClassType t, TypePrettyPrinter sb) {

            JClassType enclosing = t.getEnclosingType();
            boolean isAnon = t.getSymbol().isAnonymousClass();

            if (enclosing != null && !isAnon) {
                visitClass(enclosing, sb);
                sb.append('#');
            } else if (t.hasErasedSuperTypes() && !t.isRaw()) {
                sb.append("(erased) ");
            }

            sb.printTypeAnnotations(t.getTypeAnnotations());

            if (t.getSymbol().isUnresolved()) {
                sb.append('*'); // a small marker to spot them
            }

            if (enclosing != null && !isAnon || !sb.qualifyNames) {
                sb.append(t.getSymbol().getSimpleName());
            } else {
                sb.append(t.getSymbol().getBinaryName());
            }
            List<JTypeMirror> targs = t.getTypeArgs();
            if (t.isRaw() || targs.isEmpty()) {
                return null;
            }

            if (t.isGenericTypeDeclaration() && sb.printTypeVarBounds != NO) {
                sb.printTypeVarBounds = YES;
            }
            join(sb, targs, ", ", "<", ">");
            return null;
        }

        @Override
        public Void visitWildcard(JWildcardType t, TypePrettyPrinter sb) {
            sb.printTypeAnnotations(t.getTypeAnnotations());
            sb.append("?");
            if (t.isUnbounded()) {
                return null;
            }

            sb.append(t.isUpperBound() ? " extends " : " super ");

            t.getBound().acceptVisitor(this, sb);
            return null;
        }

        @Override
        public Void visitPrimitive(JPrimitiveType t, TypePrettyPrinter sb) {
            sb.printTypeAnnotations(t.getTypeAnnotations());
            sb.append(t.getSimpleName());
            return null;
        }

        @Override
        public Void visitTypeVar(JTypeVar t, TypePrettyPrinter sb) {
            if (t instanceof CaptureMatcher) {
                sb.append(t.toString());
                return null;
            }
            if (!t.isCaptured() && sb.qualifyTvars) {
                JTypeParameterSymbol sym = t.getSymbol();
                if (sym != null) {
                    sb.append(sym.getDeclaringSymbol().getSimpleName());
                    sb.append('#');
                }
            }

            sb.printTypeAnnotations(t.getTypeAnnotations());
            sb.append(t.getName());

            if (sb.printTypeVarBounds == YES) {
                sb.printTypeVarBounds = NO;
                if (!t.getUpperBound().isTop()) {
                    sb.append(" extends ");
                    t.getUpperBound().acceptVisitor(this, sb);
                }
                if (!t.getLowerBound().isBottom()) {
                    sb.append(" super ");
                    t.getLowerBound().acceptVisitor(this, sb);
                }
                sb.printTypeVarBounds = YES;
            }
            return null;
        }

        /**
         * Formats {@link Arrays#asList(Object[])} as {@code <T> asList(T...) -> List<T>}
         */
        @Override
        public Void visitMethodType(JMethodSig t, TypePrettyPrinter sb) {
            if (sb.printMethodHeader) {
                t.getDeclaringType().acceptVisitor(this, sb);
                sb.append(".");

                if (t.isGeneric()) {
                    final OptionalBool printBounds = sb.printTypeVarBounds;
                    if (printBounds != NO) {
                        sb.printTypeVarBounds = YES;
                    }
                    join(sb, t.getTypeParameters(), ", ", "<", "> ", false);
                    sb.printTypeVarBounds = printBounds;
                }
            }

            sb.append(t.getName());

            join(sb, t.getFormalParameters(), ", ", "(", ")", t.isVarargs());

            if (sb.printMethodReturnType) {
                sb.append(" -> ");
                t.getReturnType().acceptVisitor(this, sb);
            }
            return null;
        }

        @Override
        public Void visitIntersection(JIntersectionType t, TypePrettyPrinter sb) {
            return join(sb, t.getComponents(), " & ", "", "");
        }

        @Override
        public Void visitArray(JArrayType t, TypePrettyPrinter sb) {
            JTypeMirror component = t.getComponentType();
            if (component instanceof JIntersectionType) {
                sb.append("(");
            }

            boolean isVarargs = sb.isVarargs;
            sb.isVarargs = false;
            component.acceptVisitor(this, sb);

            if (component instanceof JIntersectionType) {
                sb.append(")");
            }
            // todo I think the annotation placement might be wrong, add tests
            sb.printTypeAnnotations(t.getTypeAnnotations());
            sb.append(isVarargs ? "..." : "[]");
            return null;
        }

        @Override
        public Void visitNullType(JTypeMirror t, TypePrettyPrinter sb) {
            sb.append("null");
            return null;
        }

        @Override
        public Void visitInferenceVar(InferenceVar t, TypePrettyPrinter sb) {
            sb.append(t.getName());
            return null;
        }

        private Void join(TypePrettyPrinter sb, List<? extends JTypeMirror> ts, String delim, String prefix, String suffix) {
            return join(sb, ts, delim, prefix, suffix, false);
        }

        private Void join(TypePrettyPrinter sb, List<? extends JTypeMirror> types, String delim, String prefix, String suffix, boolean isVarargs) {
            sb.isVarargs = false;
            boolean empty = types.isEmpty();
            sb.append(prefix);
            if (!empty) {
                for (int i = 0; i < types.size() - 1; i++) {
                    types.get(i).acceptVisitor(this, sb);
                    sb.append(delim);
                }
                if (isVarargs) {
                    sb.isVarargs = true;
                }
                types.get(types.size() - 1).acceptVisitor(this, sb);
            }
            sb.append(suffix);
            return null;
        }
    }


}

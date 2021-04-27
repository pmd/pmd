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

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
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
        TypePrettyPrinter sb = new TypePrettyPrinter();
        t.acceptVisitor(PrettyPrintVisitor.INSTANCE, sb);
        return sb.getResult();
    }

    public static @NonNull String prettyPrint(@NonNull JMethodSig sig, boolean printHeader) {
        TypePrettyPrinter sb = new TypePrettyPrinter();
        sb.printMethodHeader = printHeader;
        sig.acceptVisitor(PrettyPrintVisitor.INSTANCE, sb);
        return sb.getResult();
    }

    public static @NonNull String prettyPrintWithTvarBounds(@NonNull JTypeMirror sig) {
        TypePrettyPrinter sb = new TypePrettyPrinter();
        sb.printTypeVarBounds = YES;
        sig.acceptVisitor(PrettyPrintVisitor.INSTANCE, sb);
        return sb.getResult();
    }

    public static @NonNull String prettyPrintWithSimpleNames(@NonNull JTypeMirror sig) {
        TypePrettyPrinter sb = new TypePrettyPrinter();
        sb.qualifyNames = false;
        sig.acceptVisitor(PrettyPrintVisitor.INSTANCE, sb);
        return sb.getResult();
    }

    public static String prettyPrintWithTvarQualifier(@NonNull JTypeMirror t) {
        TypePrettyPrinter sb = new TypePrettyPrinter();
        sb.qualifyTvars = true;
        t.acceptVisitor(PrettyPrintVisitor.INSTANCE, sb);
        return sb.getResult();
    }

    private static class TypePrettyPrinter {

        private final StringBuilder sb = new StringBuilder();

        private boolean printMethodHeader = true;
        private OptionalBool printTypeVarBounds = UNKNOWN;
        private boolean qualifyTvars = false;
        private boolean qualifyNames = true;
        private boolean isVarargs = false;


        StringBuilder append(Object o) {
            return sb.append(o);
        }


        public String getResult() {
            return sb.toString();
        }
    }

    private static class PrettyPrintVisitor implements JTypeVisitor<Void, TypePrettyPrinter> {

        static final PrettyPrintVisitor INSTANCE = new PrettyPrintVisitor();

        @Override
        public Void visit(JTypeMirror t, TypePrettyPrinter sb) {
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
            sb.append(t.getSimpleName());
            return null;
        }

        @Override
        public Void visitTypeVar(JTypeVar t, TypePrettyPrinter sb) {
            if (!t.isCaptured() && sb.qualifyTvars) {
                JTypeParameterSymbol sym = t.getSymbol();
                if (sym != null) {
                    sb.append(sym.getDeclaringSymbol().getSimpleName());
                    sb.append('#');
                }
            }
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

            join(sb, t.getFormalParameters(), ", ", "(", ") -> ", t.isVarargs());

            t.getReturnType().acceptVisitor(this, sb);
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

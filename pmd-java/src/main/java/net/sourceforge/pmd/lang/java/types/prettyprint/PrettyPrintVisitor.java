/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.prettyprint;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.CaptureMatcher;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JTypeVisitor;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.util.OptionalBool;

public class PrettyPrintVisitor<P extends TypePrettyPrinter> implements JTypeVisitor<Void, P> {

    @Override
    public Void visit(JTypeMirror t, P sb) {
        sb.printTypeAnnotations(t.getTypeAnnotations());
        sb.append(t.toString());
        return null;
    }

    @Override
    public Void visitClass(JClassType t, P sb) {

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

        appendClassName(t, sb, enclosing, isAnon);

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

    protected void appendClassName(JClassType t, P sb, JClassType enclosing, boolean isAnon) {
        if (enclosing != null && !isAnon || !sb.qualifyNames) {
            sb.append(t.getSymbol().getSimpleName());
        } else {
            sb.append(t.getSymbol().getBinaryName());
        }
    }

    @Override
    public Void visitWildcard(JWildcardType t, P sb) {
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
    public Void visitPrimitive(JPrimitiveType t, P sb) {
        sb.printTypeAnnotations(t.getTypeAnnotations());
        sb.append(t.getSimpleName());
        return null;
    }

    @Override
    public Void visitTypeVar(JTypeVar t, P sb) {
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
    public Void visitMethodType(JMethodSig t, P sb) {
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
    public Void visitIntersection(JIntersectionType t, P sb) {
        return join(sb, t.getComponents(), " & ", "", "");
    }

    @Override
    public Void visitArray(JArrayType t, P sb) {
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
    public Void visitNullType(JTypeMirror t, P sb) {
        sb.append("null");
        return null;
    }

    @Override
    public Void visitInferenceVar(InferenceVar t, P sb) {
        sb.append(t.getName());
        return null;
    }

    protected Void join(P sb, List<? extends JTypeMirror> ts, String delim, String prefix, String suffix) {
        return join(sb, ts, delim, prefix, suffix, false);
    }

    protected Void join(P sb, List<? extends JTypeMirror> types, String delim, String prefix, String suffix, boolean isVarargs) {
        sb.isVarargs = false;
        sb.append(prefix);
        if (!types.isEmpty()) {
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

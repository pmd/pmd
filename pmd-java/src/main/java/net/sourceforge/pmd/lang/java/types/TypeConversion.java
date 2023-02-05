/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static java.util.Arrays.asList;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.BYTE;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.CHAR;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.DOUBLE;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.FLOAT;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.LONG;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.SHORT;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Utility class for type conversions, as defined in <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-5.html">JLS§5</a>.
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class TypeConversion {

    private TypeConversion() {

    }

    /**
     * Performs <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-5.html#jls-5.6.1">Unary numeric promotion
     * (JLS§5.6.1)</a>.
     * <p>This occurs in the following situations:
     * <ul>
     * <li>Each dimension expression in an array creation expression (§15.10.1)
     * <li>The index expression in an array access expression (§15.10.3)
     * <li>The operand of a unary plus operator + (§15.15.3)
     * <li>The operand of a unary minus operator - (§15.15.4)
     * <li>The operand of a bitwise complement operator ~ (§15.15.5)
     * <li>Each operand, separately, of a shift operator &lt;&lt;, &gt;&gt;, or &gt;&gt;&gt; (§15.19).
     * </ul>
     *
     * <p>Returns {@link TypeSystem#ERROR} if the given type is
     * not a numeric type, {@link TypeSystem#UNKNOWN} if the type
     * is unresolved.
     */
    public static JTypeMirror unaryNumericPromotion(JTypeMirror t) {
        t = t.unbox();

        TypeSystem ts = t.getTypeSystem();

        if (t.isPrimitive(BYTE) || t.isPrimitive(SHORT) || t.isPrimitive(CHAR)) {
            return ts.INT;
        }

        return t.isNumeric() || t == ts.UNKNOWN ? t : ts.ERROR;
    }

    /**
     * JLS§5.6.2
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-5.html#jls-5.6.2
     *
     * Binary numeric promotion is performed on the operands of certain operators:
     * <ul>
     * <li>The multiplicative operators *, /, and % (§15.17)
     * <li>The addition and subtraction operators for numeric types + and - (§15.18.2)
     * <li>The numerical comparison operators &lt;, &lt;=, &gt;, and &gt;= (§15.20.1)
     * <li>The numerical equality operators == and != (§15.21.1)
     * <li>The integer bitwise operators &amp;, ^, and | (§15.22.1)
     * <li>In certain cases, the conditional operator ? : (§15.25)
     * </ul>
     * <p>Returns {@link TypeSystem#ERROR} if either of the parameters
     * is not numeric. This DOES NOT care for unresolved types.
     */
    public static JTypeMirror binaryNumericPromotion(JTypeMirror t, JTypeMirror s) {
        JTypeMirror t1 = t.unbox();
        JTypeMirror s1 = s.unbox();

        TypeSystem ts = t.getTypeSystem();

        if (t1.isPrimitive(DOUBLE) || s1.isPrimitive(DOUBLE)) {
            return ts.DOUBLE;
        } else if (t1.isPrimitive(FLOAT) || s1.isPrimitive(FLOAT)) {
            return ts.FLOAT;
        } else if (t1.isPrimitive(LONG) || s1.isPrimitive(LONG)) {
            return ts.LONG;
        } else if (t1.isNumeric() && s1.isNumeric()) {
            return ts.INT;
        } else {
            // this is a typing error, both types should be referring to a numeric type
            return ts.ERROR;
        }
    }

    /**
     * Is t convertible to s by boxing/unboxing/widening conversion?
     * Only t can undergo conversion.
     */
    public static boolean isConvertibleUsingBoxing(JTypeMirror t, JTypeMirror s) {
        return isConvertibleCommon(t, s, false);
    }

    /**
     * Is t convertible to s by boxing/unboxing conversion?
     * Only t can undergo conversion.
     */
    public static boolean isConvertibleInCastContext(JTypeMirror t, JTypeMirror s) {
        return isConvertibleCommon(t, s, true);
    }

    private static boolean isConvertibleCommon(JTypeMirror t, JTypeMirror s, boolean isCastContext) {
        TypeSystem ts = t.getTypeSystem();
        if (t == ts.UNKNOWN || t == ts.ERROR) {
            return true;
        }

        if (t instanceof InferenceVar || s instanceof InferenceVar) {
            return t.box().isSubtypeOf(s.box());
        }

        if (t.isPrimitive() == s.isPrimitive()) {
            return t.isConvertibleTo(s).bySubtyping();
        }

        if (isCastContext) {
            return t.isPrimitive() ? t.box().isConvertibleTo(s).bySubtyping()
                                   : t.isConvertibleTo(s.box()).bySubtyping();
        } else {
            return t.isPrimitive() ? t.box().isConvertibleTo(s).somehow()
                                   : t.unbox().isConvertibleTo(s).somehow();
        }
    }


    /**
     * Perform capture conversion on the type t. This replaces wildcards
     * with fresh type variables. Capture conversion is not applied recursively.
     * Capture conversion on any type other than a parameterized type (§4.5) acts
     * as an identity conversion (§5.1.1).
     *
     * @return The capture conversion of t
     */
    public static JTypeMirror capture(JTypeMirror t) {
        return t instanceof JClassType ? capture((JClassType) t) : t;
    }

    /**
     * Perform capture conversion on the type t. This replaces wildcards
     * with fresh type variables. Capture conversion is not applied recursively.
     * Capture conversion on any type other than a parameterized type (§4.5) acts
     * as an identity conversion (§5.1.1).
     *
     * @return The capture conversion of t
     */
    public static JClassType capture(JClassType type) {
        if (type == null) {
            return null;
        }

        final @Nullable JClassType enclosing = capture(type.getEnclosingType());
        if (enclosing == type.getEnclosingType() && !isWilcardParameterized(type)) {
            return type; // 99% take this path
        }

        TypeSystem ts = type.getTypeSystem();
        List<JTypeMirror> typeArgs = type.getTypeArgs();
        List<JTypeVar> typeParams = type.getFormalTypeParams();

        // This is the algorithm described at https://docs.oracle.com/javase/specs/jls/se10/html/jls-5.html#jls-5.1.10

        // Let G name a generic type declaration (§8.1.2, §9.1.2)
        // with n type parameters A1,...,An with corresponding bounds U1,...,Un.

        // There exists a capture conversion from a parameterized type G<T1,...,Tn> (§4.5)
        // to a parameterized type G<S1,...,Sn>, where, for 1 ≤ i ≤ n :
        // -> see the loop

        // typeParams is A1..An
        // typeArgs is T1..Tn
        // freshVars is S1..Sn

        List<JTypeMirror> freshVars = makeFreshVars(type);

        // types may be non-well formed if the symbol is unresolved
        // in this case the typeParams list is most likely empty
        boolean wellFormed = typeParams.size() == freshVars.size();

        // Map of Ai to Si, for the substitution
        Substitution subst = wellFormed ? Substitution.mapping(typeParams, freshVars) : Substitution.EMPTY;

        for (int i = 0; i < typeArgs.size(); i++) {
            JTypeMirror fresh = freshVars.get(i);       // Si
            JTypeMirror arg = typeArgs.get(i);          // Ti

            // we mutate the bounds to preserve the correct instance in
            // the substitutions

            if (arg instanceof JWildcardType) {
                JWildcardType w = (JWildcardType) arg;        // Ti alias
                TypeVarImpl.CapturedTypeVar freshVar = (TypeVarImpl.CapturedTypeVar) fresh; // Si alias

                JTypeMirror prevUpper = wellFormed ? typeParams.get(i).getUpperBound() : ts.OBJECT; // Ui
                JTypeMirror substituted = TypeOps.subst(prevUpper, subst);

                if (w.isUnbounded()) {
                    // If Ti is a wildcard type argument (§4.5.1) of the form ?,
                    // then Si is a fresh type variable whose upper bound is Ui[A1:=S1,...,An:=Sn]
                    // and whose lower bound is the null type (§4.1).

                    freshVar.setUpperBound(substituted);
                    freshVar.setLowerBound(ts.NULL_TYPE);

                } else if (w.isUpperBound()) {
                    // If Ti is a wildcard type argument of the form ? extends Bi,
                    // then Si is a fresh type variable whose upper bound is glb(Bi, Ui[A1:=S1,...,An:=Sn])
                    // and whose lower bound is the null type.
                    freshVar.setUpperBound(ts.glb(asList(substituted, w.getBound())));
                    freshVar.setLowerBound(ts.NULL_TYPE);

                } else {
                    // If Ti is a wildcard type argument of the form ? super Bi,
                    // then Si is a fresh type variable whose upper bound is Ui[A1:=S1,...,An:=Sn]
                    // and whose lower bound is Bi.
                    freshVar.setUpperBound(substituted);
                    freshVar.setLowerBound(w.getBound());
                }
            }
        }

        if (enclosing != null) {
            return enclosing.selectInner(type.getSymbol(), freshVars);
        } else {
            return type.withTypeArguments(freshVars);
        }
    }

    /**
     * Returns true if the type is a parameterized class type, which has
     * wildcards as type arguments. Capture variables don't count.
     */
    public static boolean isWilcardParameterized(JTypeMirror t) {
        return t instanceof JClassType
                && CollectionUtil.any(((JClassType) t).getTypeArgs(), it -> it instanceof JWildcardType);
    }


    private static List<JTypeMirror> makeFreshVars(JClassType type) {
        List<JTypeMirror> freshVars = new ArrayList<>(type.getTypeArgs().size());
        for (JTypeMirror typeArg : type.getTypeArgs()) {
            if (typeArg instanceof JWildcardType) {
                freshVars.add(TypeVarImpl.freshCapture((JWildcardType) typeArg));
            } else {
                freshVars.add(typeArg);
            }
        }
        return freshVars;
    }

}

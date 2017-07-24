/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;


import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.CONTAINS;
import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.EQUALITY;
import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.LOOSE_INVOCATION;
import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.SUBTYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;


import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Bound;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.BoundOrConstraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Constraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Variable;

public class TypeInferenceTest {
    private JavaTypeDefinition number = JavaTypeDefinition.forClass(Number.class);
    private JavaTypeDefinition integer = JavaTypeDefinition.forClass(Integer.class);
    private JavaTypeDefinition primitiveInt = JavaTypeDefinition.forClass(int.class);
    private JavaTypeDefinition generic = JavaTypeDefinition.forClass(Map.class, number, integer);
    private Variable a = new Variable();
    private Variable b = new Variable();

    @Test
    public void testEqualityReduceProperVsProper() {
        // If S and T are proper types, the constraint reduces to true if S is the same as T (§4.3.4), and false
        // otherwise.
        assertTrue(new Constraint(number, number, EQUALITY).reduce().isEmpty());
        assertEquals(new Constraint(number, integer, EQUALITY).reduce(), null);

        // Otherwise, if S or T is the null type, the constraint reduces to false. TODO
    }

    @Test
    public void testEqualityReduceVariableVsNotPrimitive() {
        // Otherwise, if S is an inference variable, α, and T is not a primitive type, the constraint reduces to
        // the bound α = T.
        List<BoundOrConstraint> result = new Constraint(a, number, EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), a, number, EQUALITY, Bound.class);
    }

    @Test
    public void testEqualityReduceNotPrimitiveVsVariable() {
        // Otherwise, if T is an inference variable, α, and S is not a primitive type, the constraint reduces
        // to the bound S = α.
        List<BoundOrConstraint> result = new Constraint(number, a, EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, a, EQUALITY, Bound.class);

        result = new Constraint(a, b, EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), a, b, EQUALITY, Bound.class);
    }

    @Test
    public void testEqualityReduceSameErasure() {
        // Otherwise, if S and T are class or interface types with the same erasure, where S has type
        // arguments B1, ..., Bn and T has type arguments A1, ..., An, the constraint reduces to the
        // following new constraints: for all i (1 ≤ i ≤ n), ‹Bi = Ai›.
        List<BoundOrConstraint> result = new Constraint(generic, generic, EQUALITY).reduce();
        assertEquals(result.size(), 2);
        testBoundOrConstraint(result.get(0), number, number, EQUALITY, Constraint.class);
        testBoundOrConstraint(result.get(1), integer, integer, EQUALITY, Constraint.class);
    }

    @Test
    public void testEqualityReduceArrayTypes() {
        // Otherwise, if S and T are array types, S'[] and T'[], the constraint reduces to ‹S' = T'›.
        List<BoundOrConstraint> result = new Constraint(JavaTypeDefinition.forClass(Number[].class),
                                                        JavaTypeDefinition.forClass(Integer[].class), EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, integer, EQUALITY, Constraint.class);
    }

    @Test
    public void testSubtypeReduceProperVsProper() {
        // A constraint formula of the form ‹S <: T› is reduced as follows:

        // If S and T are proper types, the constraint reduces to true if S is a subtype of T (§4.10),
        // and false otherwise.
        List<BoundOrConstraint> result = new Constraint(integer, number, SUBTYPE).reduce();
        assertEquals(result.size(), 0);
        result = new Constraint(number, integer, SUBTYPE).reduce();
        assertEquals(result, null);


        // Otherwise, if S is the null type, the constraint reduces to true. TODO

        // Otherwise, if T is the null type, the constraint reduces to false. TODO
    }

    @Test
    public void testSubtypeReduceVariableVsAny() {
        // Otherwise, if S is an inference variable, α, the constraint reduces to the bound α <: T.
        List<BoundOrConstraint> result = new Constraint(a, integer, SUBTYPE).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), a, integer, SUBTYPE, Bound.class);
    }

    @Test
    public void testSubtypeReduceAnyVsVariable() {
        // Otherwise, if T is an inference variable, α, the constraint reduces to the bound S <: α.
        List<BoundOrConstraint> result = new Constraint(integer, a, SUBTYPE).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), integer, a, SUBTYPE, Bound.class);

        result = new Constraint(a, b, SUBTYPE).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), a, b, SUBTYPE, Bound.class);
    }


    @Test
    public void testLooseInvocationProperVsProper() {
        // A constraint formula of the form ‹S → T› is reduced as follows:

        // If S and T are proper types, the constraint reduces to true if S is compatible in a loose invocation
        // context with T (§5.3), and false otherwise.
        List<BoundOrConstraint> result = new Constraint(number, integer, LOOSE_INVOCATION).reduce();
        assertEquals(result, null);

        result = new Constraint(integer, number, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 0);
    }

    @Test
    public void testLooseInvocationLeftBoxing() {
        // Otherwise, if S is a primitive type, let S' be the result of applying boxing conversion (§5.1.7) to S.
        // Then the constraint reduces to ‹S' → T›.
        List<BoundOrConstraint> result = new Constraint(primitiveInt, number, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), integer, number, LOOSE_INVOCATION, Constraint.class);

    }

    @Test
    public void testLooseInvocationRightBoxing() {
        // Otherwise, if T is a primitive type, let T' be the result of applying boxing conversion (§5.1.7) to T.
        // Then the constraint reduces to ‹S = T'›.
        List<BoundOrConstraint> result = new Constraint(number, primitiveInt, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, integer, EQUALITY, Constraint.class);

        // Otherwise, if T is a parameterized type of the form G<T1, ..., Tn>, and there exists no type of the
        // form G<...> that is a supertype of S, but the raw type G is a supertype of S, then the constraint
        // reduces to true. TODO

        // Otherwise, if T is an array type of the form G<T1, ..., Tn>[]k, and there exists no type of the form
        // G<...>[]k that is a supertype of S, but the raw type G[]k is a supertype of S, then the constraint
        // reduces to true. (The notation []k indicates an array type of k dimensions.) TODO

    }

    @Test
    public void testLooseInvocationAnythingElse() {
        // Otherwise, the constraint reduces to ‹S<:T›.
        List<BoundOrConstraint> result = new Constraint(number, a, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, a, SUBTYPE, Constraint.class);

        result = new Constraint(a, number, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), a, number, SUBTYPE, Constraint.class);
    }

    @Test
    public void testContainmentReduceTypeVsType() {
        // A constraint formula of the form ‹S <= T›, where S and T are type arguments (§4.5.1), is reduced as
        // follows:

        // If T is a type: // If S is a type, the constraint reduces to ‹S = T›.
        List<BoundOrConstraint> result = new Constraint(number, integer, CONTAINS).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, integer, EQUALITY, Constraint.class);

        // If T is a type: // If S is a wildcard, the constraint reduces to false. TODO


        // If T is a wildcard of the form ?, the constraint reduces to true. TODO

        // If T is a wildcard of the form ? extends T': TODO


        // If T is a wildcard of the form ? super T': TODO
    }


    private void testBoundOrConstraint(BoundOrConstraint val, JavaTypeDefinition left, JavaTypeDefinition right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftProper(), left);
        assertEquals(val.rightProper(), right);
        assertEquals(val.getRuleType(), rule);
    }


    private void testBoundOrConstraint(BoundOrConstraint val, JavaTypeDefinition left, Variable right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftProper(), left);
        assertEquals(val.rightVariable(), right);
        assertEquals(val.getRuleType(), rule);
    }

    private void testBoundOrConstraint(BoundOrConstraint val, Variable left, JavaTypeDefinition right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftVariable(), left);
        assertEquals(val.rightProper(), right);
        assertEquals(val.getRuleType(), rule);
    }

    private void testBoundOrConstraint(BoundOrConstraint val, Variable left, Variable right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftVariable(), left);
        assertEquals(val.rightVariable(), right);
        assertEquals(val.getRuleType(), rule);
    }
}

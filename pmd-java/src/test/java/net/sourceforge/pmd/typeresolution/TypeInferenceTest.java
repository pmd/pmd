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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.taskdefs.Java;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Bound;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.BoundOrConstraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Constraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.TypeInferenceResolver;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Variable;

public class TypeInferenceTest {
    private JavaTypeDefinition number = JavaTypeDefinition.forClass(Number.class);
    private JavaTypeDefinition integer = JavaTypeDefinition.forClass(Integer.class);
    private JavaTypeDefinition primitiveInt = JavaTypeDefinition.forClass(int.class);
    private JavaTypeDefinition generic = JavaTypeDefinition.forClass(Map.class, number, integer);
    private Variable alpha = new Variable();
    private Variable beta = new Variable();
    JavaTypeDefinition s = JavaTypeDefinition.forClass(int.class);
    JavaTypeDefinition t = JavaTypeDefinition.forClass(double.class);

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
        List<BoundOrConstraint> result = new Constraint(alpha, number, EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), alpha, number, EQUALITY, Bound.class);
    }

    @Test
    public void testEqualityReduceNotPrimitiveVsVariable() {
        // Otherwise, if T is an inference variable, α, and S is not a primitive type, the constraint reduces
        // to the bound S = α.
        List<BoundOrConstraint> result = new Constraint(number, alpha, EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, alpha, EQUALITY, Bound.class);

        result = new Constraint(alpha, beta, EQUALITY).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), alpha, beta, EQUALITY, Bound.class);
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
                                                        JavaTypeDefinition.forClass(Integer[].class), EQUALITY)
                .reduce();
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
        List<BoundOrConstraint> result = new Constraint(alpha, integer, SUBTYPE).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), alpha, integer, SUBTYPE, Bound.class);
    }

    @Test
    public void testSubtypeReduceAnyVsVariable() {
        // Otherwise, if T is an inference variable, α, the constraint reduces to the bound S <: α.
        List<BoundOrConstraint> result = new Constraint(integer, alpha, SUBTYPE).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), integer, alpha, SUBTYPE, Bound.class);

        result = new Constraint(alpha, beta, SUBTYPE).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), alpha, beta, SUBTYPE, Bound.class);
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
        List<BoundOrConstraint> result = new Constraint(number, alpha, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), number, alpha, SUBTYPE, Constraint.class);

        result = new Constraint(alpha, number, LOOSE_INVOCATION).reduce();
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), alpha, number, SUBTYPE, Constraint.class);
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

    @Test
    public void testIncorporationEqualityAndEquality() {
        List<Constraint> result;

        // ### Original rule 1. : α = S and α = T imply ‹S = T›
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(alpha, t, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);

        // α = S and T = α imply ‹S = T›
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(t, alpha, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);

        // S = α and α = T imply ‹S = T›
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(alpha, t, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);

        // S = α and T = α imply ‹S = T›
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(t, alpha, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);
    }

    @Test
    public void testIncorporationEqualityAndSubtypeLeftVariable() {
        List<Constraint> result;

        // ### Original rule 2. : α = S and α <: T imply ‹S <: T›
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(alpha, t, SUBTYPE));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);

        // S = α and α <: T imply ‹S <: T›
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(alpha, t, SUBTYPE));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);

        // α <: T and α = S imply ‹S <: T›
        result = incorporationResult(new Bound(alpha, t, SUBTYPE), new Bound(alpha, s, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);

        // α <: T and S = α imply ‹S <: T›
        result = incorporationResult(new Bound(alpha, t, SUBTYPE), new Bound(s, alpha, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
    }

    @Test
    public void testIncorporationEqualityAndSubtypeRightVariable() {
        List<Constraint> result;

        // ### Original rule 3. : α = S and T <: α imply ‹T <: S›
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(t, alpha, SUBTYPE));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);

        // S = α and T <: α imply ‹T <: S›
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(t, alpha, SUBTYPE));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);

        // T <: α and α = S imply ‹T <: S›
        result = incorporationResult(new Bound(t, alpha, SUBTYPE), new Bound(alpha, s, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);

        // T <: α and S = α imply ‹T <: S›
        result = incorporationResult(new Bound(t, alpha, SUBTYPE), new Bound(s, alpha, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);
    }

    @Test
    public void testIncorporationSubtypeAndSubtype() {
        List<Constraint> result;

        // ### Original rule 4. : S <: α and α <: T imply ‹S <: T›
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(alpha, t, SUBTYPE));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);

        // α <: T and S <: α imply ‹S <: T›
        result = incorporationResult(new Bound(alpha, t, SUBTYPE), new Bound(s, alpha, EQUALITY));
        assertEquals(result.size(), 1);
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);

    }

    @Test
    public void testErasedCandidateSet() {
        List<JavaTypeDefinition> types = new ArrayList<>();
        types.add(JavaTypeDefinition.forClass(List.class));
        types.add(JavaTypeDefinition.forClass(Set.class));

        Set<Class<?>> erasedCandidate = TypeInferenceResolver.getErasedCandidateSet(types);

        assertEquals(erasedCandidate.size(), 3);
        assertTrue(erasedCandidate.contains(Object.class));
        assertTrue(erasedCandidate.contains(Collection.class));
        assertTrue(erasedCandidate.contains(Iterable.class));
    }

    @Test
    public void testMinimalErasedCandidateSet() {
        Set<Class<?>> minimalSet = TypeInferenceResolver.getMinimalErasedCandidateSet(
                JavaTypeDefinition.forClass(List.class).getErasedSuperTypeSet());

        assertEquals(minimalSet.size(), 1);
        assertTrue(minimalSet.contains(List.class));
    }

    @Test
    public void testLeastUpperBound() {
        List<JavaTypeDefinition> lowerBounds = new ArrayList<>();
        lowerBounds.add(JavaTypeDefinition.forClass(String.class));
        lowerBounds.add(JavaTypeDefinition.forClass(.class));

        JavaTypeDefinition result = TypeInferenceResolver.lub(lowerBounds);
    }

    private List<Constraint> incorporationResult(Bound firstBound, Bound secondBound) {
        List<Bound> current = new ArrayList<>();
        List<Bound> newBounds = new ArrayList<>();
        current.add(firstBound);
        newBounds.add(secondBound);
        return TypeInferenceResolver.incorporateBounds(current, newBounds);
    }


    private void testBoundOrConstraint(BoundOrConstraint val, JavaTypeDefinition left, JavaTypeDefinition right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftProper(), left);
        assertEquals(val.rightProper(), right);
        assertEquals(val.ruleType(), rule);
    }


    private void testBoundOrConstraint(BoundOrConstraint val, JavaTypeDefinition left, Variable right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftProper(), left);
        assertEquals(val.rightVariable(), right);
        assertEquals(val.ruleType(), rule);
    }

    private void testBoundOrConstraint(BoundOrConstraint val, Variable left, JavaTypeDefinition right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftVariable(), left);
        assertEquals(val.rightProper(), right);
        assertEquals(val.ruleType(), rule);
    }

    private void testBoundOrConstraint(BoundOrConstraint val, Variable left, Variable right,
                                       InferenceRuleType rule, Class<? extends BoundOrConstraint> type) {
        assertTrue(val.getClass() == type);
        assertEquals(val.leftVariable(), left);
        assertEquals(val.rightVariable(), right);
        assertEquals(val.ruleType(), rule);
    }
}

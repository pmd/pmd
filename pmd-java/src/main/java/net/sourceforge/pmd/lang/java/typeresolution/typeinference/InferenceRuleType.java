/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typeinference;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


@Deprecated
@InternalApi
public enum InferenceRuleType {

    /**
     * https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html#jls-18.2.4
     */
    EQUALITY {
        @Override
        public List<BoundOrConstraint> reduce(BoundOrConstraint val) {
            // A constraint formula of the form ‹S = T›, where S and T are types, is reduced as follows:

            if (val.isLeftType() && val.isRightType()) {

                List<BoundOrConstraint> newConstraints = new ArrayList<>();

                // If S and T are proper types, the constraint reduces to true if S is the same as T (§4.3.4), and false
                // otherwise.
                if (val.isLeftProper() && val.isRightProper()) {
                    if (val.leftProper().equals(val.rightProper())) {
                        return newConstraints;
                    } else {
                        return null;
                    }
                }

                // Otherwise, if S or T is the null type, the constraint reduces to false.
                if (val.isLeftNull() || val.isRightNull()) {
                    return null;
                }

                // Otherwise, if S is an inference variable, α, and T is not a primitive type, the constraint reduces to
                // the bound α = T.
                if (val.isLeftVariable() && !val.isRightPrimitive()) {
                    newConstraints.add(copyBound(val, EQUALITY));
                    return newConstraints;
                }

                // Otherwise, if T is an inference variable, α, and S is not a primitive type, the constraint reduces
                // to the bound S = α.
                if (val.isRightVariable() && !val.isLeftPrimitive()) {
                    newConstraints.add(copyBound(val, EQUALITY));
                    return newConstraints;
                }

                // Otherwise, if S and T are class or interface types with the same erasure, where S has type
                // arguments B1, ..., Bn and T has type arguments A1, ..., An, the constraint reduces to the
                // following new constraints: for all i (1 ≤ i ≤ n), ‹Bi = Ai›.
                if (val.isLeftClassOrInterface() && val.isRightClassOrInterface()
                        && val.leftProper().hasSameErasureAs(val.rightProper())) {
                    JavaTypeDefinition right = val.rightProper();
                    JavaTypeDefinition left = val.leftProper();
                    for (int index = 0; index < right.getTypeParameterCount(); ++index) {
                        newConstraints.add(new Constraint(left.getGenericType(index),
                                                          right.getGenericType(index), EQUALITY));
                    }

                    return newConstraints;
                }

                // Otherwise, if S and T are array types, S'[] and T'[], the constraint reduces to ‹S' = T'›.
                if (val.isLeftArray() && val.isRightArray()) {
                    newConstraints.add(new Constraint(val.leftProper().getComponentType(),
                                                      val.rightProper().getComponentType(), EQUALITY));
                    return newConstraints;
                }

                // Otherwise, the constraint reduces to false.
                return null;
            }

            // A constraint formula of the form ‹S = T›, where S and T are type arguments (§4.5.1), is reduced as
            // follows: TODO

            // TODO: Reduce to false for the time being, reduction is still incomplete
            return null;
            //throw new IllegalStateException("Reduce method is flawed! " + val.toString());
        }
    },

    SUBTYPE {
        @Override
        public List<BoundOrConstraint> reduce(BoundOrConstraint val) {
            // A constraint formula of the form ‹S <: T› is reduced as follows:

            List<BoundOrConstraint> newConstraints = new ArrayList<>();

            // If S and T are proper types, the constraint reduces to true if S is a subtype of T (§4.10),
            // and false otherwise.
            if (val.isLeftProper() && val.isRightProper()) {
                if (MethodTypeResolution.isSubtypeable(val.rightProper(), val.leftProper())) {
                    return newConstraints;
                } else {
                    return null;
                }
            }

            // Otherwise, if S is the null type, the constraint reduces to true.
            if (val.isLeftNull()) {
                return newConstraints;
            }

            // Otherwise, if T is the null type, the constraint reduces to false.
            if (val.isRightNull()) {
                return null;
            }

            // Otherwise, if S is an inference variable, α, the constraint reduces to the bound α <: T.
            if (val.isLeftVariable()) {
                newConstraints.add(copyBound(val, SUBTYPE));
                return newConstraints;
            }

            // Otherwise, if T is an inference variable, α, the constraint reduces to the bound S <: α.
            if (val.isRightVariable()) {
                newConstraints.add(copyBound(val, SUBTYPE));
                return newConstraints;
            }

            // Otherwise, the constraint is reduced according to the form of T: TODO

            // TODO: Reduce to false for the time being, reduction is still incomplete
            return null;
            //throw new IllegalStateException("Reduce method is flawed! " + val.toString());
        }
    },

    LOOSE_INVOCATION {
        @Override
        public List<BoundOrConstraint> reduce(BoundOrConstraint val) {
            List<BoundOrConstraint> newConstraints = new ArrayList<>();

            // TODO: expression loose invocation rules

            // A constraint formula of the form ‹S → T› is reduced as follows:

            // If S and T are proper types, the constraint reduces to true if S is compatible in a loose invocation
            // context with T (§5.3), and false otherwise.
            if (val.isLeftProper() && val.isRightProper()) {
                if (MethodTypeResolution.isMethodConvertible(val.rightProper(), val.leftProper())) {
                    return newConstraints;
                } else {
                    return null;
                }
            }

            // Otherwise, if S is a primitive type, let S' be the result of applying boxing conversion (§5.1.7) to S.
            // Then the constraint reduces to ‹S' → T›.
            if (val.isLeftPrimitive()) {
                if (val.rightProper() != null) {
                    newConstraints.add(new Constraint(MethodTypeResolution.boxPrimitive(val.leftProper()),
                                                      val.rightProper(), LOOSE_INVOCATION));
                } else {
                    newConstraints.add(new Constraint(MethodTypeResolution.boxPrimitive(val.leftProper()),
                                                      val.rightVariable(), LOOSE_INVOCATION));
                }

                return newConstraints;
            }

            // Otherwise, if T is a primitive type, let T' be the result of applying boxing conversion (§5.1.7) to T.
            // Then the constraint reduces to ‹S = T'›.
            if (val.isRightPrimitive()) {
                if (val.leftProper() != null) {
                    newConstraints.add(new Constraint(val.leftProper(), MethodTypeResolution.boxPrimitive(val.rightProper()),
                                                      EQUALITY));
                } else {
                    newConstraints.add(new Constraint(val.leftVariable(), MethodTypeResolution.boxPrimitive(val.rightProper()),
                                                      EQUALITY));
                }

                return newConstraints;
            }

            // Otherwise, if T is a parameterized type of the form G<T1, ..., Tn>, and there exists no type of the
            // form G<...> that is a supertype of S, but the raw type G is a supertype of S, then the constraint
            // reduces to true. TODO

            // Otherwise, if T is an array type of the form G<T1, ..., Tn>[]k, and there exists no type of the form
            // G<...>[]k that is a supertype of S, but the raw type G[]k is a supertype of S, then the constraint
            // reduces to true. (The notation []k indicates an array type of k dimensions.) TODO

            // Otherwise, the constraint reduces to ‹S<:T›.
            newConstraints.add(copyConstraint(val, SUBTYPE));
            return newConstraints;
        }
    },

    CONTAINS {
        @Override
        public List<BoundOrConstraint> reduce(BoundOrConstraint val) {
            List<BoundOrConstraint> newConstraints = new ArrayList<>();

            // A constraint formula of the form ‹S <= T›, where S and T are type arguments (§4.5.1), is reduced as
            // follows:

            // If T is a type:
            if (val.isRightType()) {
                // If S is a type, the constraint reduces to ‹S = T›.
                if (val.isLeftType()) {
                    newConstraints.add(copyConstraint(val, EQUALITY));

                    return newConstraints;
                }

                // If S is a wildcard, the constraint reduces to false. TODO
            }

            // If T is a wildcard of the form ?, the constraint reduces to true. TODO

            // If T is a wildcard of the form ? extends T': TODO


            // If T is a wildcard of the form ? super T': TODO

            // TODO: Reduce to false for the time being, reduction is still incomplete
            return null;
            //throw new IllegalStateException("Reduce method is flawed! " + val.toString());
        }
    };


    private static Bound copyBound(BoundOrConstraint val, InferenceRuleType rule) {
        if (val.leftProper() != null) {
            if (val.rightProper() != null) {
                return new Bound(val.leftProper(), val.rightProper(), rule);
            } else {
                return new Bound(val.leftProper(), val.rightVariable(), rule);
            }
        } else {
            if (val.rightProper() != null) {
                return new Bound(val.leftVariable(), val.rightProper(), rule);
            } else {
                return new Bound(val.leftVariable(), val.rightVariable(), rule);
            }
        }
    }

    private static Constraint copyConstraint(BoundOrConstraint val, InferenceRuleType rule) {
        if (val.leftProper() != null) {
            if (val.rightProper() != null) {
                return new Constraint(val.leftProper(), val.rightProper(), rule);
            } else {
                return new Constraint(val.leftProper(), val.rightVariable(), rule);
            }
        } else {
            if (val.rightProper() != null) {
                return new Constraint(val.leftVariable(), val.rightProper(), rule);
            } else {
                return new Constraint(val.leftVariable(), val.rightVariable(), rule);
            }
        }
    }

    public abstract List<BoundOrConstraint> reduce(BoundOrConstraint constraint);
}

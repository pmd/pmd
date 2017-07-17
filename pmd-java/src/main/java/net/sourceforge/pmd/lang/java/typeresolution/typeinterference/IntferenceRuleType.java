package net.sourceforge.pmd.lang.java.typeresolution.typeinterference;

import net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

import java.util.ArrayList;
import java.util.List;

public enum IntferenceRuleType {
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
                    newConstraints.add(new Bound(val.leftVariable(), val.rightProper(), EQUALITY));
                    return newConstraints;
                }

                // Otherwise, if T is an inference variable, α, and S is not a primitive type, the constraint reduces
                // to the bound S = α.
                if (val.isRightVariable() && !val.isLeftPrimitive()) {
                    newConstraints.add(new Bound(val.leftProper(), val.rightVariable(), EQUALITY));
                    return newConstraints;
                }

                // Otherwise, if S and T are class or interface types with the same erasure, where S has type
                // arguments B1, ..., Bn and T has type arguments A1, ..., An, the constraint reduces to the
                // following new constraints: for all i (1 ≤ i ≤ n), ‹Bi = Ai›.
                if (val.isLeftClassOrInterface() && val.isRightClassOrInterface() &&
                        val.leftProper().hasSameErasureAs(val.rightProper())) {
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

            throw new IllegalStateException("Reduce method is flawed! " + val.toString());
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
                if (MethodTypeResolution.isSubtypeable(val.leftProper(), val.rightProper())) {
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
                if (val.isRightProper()) {
                    newConstraints.add(new Bound(val.leftVariable(), val.rightProper(), SUBTYPE));
                } else if (val.isRightVariable()) {
                    newConstraints.add(new Bound(val.leftVariable(), val.rightVariable(), SUBTYPE));
                } else {
                    throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                }

                return newConstraints;
            }

            // Otherwise, if T is an inference variable, α, the constraint reduces to the bound S <: α.
            if (val.isRightVariable()) {
                if (val.isLeftProper()) {
                    newConstraints.add(new Bound(val.leftProper(), val.rightVariable(), SUBTYPE));
                } else if (val.isLeftVariable()) {
                    newConstraints.add(new Bound(val.leftVariable(), val.rightVariable(), SUBTYPE));
                } else {
                    throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                }

                return newConstraints;
            }

            // Otherwise, the constraint is reduced according to the form of T: TODO

            throw new IllegalStateException("Reduce method is flawed! " + val.toString());
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
                if (val.isRightProper()) {
                    newConstraints.add(new Constraint(MethodTypeResolution.boxPrimitive(val.leftProper()),
                                                      val.rightProper(), LOOSE_INVOCATION));
                } else if (val.isRightVariable()) {
                    newConstraints.add(new Constraint(MethodTypeResolution.boxPrimitive(val.leftProper()),
                                                      val.rightVariable(), LOOSE_INVOCATION));
                } else {
                    throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                }

                return newConstraints;
            }

            // Otherwise, if T is a primitive type, let T' be the result of applying boxing conversion (§5.1.7) to T.
            // Then the constraint reduces to ‹S = T'›.
            if (val.isRightPrimitive()) {
                if (val.leftProper() != null) {
                    newConstraints.add(new Constraint(val.leftProper(), MethodTypeResolution.boxPrimitive(val.rightProper()),
                                                      EQUALITY));
                } else if (val.leftVariable() != null) {
                    newConstraints.add(new Constraint(val.leftVariable(), MethodTypeResolution.boxPrimitive(val.rightProper()),
                                                      EQUALITY));
                } else {
                    throw new IllegalStateException("Reduce method is flawed! " + val.toString());
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
            if (val.leftProper() != null) {
                if (val.rightProper() != null) {
                    newConstraints.add(new Constraint(val.leftProper(), val.rightProper(), SUBTYPE));
                } else if (val.rightVariable() != null) {
                    newConstraints.add(new Constraint(val.leftProper(), val.rightVariable(), SUBTYPE));
                } else {
                    throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                }
            } else if (val.leftVariable() != null) {
                if (val.rightProper() != null) {
                    newConstraints.add(new Constraint(val.leftVariable(), val.rightProper(), SUBTYPE));
                } else if (val.rightVariable() != null) {
                    newConstraints.add(new Constraint(val.leftVariable(), val.rightVariable(), SUBTYPE));
                } else {
                    throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                }
            } else {
                throw new IllegalStateException("Reduce method is flawed! " + val.toString());
            }

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

                    if (val.isRightProper()) {
                        if (val.isLeftProper()) {
                            newConstraints.add(new Constraint(val.leftProper(), val.rightProper(), EQUALITY));
                        } else if (val.isLeftVariable()) {
                            newConstraints.add(new Constraint(val.leftVariable(), val.rightProper(), EQUALITY));
                        } else {
                            throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                        }
                    } else if (val.isRightVariable()) {
                        if (val.isLeftProper()) {
                            newConstraints.add(new Constraint(val.leftProper(), val.rightVariable(), EQUALITY));
                        } else if (val.isLeftVariable()) {
                            newConstraints.add(new Constraint(val.leftVariable(), val.rightVariable(), EQUALITY));
                        } else {
                            throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                        }
                    } else {
                        throw new IllegalStateException("Reduce method is flawed! " + val.toString());
                    }

                    return newConstraints;
                }

                // If S is a wildcard, the constraint reduces to false. TODO
            }

            // If T is a wildcard of the form ?, the constraint reduces to true. TODO

            // If T is a wildcard of the form ? extends T': TODO


            // If T is a wildcard of the form ? super T': TODO

            throw new IllegalStateException("Reduce method is flawed! " + val.toString());
        }
    };


    public List<BoundOrConstraint> reduce(BoundOrConstraint constraint) {
        return null;
    }
}

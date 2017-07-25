/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typeinference;

import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.EQUALITY;
import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.SUBTYPE;

import java.util.ArrayList;
import java.util.List;


public final class TypeInferenceResolver {

    private TypeInferenceResolver() {

    }

    /**
     * https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html#jls-18.3
     */
    public static List<Constraint> incorporateBounds(List<Bound> currentBounds, List<Bound> newBounds) {
        // (In this section, S and T are inference variables or types, and U is a proper type. For conciseness, a bound
        // of the form α = T may also match a bound of the form T = α.)

        List<Constraint> newConstraints = new ArrayList<>();

        for (Bound first : currentBounds) {
            for (Bound second : newBounds) {
                Sides sides = getUnequalSides(first, second);
                if (sides == null) {
                    continue;
                }

                if (first.ruleType() == EQUALITY && second.ruleType() == EQUALITY) {
                    // α = S and α = T imply ‹S = T›
                    newConstraints.add(copyConstraint(first, second, getUnequalSides(first, second), EQUALITY));
                } else if (first.ruleType() == EQUALITY && second.ruleType() == SUBTYPE) {
                    if (sides.second == Side.RIGHT) {
                        // α = S and α <: T imply ‹S <: T›
                        newConstraints.add(copyConstraint(first, second, sides, SUBTYPE));
                    } else {
                        // α = S and T <: α imply ‹T <: S›
                        newConstraints.add(copyConstraint(second, first, sides.copySwap(), SUBTYPE));
                    }

                } else if (first.ruleType() == SUBTYPE && second.ruleType() == EQUALITY) {
                    if (sides.first == Side.RIGHT) {
                        // α <: T and α = S imply ‹S <: T›
                        newConstraints.add(copyConstraint(second, first, sides.copySwap(), SUBTYPE));
                    } else {
                        // T <: α and α = S imply ‹T <: S›
                        newConstraints.add(copyConstraint(first, second, sides, SUBTYPE));
                    }

                } else if (first.ruleType() == SUBTYPE && second.ruleType() == SUBTYPE) {
                    if (sides.first == Side.LEFT && sides.second == Side.RIGHT) {
                        // S <: α and α <: T imply ‹S <: T›
                        newConstraints.add(copyConstraint(first, second, sides, SUBTYPE));
                    } else if (sides.first == Side.RIGHT && sides.second == Side.LEFT) {
                        // α <: T and S <: α imply ‹S <: T›
                        newConstraints.add(copyConstraint(second, first, sides.copySwap(), SUBTYPE));
                    }
                }


                // α = U and S = T imply ‹S[α:=U] = T[α:=U]› TODO

                // α = U and S <: T imply ‹S[α:=U] <: T[α:=U]› TODO
            }
        }

        return newConstraints;
    }

    private enum Side {
        LEFT, RIGHT
    }

    private static class Sides {
        /* default */ final Side first;
        /* default */ final Side second;

        /* default */ Sides(Side first, Side second) {
            this.first = first;
            this.second = second;
        }

        /* default */ Sides copySwap() {
            return new Sides(second, first);
        }
    }

    private static Sides getUnequalSides(BoundOrConstraint first, BoundOrConstraint second) {
        if (first.leftVariable() != null) {
            if (first.leftVariable() == second.leftVariable()) {
                return new Sides(Side.RIGHT, Side.RIGHT);
            } else if (first.leftVariable() == second.rightVariable()) {
                return new Sides(Side.RIGHT, Side.LEFT);
            }
        } else if (first.rightVariable() != null) {
            if (first.rightVariable() == second.leftVariable()) {
                return new Sides(Side.LEFT, Side.RIGHT);
            } else if (first.rightVariable() == second.rightVariable()) {
                return new Sides(Side.LEFT, Side.LEFT);
            }
        }

        return null;
    }

    private static Constraint copyConstraint(BoundOrConstraint first, BoundOrConstraint second, Sides sides,
                                             InferenceRuleType rule) {
        if (sides.first == Side.LEFT) {
            if (sides.second == Side.LEFT) {
                if (first.leftVariable() != null) {
                    if (second.leftVariable() != null) {
                        return new Constraint(first.leftVariable(), second.leftVariable(), rule);
                    } else {
                        return new Constraint(first.leftVariable(), second.leftProper(), rule);
                    }
                } else {
                    if (second.leftVariable() != null) {
                        return new Constraint(first.leftProper(), second.leftVariable(), rule);
                    } else {
                        return new Constraint(first.leftProper(), second.leftProper(), rule);
                    }
                }
            } else {
                if (first.leftVariable() != null) {
                    if (second.rightVariable() != null) {
                        return new Constraint(first.leftVariable(), second.rightVariable(), rule);
                    } else {
                        return new Constraint(first.leftVariable(), second.rightProper(), rule);
                    }
                } else {
                    if (second.rightVariable() != null) {
                        return new Constraint(first.leftProper(), second.rightVariable(), rule);
                    } else {
                        return new Constraint(first.leftProper(), second.rightProper(), rule);
                    }
                }
            }
        } else {
            if (sides.second == Side.LEFT) {
                if (first.rightVariable() != null) {
                    if (second.leftVariable() != null) {
                        return new Constraint(first.rightVariable(), second.leftVariable(), rule);
                    } else {
                        return new Constraint(first.rightVariable(), second.leftProper(), rule);
                    }
                } else {
                    if (second.leftVariable() != null) {
                        return new Constraint(first.rightProper(), second.leftVariable(), rule);
                    } else {
                        return new Constraint(first.rightProper(), second.leftProper(), rule);
                    }
                }
            } else {
                if (first.rightVariable() != null) {
                    if (second.rightVariable() != null) {
                        return new Constraint(first.rightVariable(), second.rightVariable(), rule);
                    } else {
                        return new Constraint(first.rightVariable(), second.rightProper(), rule);
                    }
                } else {
                    if (second.rightVariable() != null) {
                        return new Constraint(first.rightProper(), second.rightVariable(), rule);
                    } else {
                        return new Constraint(first.rightProper(), second.rightProper(), rule);
                    }
                }
            }
        }
    }
}

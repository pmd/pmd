/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typeinference;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


@Deprecated
@InternalApi
public abstract class BoundOrConstraint {
    private final JavaTypeDefinition leftProperType;
    private final Variable leftTypeVariable;

    private final JavaTypeDefinition rightProperType;
    private final Variable rightTypeVariable;

    protected final InferenceRuleType ruleType;

    public BoundOrConstraint(JavaTypeDefinition leftProperType, JavaTypeDefinition rightProperType,
                             InferenceRuleType ruleType) {
        this.leftProperType = leftProperType;
        this.leftTypeVariable = null;
        this.rightProperType = rightProperType;
        this.rightTypeVariable = null;
        this.ruleType = ruleType;
    }

    public BoundOrConstraint(JavaTypeDefinition leftProperType, Variable rightTypeVariable,
                             InferenceRuleType ruleType) {
        this.leftProperType = leftProperType;
        this.leftTypeVariable = null;
        this.rightProperType = null;
        this.rightTypeVariable = rightTypeVariable;
        this.ruleType = ruleType;
    }

    public BoundOrConstraint(Variable leftTypeVariable, JavaTypeDefinition rightProperType,
                             InferenceRuleType ruleType) {
        this.leftProperType = null;
        this.leftTypeVariable = leftTypeVariable;
        this.rightProperType = rightProperType;
        this.rightTypeVariable = null;
        this.ruleType = ruleType;
    }

    public BoundOrConstraint(Variable leftTypeVariable, Variable rightTypeVariable,
                             InferenceRuleType ruleType) {
        this.leftProperType = null;
        this.leftTypeVariable = leftTypeVariable;
        this.rightProperType = null;
        this.rightTypeVariable = rightTypeVariable;
        this.ruleType = ruleType;
    }

    /* Proper type like Number or Object */
    public boolean isLeftProper() {
        return leftProperType != null && !leftProperType.isNullType() && !leftProperType.isPrimitive()
                && !leftProperType.isArrayType() && !leftProperType.isGeneric();
    }

    public boolean isLeftVariable() {
        return leftTypeVariable != null;
    }

    public boolean isLeftNull() {
        return leftProperType != null && leftProperType.isNullType();
    }

    public boolean isLeftType() {
        return leftProperType != null || leftTypeVariable != null; // wildcard is not a type
    }

    public boolean isLeftPrimitive() {
        return leftProperType != null && leftProperType.isPrimitive();
    }

    public boolean isLeftClassOrInterface() {
        return leftProperType != null && leftProperType.isClassOrInterface();
    }

    public boolean isLeftArray() {
        return leftProperType != null && leftProperType.isArrayType();
    }

    public boolean isRightProper() {
        return rightProperType != null && !rightProperType.isNullType() && !rightProperType.isPrimitive()
                && !rightProperType.isArrayType() && !rightProperType.isGeneric();
    }

    public boolean isRightVariable() {
        return rightTypeVariable != null;
    }

    public boolean isRightNull() {
        return rightProperType != null && rightProperType.isNullType();
    }

    public boolean isRightType() {
        return rightProperType != null || rightTypeVariable != null; // wildcard is not a type
    }

    public boolean isRightPrimitive() {
        return rightProperType != null && rightProperType.isPrimitive();
    }

    public boolean isRightClassOrInterface() {
        return rightProperType != null && rightProperType.isClassOrInterface();
    }

    public boolean isRightArray() {
        return rightProperType != null && rightProperType.isArrayType();
    }

    public JavaTypeDefinition leftProper() {
        return leftProperType;
    }

    public Variable leftVariable() {
        return leftTypeVariable;
    }

    public JavaTypeDefinition rightProper() {
        return rightProperType;
    }

    public Variable rightVariable() {
        return rightTypeVariable;
    }

    public InferenceRuleType ruleType() {
        return ruleType;
    }

    public abstract List<BoundOrConstraint> reduce();

    public void addVariablesToSet(Set<Variable> variables) {
        if (leftTypeVariable != null) {
            variables.add(leftTypeVariable);
        }

        if (rightTypeVariable != null) {
            variables.add(rightTypeVariable);
        }
    }

    /**
     * @return true, if the left-hand side mentions variables
     */
    public boolean leftHasMentionedVariable() {
        return leftTypeVariable != null;
    }

    /**
     * @return true, if the right-hand side mentions variales
     */
    public boolean rightHasMentionedVariable() {
        return rightTypeVariable != null;
    }

    public Variable getLeftMentionedVariable() {
        return leftTypeVariable;
    }

    public Variable getRightMentionedVariable() {
        return rightTypeVariable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!this.getClass().isInstance(obj)) {
            return false;
        }

        BoundOrConstraint other = (BoundOrConstraint) obj;

        if (leftProperType != null && other.leftProperType != null) {
            if (!leftProperType.equals(other.leftProperType)) {
                return false;
            }
        } else if (leftTypeVariable != null && other.leftTypeVariable != null) {
            if (!leftTypeVariable.equals(other.leftTypeVariable)) {
                return false;
            }
        } else {
            return false;
        }

        if (rightProperType != null && other.rightProperType != null) {
            if (!rightProperType.equals(other.rightProperType)) {
                return false;
            }
        } else if (rightTypeVariable != null && other.rightTypeVariable != null) {
            if (!rightTypeVariable.equals(other.rightTypeVariable)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 31;

        if (leftProperType != null) {
            result *= leftProperType.hashCode();
        } else {
            result *= leftTypeVariable.hashCode();
        }

        if (rightProperType != null) {
            result *= rightProperType.hashCode();
        } else {
            result *= rightTypeVariable.hashCode();
        }

        return result;
    }
}

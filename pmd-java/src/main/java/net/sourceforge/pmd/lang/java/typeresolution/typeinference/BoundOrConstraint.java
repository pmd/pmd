/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typeinference;

import java.util.List;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


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

    public InferenceRuleType getRuleType() {
        return ruleType;
    }

    public abstract List<BoundOrConstraint> reduce();
}

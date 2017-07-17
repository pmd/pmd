package net.sourceforge.pmd.lang.java.typeresolution.typeinterference;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

import java.util.List;

public abstract class BoundOrConstraint {
    private final JavaTypeDefinition leftProperType;
    private final BoundOrConstraint leftTypeVariable;

    private final JavaTypeDefinition rightProperType;
    private final BoundOrConstraint rightTypeVariable;

    protected final IntferenceRuleType ruleType;

    public BoundOrConstraint(JavaTypeDefinition leftProperType, JavaTypeDefinition rightProperType,
                             IntferenceRuleType ruleType) {
        this.leftProperType = leftProperType;
        this.leftTypeVariable = null;
        this.rightProperType = rightProperType;
        this.rightTypeVariable = null;
        this.ruleType = ruleType;
    }

    public BoundOrConstraint(JavaTypeDefinition leftProperType, BoundOrConstraint rightTypeVariable,
                             IntferenceRuleType ruleType) {
        this.leftProperType = leftProperType;
        this.leftTypeVariable = null;
        this.rightProperType = null;
        this.rightTypeVariable = rightTypeVariable;
        this.ruleType = ruleType;
    }

    public BoundOrConstraint(BoundOrConstraint leftTypeVariable, JavaTypeDefinition rightProperType,
                             IntferenceRuleType ruleType) {
        this.leftProperType = null;
        this.leftTypeVariable = leftTypeVariable;
        this.rightProperType = rightProperType;
        this.rightTypeVariable = null;
        this.ruleType = ruleType;
    }

    public BoundOrConstraint(BoundOrConstraint leftTypeVariable, BoundOrConstraint rightTypeVariable,
                             IntferenceRuleType ruleType) {
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
        return isLeftProper() || isLeftVariable(); // wildcard is not a type
    }

    public boolean isLeftPrimitive() {
        return leftProperType != null && leftProperType.isPrimitive();
    }

    public boolean isLeftClassOrInterface() {
        // TODO: is this valid?
        return !isLeftPrimitive() && !isLeftArray();
    }

    public boolean isLeftArray() {
        return leftProperType != null && leftProperType.isArrayType();
    }

    public boolean isRightProper() {
        return rightProperType != null && !rightProperType.isNullType();
    }

    public boolean isRightVariable() {
        return rightTypeVariable != null;
    }

    public boolean isRightNull() {
        return rightProperType != null && rightProperType.isNullType();
    }

    public boolean isRightType() {
        return isRightProper() || isRightVariable(); // wildcard is not a type
    }

    public boolean isRightPrimitive() {
        return rightProperType != null && rightProperType.isPrimitive();
    }

    public boolean isRightClassOrInterface() {
        // TODO: is this valid?
        return !isRightPrimitive() && !isRightArray();
    }

    public boolean isRightArray() {
        return rightProperType != null && rightProperType.isArrayType();
    }

    public JavaTypeDefinition leftProper() {
        return leftProperType;
    }

    public BoundOrConstraint leftVariable() {
        return leftTypeVariable;
    }

    public JavaTypeDefinition rightProper() {
        return rightProperType;
    }

    public BoundOrConstraint rightVariable() {
        return rightTypeVariable;
    }

    public IntferenceRuleType getRuleType() {
        return ruleType;
    }

    public abstract List<BoundOrConstraint> reduce();
}

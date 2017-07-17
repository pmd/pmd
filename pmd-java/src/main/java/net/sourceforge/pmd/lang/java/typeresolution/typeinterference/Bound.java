package net.sourceforge.pmd.lang.java.typeresolution.typeinterference;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

import java.util.List;

public class Bound extends BoundOrConstraint {
    public Bound(JavaTypeDefinition leftProperType, JavaTypeDefinition rightProperType, IntferenceRuleType ruleType) {
        super(leftProperType, rightProperType, ruleType);
    }

    public Bound(JavaTypeDefinition leftProperType, BoundOrConstraint rightTypeVariable, IntferenceRuleType
            ruleType) {
        super(leftProperType, rightTypeVariable, ruleType);
    }

    public Bound(BoundOrConstraint leftTypeVariable, JavaTypeDefinition rightProperType, IntferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightProperType, ruleType);
    }

    public Bound(BoundOrConstraint leftTypeVariable, BoundOrConstraint rightTypeVariable, IntferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightTypeVariable, ruleType);
    }

    @Override
    public List<BoundOrConstraint> reduce() {
        throw new IllegalStateException("Don't reduce bounds. " + toString());
    }
}

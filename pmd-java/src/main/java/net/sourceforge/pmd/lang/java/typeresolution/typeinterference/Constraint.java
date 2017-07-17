package net.sourceforge.pmd.lang.java.typeresolution.typeinterference;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

import java.util.List;

public class Constraint extends BoundOrConstraint {
    public Constraint(JavaTypeDefinition leftProperType, JavaTypeDefinition rightProperType, IntferenceRuleType
            ruleType) {
        super(leftProperType, rightProperType, ruleType);
    }

    public Constraint(JavaTypeDefinition leftProperType, BoundOrConstraint rightTypeVariable, IntferenceRuleType
            ruleType) {
        super(leftProperType, rightTypeVariable, ruleType);
    }

    public Constraint(BoundOrConstraint leftTypeVariable, JavaTypeDefinition rightProperType, IntferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightProperType, ruleType);
    }

    public Constraint(BoundOrConstraint leftTypeVariable, BoundOrConstraint rightTypeVariable, IntferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightTypeVariable, ruleType);
    }

    @Override
    public List<BoundOrConstraint> reduce() {
        return ruleType.reduce(this);
    }
}

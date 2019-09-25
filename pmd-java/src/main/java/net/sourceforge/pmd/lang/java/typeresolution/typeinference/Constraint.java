/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typeinference;


import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


@Deprecated
@InternalApi
public class Constraint extends BoundOrConstraint {
    public Constraint(JavaTypeDefinition leftProperType, JavaTypeDefinition rightProperType, InferenceRuleType
            ruleType) {
        super(leftProperType, rightProperType, ruleType);
    }

    public Constraint(JavaTypeDefinition leftProperType, Variable rightTypeVariable, InferenceRuleType
            ruleType) {
        super(leftProperType, rightTypeVariable, ruleType);
    }

    public Constraint(Variable leftTypeVariable, JavaTypeDefinition rightProperType, InferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightProperType, ruleType);
    }

    public Constraint(Variable leftTypeVariable, Variable rightTypeVariable, InferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightTypeVariable, ruleType);
    }

    @Override
    public List<BoundOrConstraint> reduce() {
        return ruleType.reduce(this);
    }
}

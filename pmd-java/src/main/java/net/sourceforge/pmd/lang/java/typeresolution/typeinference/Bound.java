/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typeinference;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


@Deprecated
@InternalApi
public class Bound extends BoundOrConstraint {
    public Bound(JavaTypeDefinition leftProperType, JavaTypeDefinition rightProperType, InferenceRuleType ruleType) {
        super(leftProperType, rightProperType, ruleType);
    }

    public Bound(JavaTypeDefinition leftProperType, Variable rightTypeVariable, InferenceRuleType
            ruleType) {
        super(leftProperType, rightTypeVariable, ruleType);
    }

    public Bound(Variable leftTypeVariable, JavaTypeDefinition rightProperType, InferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightProperType, ruleType);
    }

    public Bound(Variable leftTypeVariable, Variable rightTypeVariable, InferenceRuleType
            ruleType) {
        super(leftTypeVariable, rightTypeVariable, ruleType);
    }

    @Override
    public List<BoundOrConstraint> reduce() {
        throw new IllegalStateException("Don't reduce bounds. " + toString());
    }


}

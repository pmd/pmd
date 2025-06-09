/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Rule that detects boolean parameters in public and global Apex methods.
 * 
 * <p>
 * Boolean parameters can make method calls difficult to understand and
 * maintain. They often indicate that a method is doing more than one thing and
 * could benefit from being split into separate methods with more descriptive
 * names.
 * </p>
 * 
 * <p>
 * This rule flags any boolean parameters found in public or global methods,
 * encouraging developers to use more expressive alternatives such as enums,
 * separate methods, or configuration objects.
 * </p>
 * 
 * @see https://github.com/pmd/pmd/issues/5427
 */
public class AvoidBooleanMethodParametersRule extends AbstractApexRule {

    private static final String BOOLEAN_TYPE = "boolean";

    /**
     * Visits an Apex method node and checks for boolean global/public
     * parameters.
     * 
     * @param theMethod
     *            the method node being visited
     * @param data
     *            the rule context data
     * @return the rule context data
     */
    @Override
    public Object visit(ASTMethod theMethod, Object data) {
        if (!isPublicOrGlobal(theMethod)) {
            return data;
        }
        theMethod.descendants(ASTParameter.class).filter(parameter -> isBoolean(parameter))
                .forEach(parameter -> asCtx(data).addViolation(parameter));
        return data;
    }

    /**
     * Builds the target selector for this rule.
     * 
     * <p>
     * This rule targets Apex method nodes ({@link ASTMethod}) since it needs to
     * analyze method parameters for boolean types.
     * </p>
     * 
     * @return a rule target selector configured for ASTMethod nodes
     */
    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethod.class);
    }


    private boolean isPublicOrGlobal(ASTMethod method) {
        ASTModifierNode modifier = method.getModifiers();
        return modifier.isPublic() || modifier.isGlobal();
    }

    private boolean isBoolean(ASTParameter parameter) {
        return BOOLEAN_TYPE.equalsIgnoreCase(parameter.getType());
    }
}

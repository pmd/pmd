/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;


public class UnusedFormalParameterRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> CHECKALL_DESCRIPTOR = booleanProperty("checkAll").desc("Check all methods, including non-private ones").defaultValue(false).build();

    public UnusedFormalParameterRule() {
        super(ASTConstructorDeclaration.class, ASTMethodDeclaration.class);
        definePropertyDescriptor(CHECKALL_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (node.getVisibility() != Visibility.V_PRIVATE && !getProperty(CHECKALL_DESCRIPTOR)) {
            return null;
        }
        check(node, ctx);
        return null;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (node.getVisibility() != Visibility.V_PRIVATE && !getProperty(CHECKALL_DESCRIPTOR)) {
            return null;
        }
        if (node.getBody() != null
            && !node.hasModifiers(JModifier.DEFAULT)
            && !JavaRuleUtil.isSerializationReadObject(node)
            && !node.isOverride()) {
            check(node, ctx);
        }
        return null;
    }

    private void check(ASTExecutableDeclaration node, RuleContext ctx) {
        for (ASTFormalParameter formal : node.getFormalParameters()) {
            ASTVariableId varId = formal.getVarId();
            if (JavaAstUtils.isNeverUsed(varId) && !JavaRuleUtil.isExplicitUnusedVarName(varId.getName())) {
                ctx.addViolation(varId, node instanceof ASTMethodDeclaration ? "method" : "constructor", varId.getName());
            }
        }
    }

}

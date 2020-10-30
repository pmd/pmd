/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.RuleAstUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class UnusedFormalParameterRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Boolean> CHECKALL_DESCRIPTOR = booleanProperty("checkAll").desc("Check all methods, including non-private ones").defaultValue(false).build();

    public UnusedFormalParameterRule() {
        definePropertyDescriptor(CHECKALL_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        check(node, data);
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getVisibility() != Visibility.V_PRIVATE && !getProperty(CHECKALL_DESCRIPTOR)) {
            return data;
        }
        if (node.getBody() != null && !isSerializationMethod(node) && !node.isOverridden()) {
            check(node, data);
        }
        return data;
    }

    // TODO consider moving to RuleAstUtil, see also UnusedPrivateMethod and other rules that deal with serialization
    private boolean isSerializationMethod(ASTMethodDeclaration node) {
        return node.getVisibility() == Visibility.V_PRIVATE
            && "readObject".equals(node.getName())
            && RuleAstUtil.hasExceptionList(node, InvalidObjectException.class)
            && RuleAstUtil.hasParameters(node, ObjectInputStream.class);
    }

    private void check(ASTMethodOrConstructorDeclaration node, Object data) {
        if (!node.getEnclosingType().isInterface()) {
            for (ASTFormalParameter formal : node.getFormalParameters()) {
                ASTVariableDeclaratorId varId = formal.getVarId();
                if (UnusedLocalVariableRule.isNeverUsed(varId)) {
                    addViolation(data, varId, new Object[] {node instanceof ASTMethodDeclaration ? "method" : "constructor", varId.getName(),});
                }
            }
        }
    }

}

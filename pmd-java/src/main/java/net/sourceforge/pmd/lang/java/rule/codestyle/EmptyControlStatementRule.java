/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEmptyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;

public class EmptyControlStatementRule extends AbstractJavaRule {

    public EmptyControlStatementRule() {
        addRuleChainVisit(ASTFinallyStatement.class);
        addRuleChainVisit(ASTSynchronizedStatement.class);
        addRuleChainVisit(ASTTryStatement.class);
        addRuleChainVisit(ASTDoStatement.class);
        addRuleChainVisit(ASTBlock.class);
        addRuleChainVisit(ASTForStatement.class);
        addRuleChainVisit(ASTWhileStatement.class);
        addRuleChainVisit(ASTIfStatement.class);
        addRuleChainVisit(ASTSwitchStatement.class);
        addRuleChainVisit(ASTInitializer.class);
    }

    @Override
    public Object visit(JavaNode node, Object data) {
        throw new UnsupportedOperationException("should not be called");
    }

    @Override
    public Object visit(ASTFinallyStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            addViolation(data, node, "Empty finally clause");
        }
        return null;
    }

    @Override
    public Object visit(ASTSynchronizedStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            addViolation(data, node, "Empty synchronized statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        if (node.getNumChildren() == 1) {
            addViolation(data, node, "Empty switch statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        if (isEmpty(node) && node.getNthParent(3) instanceof ASTBlock) {
            addViolation(data, node, "Empty block");
        }
        return null;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        if (isEmpty(node.getThenBranch().getChild(0))) {
            addViolation(data, node, "Empty if statement");
        }
        if (isEmpty(node.getElseBranch().getChild(0))) {
            addViolation(data, node, "Empty else statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            addViolation(data, node, "Empty while statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            addViolation(data, node, "Empty for statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            addViolation(data, node, "Empty do..while statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTInitializer node, Object data) {
        if (isEmpty(node.getBody())) {
            addViolation(data, node, "Empty initializer statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            // all resources must be explicitly ignored
            boolean allResourcesIgnored = true;
            boolean hasResource = false;
            ASTResourceSpecification resources = node.getFirstChildOfType(ASTResourceSpecification.class);
            if (resources != null) {
                for (ASTResource resource : resources.findDescendantsOfType(ASTResource.class)) {
                    hasResource = true;
                    String name = resource.getVariableDeclaratorId().getName();
                    if (!JavaRuleUtil.isExplicitUnusedVarName(name)) {
                        allResourcesIgnored = false;
                        break;
                    }
                }
            }

            if (hasResource && !allResourcesIgnored) {
                addViolation(data, node, "Empty try body - you could rename the resource to 'ignored'");
            } else if (!hasResource) {
                addViolation(data, node, "Empty try body");
            }
        }
        return null;
    }

    private boolean isEmpty(JavaNode node) {
        if (node instanceof ASTStatement) {
            node = node.getChild(0);
        }
        return node instanceof ASTBlock && node.getNumChildren() == 0
            || node instanceof ASTEmptyStatement;
    }
}

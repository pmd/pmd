/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEmptyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceList;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class EmptyControlStatementRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> ALLOW_COMMENTED_BLOCKS
            = PropertyFactory.booleanProperty("allowCommentedBlocks")
            .desc("Option for allowing empty but commented blocks. This is useful where a developer "
                    + "wants to have the code structure and explain why a condition does not require "
                    + "logic or to hold TODO comments for future work.")
            .defaultValue(Boolean.FALSE)
            .build();

    public EmptyControlStatementRule() {
        super(ASTFinallyClause.class, ASTSynchronizedStatement.class, ASTTryStatement.class, ASTDoStatement.class,
                ASTBlock.class, ASTForStatement.class, ASTForeachStatement.class, ASTWhileStatement.class,
                ASTIfStatement.class, ASTSwitchStatement.class, ASTInitializer.class);

        definePropertyDescriptor(ALLOW_COMMENTED_BLOCKS);
    }

    @Override
    public Object visit(ASTFinallyClause node, Object data) {
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty finally clause");
        }
        return null;
    }

    @Override
    public Object visit(ASTSynchronizedStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty synchronized statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        if (node.getNumChildren() == 1) {
            asCtx(data).addViolationWithMessage(node, "Empty switch statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        if (isEmpty(node) && node.getParent() instanceof ASTBlock) {
            asCtx(data).addViolationWithMessage(node, "Empty block");
        }
        return null;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        if (isEmpty(node.getThenBranch())) {
            asCtx(data).addViolationWithMessage(node, "Empty if statement");
        }
        if (node.hasElse() && isEmpty(node.getElseBranch())) {
            asCtx(data).addViolationWithMessage(node.getElseBranch(), "Empty else statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty while statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty for statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTForeachStatement node, Object data) {
        if (JavaRuleUtil.isExplicitUnusedVarName(node.getVarId().getName())) {
            // allow `for (ignored : iterable) {}`
            return null;
        }
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty foreach statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty do..while statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTInitializer node, Object data) {
        if (isEmpty(node.getBody())) {
            asCtx(data).addViolationWithMessage(node, "Empty initializer statement");
        }
        return null;
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        if (isEmpty(node.getBody())) {
            // all resources must be explicitly ignored
            boolean allResourcesIgnored = true;
            boolean hasResource = false;
            ASTResourceList resources = node.getResources();
            if (resources != null) {
                for (ASTResource resource : resources) {
                    hasResource = true;
                    String name = resource.getStableName();
                    if (!JavaRuleUtil.isExplicitUnusedVarName(name)) {
                        allResourcesIgnored = false;
                        break;
                    }
                }
            }

            if (hasResource && !allResourcesIgnored) {
                asCtx(data).addViolationWithMessage(node, "Empty try body - you could rename the resource to ''ignored''");
            } else if (!hasResource) {
                asCtx(data).addViolationWithMessage(node, "Empty try body");
            }
        }
        return null;
    }

    private boolean isEmpty(JavaNode node) {
        boolean allowCommentedBlocks = getProperty(ALLOW_COMMENTED_BLOCKS);

        return (node instanceof ASTBlock && node.getNumChildren() == 0 && !(((ASTBlock) node).containsComment() && allowCommentedBlocks))
            || node instanceof ASTEmptyStatement;
    }
}

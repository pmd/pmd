/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.Optional;
import java.util.WeakHashMap;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Finds Apex class that do not define sharing
 *
 * @author sergey.gorbaty
 */
public class ApexSharingViolationsRule extends AbstractApexRule {

    /**
     * Keep track of previously reported violations to avoid duplicates.
     */
    private WeakHashMap<ApexNode<?>, Object> localCacheOfReportedNodes = new WeakHashMap<>();

    public ApexSharingViolationsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
        addRuleChainVisit(ASTDmlDeleteStatement.class);
        addRuleChainVisit(ASTDmlInsertStatement.class);
        addRuleChainVisit(ASTDmlMergeStatement.class);
        addRuleChainVisit(ASTDmlUndeleteStatement.class);
        addRuleChainVisit(ASTDmlUpdateStatement.class);
        addRuleChainVisit(ASTDmlUpsertStatement.class);
        addRuleChainVisit(ASTMethodCallExpression.class);
        addRuleChainVisit(ASTSoqlExpression.class);
        addRuleChainVisit(ASTSoslExpression.class);
    }

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);
        localCacheOfReportedNodes.clear();
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDmlUpsertStatement node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDmlUpdateStatement node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDmlUndeleteStatement node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDmlMergeStatement node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDmlInsertStatement node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDmlDeleteStatement node, Object data) {
        checkForViolation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        if (Helper.isAnyDatabaseMethodCall(node)) {
            checkForViolation(node, data);
        }

        return data;
    }

    private void checkForViolation(ApexNode<?> node, Object data) {
        // The closest ASTUserClass class in the tree hierarchy is the node that requires the sharing declaration
        ASTUserClass sharingDeclarationClass = node.getFirstParentOfType(ASTUserClass.class);

        // This is null in the case of triggers
        if (sharingDeclarationClass != null) {
            // Apex allows a single level of class nesting. Check to see if sharingDeclarationClass has an outer class
            ASTUserClass outerClass = sharingDeclarationClass.getFirstParentOfType(ASTUserClass.class);
            // The test annotation needs to be on the outermost class
            ASTUserClass testAnnotationClass = Optional.ofNullable(outerClass).orElse(sharingDeclarationClass);

            if (!Helper.isTestMethodOrClass(testAnnotationClass) && !Helper.isSystemLevelClass(sharingDeclarationClass) && !isSharingPresent(sharingDeclarationClass)) {
                // The violation is reported on the class, not the node that performs data access
                reportViolation(sharingDeclarationClass, data);
            }
        }
    }

    private void reportViolation(ApexNode<?> node, Object data) {
        ASTModifierNode modifier = node.getFirstChildOfType(ASTModifierNode.class);
        if (modifier != null) {
            if (localCacheOfReportedNodes.put(modifier, data) == null) {
                addViolation(data, modifier);
            }
        } else {
            if (localCacheOfReportedNodes.put(node, data) == null) {
                addViolation(data, node);
            }
        }
    }

    /**
     * Does class have sharing keyword declared?
     *
     * @param node
     * @return
     */
    private boolean isSharingPresent(ASTUserClass node) {
        return node.getModifiers().isWithoutSharing() || node.getModifiers().isWithSharing()
                || node.getModifiers().isInheritedSharing();
    }

}

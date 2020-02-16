/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.List;
import java.util.WeakHashMap;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
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

    private WeakHashMap<ApexNode<?>, Object> localCacheOfReportedNodes = new WeakHashMap<>();

    public ApexSharingViolationsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        if (!Helper.isTestMethodOrClass(node)) {
            boolean sharingFound = isSharingPresent(node);
            checkForSharingDeclaration(node, data, sharingFound);
            checkForDatabaseMethods(node, data, sharingFound);
        }

        localCacheOfReportedNodes.clear();
        return data;
    }

    /**
     * Check if class contains any Database.query / Database.insert [ Database.*
     * ] methods
     *
     * @param node
     * @param data
     */
    private void checkForDatabaseMethods(ASTUserClass node, Object data, boolean sharingFound) {
        List<ASTMethodCallExpression> calls = node.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression call : calls) {
            if (Helper.isMethodName(call, "Database", Helper.ANY_METHOD)) {
                if (!sharingFound) {
                    reportViolation(node, data);
                }
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
     * Check if class has no sharing declared
     *
     * @param node
     * @param data
     */
    private void checkForSharingDeclaration(ApexNode<?> node, Object data, boolean sharingFound) {
        final boolean foundAnyDMLorSOQL = Helper.foundAnyDML(node) || Helper.foundAnySOQLorSOSL(node);
        if (!sharingFound && !Helper.isTestMethodOrClass(node) && foundAnyDMLorSOQL) {
            reportViolation(node, data);
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

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Apex supported non existent annotations for legacy reasons.
 * In the future, use of such non-existent annotations could result in broken apex code that will not compile.
 * This will prevent users of garbage annotations from being able to use legitimate annotations added to apex in the future.
 * A full list of supported annotations can be found at https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm
 *
 * @author a.subramanian
 */
public class AvoidNonExistentAnnotationsRule extends AbstractApexRule {

    public AvoidNonExistentAnnotationsRule() {
        addRuleChainVisit(ASTAnnotation.class);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        if (!node.isResolved() && notSyntheticPropertyMethod(node) && notSyntheticFieldNode(node)) {
            addViolationWithMessage(data, node, "Use of non existent annotations will lead to broken Apex code which will not compile in the future.");
        }
        return data;
    }

    private boolean notSyntheticPropertyMethod(ASTAnnotation annotation) {
        Node node = annotation.getNthParent(2);
        return !(node instanceof ASTMethod) || !StringUtils.startsWith(node.getImage(), "__sfdc_");
    }

    private boolean notSyntheticFieldNode(ASTAnnotation annotation) {
        return !(annotation.getNthParent(2) instanceof ASTFieldDeclarationStatements);
    }
}

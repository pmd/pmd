/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class FieldDeclarationsShouldBeAtStartRule extends AbstractApexRule {
    private static final Comparator<ApexNode<?>> nodeBySourceLocationComparator =
        Comparator
            .<ApexNode<?>>comparingInt(ApexNode::getBeginLine)
            .thenComparing(ApexNode::getBeginColumn);

    @Override
    public Object visit(ASTUserClass node, Object data) {
        // Unfortunately the parser re-orders the AST to put field declarations before method declarations
        // so we have to rely on line numbers / positions to work out where the first method starts so we
        // can check if the fields are in acceptable places.
        List<ASTField> fields = node.findChildrenOfType(ASTField.class);
        List<ASTMethod> methods = node.findChildrenOfType(ASTMethod.class);

        Optional<ASTMethod> firstMethod = methods.stream()
            .filter(ApexNode::hasRealLoc)
            .min(nodeBySourceLocationComparator);

        if (!firstMethod.isPresent()) {
            // there are no methods so the field declaration has to come first
            return super.visit(node, data);
        }

        for (ASTField field : fields) {
            if (nodeBySourceLocationComparator.compare(field, firstMethod.get()) > 0) {
                addViolation(data, field, field.getName());
            }
        }

        return super.visit(node, data);
    }
}

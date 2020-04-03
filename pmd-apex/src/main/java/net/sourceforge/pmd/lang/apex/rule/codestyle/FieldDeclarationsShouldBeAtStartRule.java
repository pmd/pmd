/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
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
        // so we have to rely on line numbers / positions to work out where the first non-field declaration starts
        // so we can check if the fields are in acceptable places.
        List<ASTField> fields = node.findChildrenOfType(ASTField.class);

        List<ApexNode<?>> nonFieldDeclarations = new ArrayList<>();

        nonFieldDeclarations.addAll(getMethodNodes(node));
        nonFieldDeclarations.addAll(node.findChildrenOfType(ASTUserClass.class));
        nonFieldDeclarations.addAll(node.findChildrenOfType(ASTProperty.class));
        nonFieldDeclarations.addAll(node.findChildrenOfType(ASTBlockStatement.class));

        Optional<ApexNode<?>> firstNonFieldDeclaration = nonFieldDeclarations.stream()
            .filter(ApexNode::hasRealLoc)
            .min(nodeBySourceLocationComparator);

        if (!firstNonFieldDeclaration.isPresent()) {
            // there is nothing except field declaration, so that has to come first
            return super.visit(node, data);
        }

        for (ASTField field : fields) {
            if (nodeBySourceLocationComparator.compare(field, firstNonFieldDeclaration.get()) > 0) {
                addViolation(data, field, field.getName());
            }
        }

        return super.visit(node, data);
    }

    private List<ApexNode<?>> getMethodNodes(ASTUserClass node) {
        // The method <clinit> represents static initializer blocks, of which there can be many. Given that the
        // <clinit> method doesn't contain location information, only the containing ASTBlockStatements, we fetch
        // them for that method only.
        return node.findChildrenOfType(ASTMethod.class).stream()
            .flatMap(method -> method.getImage().equals("<clinit>") ?
                method.findChildrenOfType(ASTBlockStatement.class).stream() : Stream.of(method))
            .collect(Collectors.toList());
    }
}

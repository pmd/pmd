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
    private static final Comparator<ApexNode<?>> NODE_BY_SOURCE_LOCATION_COMPARATOR =
        Comparator
            .<ApexNode<?>>comparingInt(ApexNode::getBeginLine)
            .thenComparing(ApexNode::getBeginColumn);
    public static final String STATIC_INITIALIZER_METHOD_NAME = "<clinit>";

    public FieldDeclarationsShouldBeAtStartRule() {
        addRuleChainVisit(ASTUserClass.class);
    }

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
            .min(NODE_BY_SOURCE_LOCATION_COMPARATOR);

        if (!firstNonFieldDeclaration.isPresent()) {
            // there is nothing except field declaration, so that has to come first
            return data;
        }

        for (ASTField field : fields) {
            if (NODE_BY_SOURCE_LOCATION_COMPARATOR.compare(field, firstNonFieldDeclaration.get()) > 0) {
                addViolation(data, field, field.getName());
            }
        }

        return data;
    }

    private List<ApexNode<?>> getMethodNodes(ASTUserClass node) {
        // The method <clinit> represents static initializer blocks, of which there can be many. The
        // <clinit> method doesn't contain location information, however the containing ASTBlockStatements do,
        // so we fetch them for that method only.
        return node.findChildrenOfType(ASTMethod.class).stream()
            .flatMap(method -> method.getImage().equals(STATIC_INITIALIZER_METHOD_NAME)
                ? method.findChildrenOfType(ASTBlockStatement.class).stream() : Stream.of(method))
            .collect(Collectors.toList());
    }
}

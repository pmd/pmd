/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class FieldDeclarationsShouldBeAtStartRule extends AbstractApexRule {
    private static final Comparator<ApexNode<?>> NODE_BY_SOURCE_LOCATION_COMPARATOR =
        Comparator
            .<ApexNode<?>>comparingInt(ApexNode::getBeginLine)
            .thenComparing(ApexNode::getBeginColumn);

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        // Unfortunately the parser re-orders the AST to put field declarations before method declarations
        // so we have to rely on line numbers / positions to work out where the first non-field declaration starts
        // so we can check if the fields are in acceptable places.
        List<ASTFieldDeclaration> fields = node.children(ASTFieldDeclarationStatements.class)
                                               .children(ASTFieldDeclaration.class)
                                               .toList();

        List<ApexNode<?>> nonFieldDeclarations = new ArrayList<>();

        nonFieldDeclarations.addAll(getMethodNodes(node));
        nonFieldDeclarations.addAll(node.children(ASTUserClass.class).toList());
        nonFieldDeclarations.addAll(node.children(ASTProperty.class).toList());
        nonFieldDeclarations.addAll(node.children(ASTBlockStatement.class).toList());

        Optional<ApexNode<?>> firstNonFieldDeclaration = nonFieldDeclarations.stream()
            .filter(ApexNode::hasRealLoc)
            .min(NODE_BY_SOURCE_LOCATION_COMPARATOR);

        if (!firstNonFieldDeclaration.isPresent()) {
            // there is nothing except field declaration, so that has to come first
            return data;
        }

        for (ASTFieldDeclaration field : fields) {
            if (NODE_BY_SOURCE_LOCATION_COMPARATOR.compare(field, firstNonFieldDeclaration.get()) > 0) {
                asCtx(data).addViolation(field, field.getName());
            }
        }

        return data;
    }

    private List<? extends ApexNode<?>> getMethodNodes(ASTUserClass node) {
        return node.descendants(ASTMethod.class).map(m -> (ApexNode<?>) m).toList();
    }
}

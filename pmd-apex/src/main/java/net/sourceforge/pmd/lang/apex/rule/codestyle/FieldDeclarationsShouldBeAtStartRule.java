/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class FieldDeclarationsShouldBeAtStartRule extends AbstractApexRule {
    @Override
    public Object visit(ASTUserClass node, Object data) {
        // Unfortunately the parser re-orders the AST to put field declarations before method declarations
        // so we have to rely on line numbers / positions to work out where the first method starts so we
        // can check if the fields are in acceptable places.
        List<ASTFieldDeclaration> fields = node.findDescendantsOfType(ASTFieldDeclaration.class);
        List<ASTMethod> methods = node.findDescendantsOfType(ASTMethod.class);

        Optional<NodeAndLocation> firstMethod =
            methods.stream().map(NodeAndLocation::new)
                .min(Comparator.naturalOrder());

        if (!firstMethod.isPresent()) {
            // there are no methods so the field declaration has to come first
            return data;
        }

        for (ASTFieldDeclaration field : fields) {
            NodeAndLocation fieldPosition = new NodeAndLocation(field);
            if (fieldPosition.compareTo(firstMethod.get()) > 0) {
                addViolation(data, field);
            }
        }

        return data;
    }

    private static class NodeAndLocation implements Comparable<NodeAndLocation> {
        public int line;
        public int column;
        public ApexNode<?> node;

        public NodeAndLocation(ApexNode<?> node) {
            this.node = node;

            line = node.getBeginLine();
            column = node.getBeginColumn();
        }

        @Override
        public int compareTo(NodeAndLocation other) {
            int lineCompare = Integer.compare(line, other.line);
            if (lineCompare != 0) {
                return lineCompare;
            }

            return Integer.compare(column, other.column);
        }
    }
}

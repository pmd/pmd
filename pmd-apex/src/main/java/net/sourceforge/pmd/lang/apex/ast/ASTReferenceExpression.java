/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTReferenceExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTReferenceExpression(Node referenceExpression) {
        super(referenceExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /*
    public IdentifierContext getContext() {
        return node.getContext();
    }
     */
    // TODO(b/239648780)


    /*
    public ReferenceType getReferenceType() {
        return node.getReferenceType();
    }
     */
    // TODO(b/239648780)

    @Override
    public String getImage() {
        /*
        if (node.getNames() != null && !node.getNames().isEmpty()) {
            return node.getNames().get(0).getValue();
        }
         */
        // TODO(b/239648780)
        return null;
    }

    public List<String> getNames() {
        /*
        List<Identifier> identifiers = node.getNames();
        if (identifiers != null) {
            return identifiers.stream().map(id -> id.getValue()).collect(Collectors.toList());
        }
         */
        // TODO(b/239648780)
        return Collections.emptyList();
    }

    public boolean isSafeNav() {
        // return node.isSafeNav();
        // TODO(b/239648780)
        return false;
    }

    public boolean isSObjectType() {
        /*
        List<Identifier> identifiers = node.getNames();
        if (identifiers != null) {
            return identifiers.stream().anyMatch(id -> "sobjecttype".equalsIgnoreCase(id.getValue()));
        }
         */
        // TODO(b/239648780)
        return false;
    }
}

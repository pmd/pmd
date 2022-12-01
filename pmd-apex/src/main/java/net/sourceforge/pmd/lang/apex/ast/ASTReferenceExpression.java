/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.google.summit.ast.Identifier;

public class ASTReferenceExpression extends AbstractApexNode.Many<Identifier> {

    private final ReferenceType referenceType;
    private final boolean isSafe;

    ASTReferenceExpression(List<Identifier> identifiers, ReferenceType referenceType, boolean isSafe) {
        super(identifiers);
        this.referenceType = referenceType;
        this.isSafe = isSafe;
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
    // TODO(b/243906211)

    /* TODO(b/239648780) this is not yet set to a meaningful value
    public */ ReferenceType getReferenceType() {
        return referenceType;
    }

    @Override
    public String getImage() {
        if (!nodes.isEmpty()) {
            return nodes.get(0).getString();
        }
        return "";
    }

    public List<String> getNames() {
        return nodes.stream().map(Identifier::getString).collect(Collectors.toList());
    }

    public boolean isSafeNav() {
        return this.isSafe;
    }

    public boolean isSObjectType() {
        return nodes.stream().anyMatch(id -> "sobjecttype".equalsIgnoreCase(id.getString()));
    }

    @Override
    public boolean hasRealLoc() {
        return !nodes.isEmpty();
    }
}

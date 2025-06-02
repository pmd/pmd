/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

import com.google.summit.ast.Identifier;

public final class ASTReferenceExpression extends AbstractApexNode.Many<Identifier> {

    private final ReferenceType referenceType;
    private final boolean isSafe;

    ASTReferenceExpression(List<Identifier> identifiers, ReferenceType referenceType, boolean isSafe) {
        super(identifiers);
        this.referenceType = referenceType;
        this.isSafe = isSafe;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    @Override
    public String getImage() {
        if (!nodes.isEmpty()) {
            return nodes.get(0).getString();
        }
        return "";
    }

    @NoAttribute
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

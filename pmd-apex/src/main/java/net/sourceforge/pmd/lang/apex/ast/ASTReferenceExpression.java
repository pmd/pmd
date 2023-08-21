/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.expression.IdentifierContext;
import apex.jorje.semantic.ast.expression.ReferenceExpression;
import apex.jorje.semantic.ast.expression.ReferenceType;


public final class ASTReferenceExpression extends AbstractApexNode<ReferenceExpression> {

    ASTReferenceExpression(ReferenceExpression referenceExpression) {
        super(referenceExpression);
    }



    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public IdentifierContext getContext() {
        return node.getContext();
    }


    public ReferenceType getReferenceType() {
        return node.getReferenceType();
    }

    @Override
    public String getImage() {
        if (node.getNames() != null && !node.getNames().isEmpty()) {
            return node.getNames().get(0).getValue();
        }
        return null;
    }

    public List<String> getNames() {
        List<Identifier> identifiers = node.getNames();
        if (identifiers != null) {
            return identifiers.stream().map(id -> id.getValue()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean isSafeNav() {
        return node.isSafeNav();
    }

    public boolean isSObjectType() {
        List<Identifier> identifiers = node.getNames();
        return identifiers != null
            && identifiers.stream().anyMatch(id -> "sobjecttype".equalsIgnoreCase(id.getValue()));
    }
}

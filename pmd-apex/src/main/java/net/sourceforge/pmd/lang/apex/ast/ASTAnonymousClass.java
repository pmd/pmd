/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.AnonymousClass;

public class ASTAnonymousClass extends ApexRootNode<AnonymousClass> {

    public ASTAnonymousClass(AnonymousClass anonymousClass) {
        super(anonymousClass);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getClass().getName();
    }
}

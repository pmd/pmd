/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.CompilationUnit;

public class ASTAnonymousClass extends ApexRootNode<CompilationUnit> {

    @Deprecated
    @InternalApi
    public ASTAnonymousClass(CompilationUnit anonymousClass) {
        super(anonymousClass);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getClass().getName();
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.RunAsBlockStatement;

public final class ASTRunAsBlockStatement extends AbstractApexNode<RunAsBlockStatement> {

    ASTRunAsBlockStatement(RunAsBlockStatement runAsBlockStatement) {
        super(runAsBlockStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}

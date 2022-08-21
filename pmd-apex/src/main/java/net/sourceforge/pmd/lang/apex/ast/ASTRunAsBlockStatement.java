/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.RunAsStatement;

public class ASTRunAsBlockStatement extends AbstractApexNode.Single<RunAsStatement> {

    @Deprecated
    @InternalApi
    public ASTRunAsBlockStatement(RunAsStatement runAsStatement) {
        super(runAsStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

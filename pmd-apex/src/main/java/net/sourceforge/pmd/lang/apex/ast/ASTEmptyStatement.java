/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.EmptyStatement;

public class ASTEmptyStatement extends AbstractApexNode<EmptyStatement> {
    public ASTEmptyStatement(EmptyStatement emptyStatement) {
        super(emptyStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

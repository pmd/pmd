/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.ForEachStatement;

public class ASTForEachStatement extends AbstractApexNode<ForEachStatement> {

    public ASTForEachStatement(ForEachStatement forEachStatement) {
        super(forEachStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

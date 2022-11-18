/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

public class ASTConstructorPreambleStatement extends AbstractApexNode.Single<Node> {

    ASTConstructorPreambleStatement(Node constructorPreambleStatement) {
        super(constructorPreambleStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

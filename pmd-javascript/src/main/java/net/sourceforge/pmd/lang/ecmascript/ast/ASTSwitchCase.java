/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.SwitchCase;

public class ASTSwitchCase extends AbstractEcmascriptNode<SwitchCase> {
    public ASTSwitchCase(SwitchCase switchCase) {
        super(switchCase);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isDefault() {
        return node.isDefault();
    }

    public EcmascriptNode<?> getExpression() {
        if (!isDefault()) {
            return (EcmascriptNode<?>) getChild(0);
        } else {
            return null;
        }
    }

    public int getNumStatements() {
        // TODO Tell Rhino folks about null Statements, should be empty List?
        return node.getStatements() != null ? node.getStatements().size() : 0;
    }

    public EcmascriptNode<?> getStatement(int index) {
        int statementIndex = index;
        if (!isDefault()) {
            statementIndex++;
        }
        return (EcmascriptNode<?>) getChild(statementIndex);
    }
}

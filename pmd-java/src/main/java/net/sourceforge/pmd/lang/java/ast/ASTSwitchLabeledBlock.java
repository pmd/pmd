/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public class ASTSwitchLabeledBlock extends AbstractJavaNode implements ASTSwitchLabeledRule {

    ASTSwitchLabeledBlock(int id) {
        super(id);
    }

    ASTSwitchLabeledBlock(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

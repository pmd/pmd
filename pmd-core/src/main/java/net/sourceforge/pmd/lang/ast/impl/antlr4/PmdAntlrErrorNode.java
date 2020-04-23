/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public abstract class PmdAntlrErrorNode extends PmdAntlrTerminalNode implements AntlrNode, ErrorNode {

    public PmdAntlrErrorNode(Token t, AntlrNameDictionary dico) {
        super(t, dico);
    }

    @Override
    public String getXPathNodeName() {
        return "Error";
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitErrorNode(this);
    }
}

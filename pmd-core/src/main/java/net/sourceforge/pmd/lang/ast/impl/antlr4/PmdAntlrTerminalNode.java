/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * @author Clément Fournier
 */
public abstract class PmdAntlrTerminalNode extends TerminalNodeImpl implements AntlrNode {

    private final DataMap<DataKey<?, ?>> userData = DataMap.newDataMap();
    private int idxInParent = -1;
    private final AntlrNameDictionary dico;

    public PmdAntlrTerminalNode(Token t, AntlrNameDictionary dico) {
        super(t);
        this.dico = dico;
    }

    @Override
    public AntlrNode getChild(int i) {
        return null;
    }

    @Override
    public int getNumChildren() {
        return 0;
    }

    @Override
    public String getXPathNodeName() {
        int type = getSymbol().getType();
        return dico.getXPathName(type);
    }

    @Override
    public AntlrNode getParent() {
        return (AntlrNode) super.getParent();
    }


    @Override
    public void setParent(RuleContext parent) {
        assert parent instanceof AntlrNode : "Parent should be an antlr node";
        super.setParent(parent);
    }

    void setIndexInParent(int idxInParent) {
        this.idxInParent = idxInParent;
    }

    @Override
    public int getIndexInParent() {
        return idxInParent;
    }

    @Override
    public int getBeginLine() {
        return getSymbol().getLine();
    }

    @Override
    public int getBeginColumn() {
        return AntlrUtils.getBeginColumn(getSymbol());
    }

    @Override
    public int getEndLine() {
        // FIXME this is not the end line if the stop token spans several lines
        return getBeginLine();
    }

    @Override
    public int getEndColumn() {
        return AntlrUtils.getEndColumn(getSymbol());
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        return userData;
    }
}

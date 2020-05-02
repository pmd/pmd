/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;

public abstract class AntlrBaseNode extends ParserRuleContext implements AntlrNode {

    private final DataMap<DataKey<?, ?>> userData = DataMap.newDataMap();

    /**
     * Constructor required by {@link ParserRuleContext}
     */
    @SuppressWarnings("unused")
    public AntlrBaseNode() {
        // Nothing to be done
    }

    /**
     * Constructor required by {@link ParserRuleContext}
     *
     * @param parent The parent
     * @param invokingStateNumber the invokingState defined by {@link org.antlr.v4.runtime.RuleContext} parent
     */
    @SuppressWarnings("unused")
    public AntlrBaseNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    /**
     * TODO @NoAttribute (port swift rules)
     */
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public String getText() {
        return super.getText();
    }


    // FIXME these coordinates are not accurate

    @Override
    public int getBeginLine() {
        return start.getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        return stop.getLine(); // This goes from 1 to n
    }

    @Override
    public int getBeginColumn() {
        return start.getCharPositionInLine(); // This goes from 0 to (n - 1)
    }

    @Override
    public int getEndColumn() {
        return stop.getCharPositionInLine(); // This goes from 0 to (n - 1)
    }

    @Override
    public DataMap<DataKey<?, ?>> getUserMap() {
        return userData;
    }

    @Override
    public AntlrNode getChild(int i) {
        return (AntlrNode) super.getChild(i);
    }

    @Override
    public AntlrBaseNode getParent() {
        return (AntlrBaseNode) super.getParent();
    }

    @Override
    public int getNumChildren() {
        return getChildCount();
    }

    @Override
    public String getXPathNodeName() {
        final String simpleName = getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - "Context".length());
    }
}

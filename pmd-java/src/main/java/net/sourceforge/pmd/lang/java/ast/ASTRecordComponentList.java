/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.ASTList.ASTMaybeEmptyListOf;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * Defines the state description of a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 *
 * <pre class="grammar">
 *
 * RecordComponentList ::= "(" ( {@linkplain ASTRecordComponent RecordComponent} ( "," {@linkplain ASTRecordComponent RecordComponent} )* )? ")"
 *
 * </pre>
 */
@Experimental
public final class ASTRecordComponentList extends ASTMaybeEmptyListOf<ASTRecordComponent> implements SymbolDeclaratorNode {

    private JConstructorSymbol symbol;


    ASTRecordComponentList(int id) {
        super(id, ASTRecordComponent.class);
    }

    /**
     * Returns true if the last component is varargs.
     */
    public boolean isVarargs() {
        ASTRecordComponent lastChild = getLastChild();
        return lastChild != null && lastChild.isVarargs();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * This returns the symbol for the canonical constructor of the
     * record. There may be a compact record constructor declaration,
     * in which case they share the same symbol.
     */
    @Override
    public JConstructorSymbol getSymbol() {
        // TODO deduplicate the symbol in case the canonical constructor
        //  is explicitly declared somewhere. Needs a notion of override-equivalence,
        //  to be provided by future PRs for type resolution
        assert symbol != null : "No symbol set for components of " + getParent();
        return symbol;
    }

    void setSymbol(JConstructorSymbol symbol) {
        AbstractTypedSymbolDeclarator.assertSymbolNull(this.symbol, this);
        this.symbol = symbol;
    }
}

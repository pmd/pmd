/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;


public class ASTFormalParameters extends AbstractJavaNode implements Iterable<ASTFormalParameter> {

    ASTFormalParameters(int id) {
        super(id);
    }

    public int size() {
        final List<ASTFormalParameter> parameters = findChildrenOfType(ASTFormalParameter.class);
        return !parameters.isEmpty() && parameters.get(0).isExplicitReceiverParameter()
               ? parameters.size() - 1 : parameters.size();
    }

    /**
     * @deprecated for removal. Use {@link #size()} instead.
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@Size")
    public int getParameterCount() {
        return size();
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public Iterator<ASTFormalParameter> iterator() {
        return children(ASTFormalParameter.class).iterator();
    }
}

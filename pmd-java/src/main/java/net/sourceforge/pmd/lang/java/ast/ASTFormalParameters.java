/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;


public class ASTFormalParameters extends AbstractJavaNode implements Iterable<ASTFormalParameter> {

    @InternalApi
    @Deprecated
    public ASTFormalParameters(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTFormalParameters(JavaParser p, int id) {
        super(p, id);
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
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public Iterator<ASTFormalParameter> iterator() {
        return new NodeChildrenIterator<>(this, ASTFormalParameter.class);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;


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

    public int getParameterCount() {
        final List<ASTFormalParameter> parameters = findChildrenOfType(ASTFormalParameter.class);
        return !parameters.isEmpty() && parameters.get(0).isExplicitReceiverParameter()
               ? parameters.size() - 1 : parameters.size();
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

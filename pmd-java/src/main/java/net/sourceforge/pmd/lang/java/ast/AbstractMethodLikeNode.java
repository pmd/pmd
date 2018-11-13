/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;


public abstract class AbstractMethodLikeNode extends AbstractJavaAccessNode implements MethodLikeNode {
    private JavaOperationQualifiedName qualifiedName;


    AbstractMethodLikeNode(int i) {
        super(i);
    }


    AbstractMethodLikeNode(JavaParser parser, int i) {
        super(parser, i);
    }


    public void setQualifiedName(JavaOperationQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }


    @Override
    public JavaOperationQualifiedName getQualifiedName() {
        return qualifiedName;
    }

}

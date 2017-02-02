/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 * 
 * @see AbstractJavaNode
 * @see TypeNode
 */
public abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

    private Class<?> type;

    public AbstractJavaTypeNode(int i) {
        super(i);
    }

    public AbstractJavaTypeNode(JavaParser p, int i) {
        super(p, i);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }
}

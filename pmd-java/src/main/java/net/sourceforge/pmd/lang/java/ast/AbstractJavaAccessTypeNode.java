/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractJavaAccessTypeNode extends AbstractJavaAccessNode implements TypeNode {

    private Class<?> type;

    public AbstractJavaAccessTypeNode(int i) {
        super(i);
    }

    public AbstractJavaAccessTypeNode(JavaParser parser, int i) {
        super(parser, i);
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

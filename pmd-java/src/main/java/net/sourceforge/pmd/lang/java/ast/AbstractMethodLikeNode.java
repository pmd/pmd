/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractMethodLikeNode extends AbstractJavaAccessNode implements MethodLikeNode {
    private JavaQualifiedName qualifiedName;


    AbstractMethodLikeNode(int i) {
        super(i);
    }


    AbstractMethodLikeNode(JavaParser parser, int i) {
        super(parser, i);
    }


    // TODO refine that type to be more specific when we split JavaQualifiedName into a hierarchy
    @Override
    public JavaQualifiedName getQualifiedName() {
        if (qualifiedName == null) {
            qualifiedName = QualifiedNameFactory.ofOperation(this);
        }
        return qualifiedName;
    }

}

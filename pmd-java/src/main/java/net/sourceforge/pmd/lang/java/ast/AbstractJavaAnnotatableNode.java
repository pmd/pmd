/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

abstract class AbstractJavaAnnotatableNode extends AbstractJavaNode implements Annotatable {

    AbstractJavaAnnotatableNode(int i) {
        super(i);
    }

    AbstractJavaAnnotatableNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public List<ASTAnnotation> getDeclaredAnnotations() {
        return this.jjtGetParent().findChildrenOfType(ASTAnnotation.class);
    }

}

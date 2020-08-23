/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

// package private
abstract class AbstractJavaAnnotatableNode extends AbstractJavaNode implements Annotatable {

    AbstractJavaAnnotatableNode(int i) {
        super(i);
    }

    @Override
    public List<ASTAnnotation> getDeclaredAnnotations() {
        return this.getParent().findChildrenOfType(ASTAnnotation.class);
    }

    @Override
    public ASTAnnotation getAnnotation(String annotQualifiedName) {
        List<ASTAnnotation> annotations = getDeclaredAnnotations();
        for (ASTAnnotation annotation : annotations) {
            ASTName name = annotation.getFirstDescendantOfType(ASTName.class);
            if (TypeTestUtil.isA(annotQualifiedName, name)) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public boolean isAnnotationPresent(String annotQualifiedName) {
        return getAnnotation(annotQualifiedName) != null;
    }

    @Override
    public boolean isAnyAnnotationPresent(Collection<String> annotQualifiedNames) {
        for (String annotQualifiedName : annotQualifiedNames) {
            if (isAnnotationPresent(annotQualifiedName)) {
                return true;
            }
        }
        return false;
    }
}

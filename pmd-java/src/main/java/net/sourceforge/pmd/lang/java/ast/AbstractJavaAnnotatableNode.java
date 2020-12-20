/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

// package private
abstract class AbstractJavaAnnotatableNode extends AbstractJavaNode implements Annotatable {

    AbstractJavaAnnotatableNode(int i) {
        super(i);
    }

    AbstractJavaAnnotatableNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public List<ASTAnnotation> getDeclaredAnnotations() {
        return this.getParent().findChildrenOfType(ASTAnnotation.class);
    }

    @Override
    public ASTAnnotation getAnnotation(String binaryName) {
        binaryName = StringUtils.deleteWhitespace(binaryName);
        List<ASTAnnotation> annotations = getDeclaredAnnotations();
        for (ASTAnnotation annotation : annotations) {
            ASTName name = annotation.getFirstDescendantOfType(ASTName.class);
            if (TypeTestUtil.isA(binaryName, name)) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public boolean isAnnotationPresent(String binaryName) {
        return getAnnotation(binaryName) != null;
    }

    @Override
    public boolean isAnyAnnotationPresent(Collection<String> binaryNames) {
        for (String annotQualifiedName : binaryNames) {
            if (isAnnotationPresent(annotQualifiedName)) {
                return true;
            }
        }
        return false;
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;

public interface Annotateable extends JavaNode {
    List<ASTAnnotation> getDeclaredAnnotations();

    ASTAnnotation getSpecificAnnotation(String annotQualifiedName);

    boolean isAnyAnnotationPresent(Collection<String> annotQualifiedNames);

    boolean isAnnotationPresent(String annotQualifiedName);
}

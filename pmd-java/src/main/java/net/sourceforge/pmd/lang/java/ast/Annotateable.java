/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;

public interface Annotateable extends JavaNode {
    boolean isAnyAnnotationPresent(Collection<String> annotQualifiedNames);

    boolean isAnnotationPresent(String annotQualifiedName);
}

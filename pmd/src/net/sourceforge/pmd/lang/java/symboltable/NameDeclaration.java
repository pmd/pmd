/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;

public interface NameDeclaration {
    JavaNode getNode();

    String getImage();

    Scope getScope();
}

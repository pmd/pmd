/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.ast.Node;

public interface NameDeclaration {
    Node getNode();

    String getImage();

    Scope getScope();
}

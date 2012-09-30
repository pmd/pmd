/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;

public interface NameDeclaration {
    SimpleNode getNode();

    String getImage();

    Scope getScope();
}

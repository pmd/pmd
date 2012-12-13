/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;

public interface NameDeclaration {
    PLSQLNode getNode();

    String getImage();

    Scope getScope();
}

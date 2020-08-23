/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.TypeNode;

public interface TypedNameDeclaration {

    String getTypeImage();

    Class<?> getType();

    /**
     * Nullable
     */
    TypeNode getTypeNode();

}

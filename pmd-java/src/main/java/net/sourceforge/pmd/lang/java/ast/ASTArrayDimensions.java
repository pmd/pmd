/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;


/**
 * Represents array type dimensions. This node may occur in several contexts:
 * <ul>
 * <li>In an {@linkplain ASTArrayType array type}</li>
 * <li>As the {@linkplain ASTMethodDeclaration#getExtraDimensions() extra dimensions of a method declaration},
 * after the formal parameter list. For example:
 * <pre>public int newIntArray(int length) [];</pre>
 * </li>
 * <li>As the {@linkplain ASTVariableDeclaratorId#getExtraDimensions() extra dimensions of a variable declarator id},
 * in a {@linkplain ASTVariableDeclarator variable declarator}. For example:
 * <pre>public int a[], b[][];</pre>
 * </li>
 * </ul>
 *
 * <p>Some dimensions may be initialized with an expression, but only in
 * the array type of an {@linkplain ASTArrayAllocation array allocation expression}.
 *
 * <pre class="grammar">
 *
 * ArrayDimensions ::= {@link ASTArrayTypeDim ArrayTypeDim}+ {@link ASTArrayDimExpr ArrayDimExpr}*
 *
 * </pre>
 */
public final class ASTArrayDimensions extends ASTNonEmptyList<ASTArrayTypeDim> {


    ASTArrayDimensions(int id) {
        super(id, ASTArrayTypeDim.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


}

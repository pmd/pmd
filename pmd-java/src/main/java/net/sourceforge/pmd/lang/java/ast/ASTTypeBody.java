/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Body of a type declaration.
 *
 * <pre class="grammar">
 *
 * TypeBody ::= {@link ASTClassOrInterfaceBody ClassOrInterfaceBody}
 *            | {@link ASTEnumBody EnumBody}
 *            | {@link ASTAnnotationTypeBody}
 *
 * </pre>
 *
 * @author Clément Fournier
 */
public interface ASTTypeBody extends JavaNode {


    default NodeStream<ASTBodyDeclaration> getDeclarations() {
        return children(ASTBodyDeclaration.class);
    }


}

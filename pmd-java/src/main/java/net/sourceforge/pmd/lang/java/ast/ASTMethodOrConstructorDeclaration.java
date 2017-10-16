/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.SignedNode;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;

/**
 * @author Clément Fournier
 */
public interface ASTMethodOrConstructorDeclaration extends
                                                   SignedNode<ASTMethodOrConstructorDeclaration>,
                                                   JavaQualifiableNode,
                                                   AccessNode,
                                                   JavaNode {

    @Override
    JavaOperationSignature getSignature();

}

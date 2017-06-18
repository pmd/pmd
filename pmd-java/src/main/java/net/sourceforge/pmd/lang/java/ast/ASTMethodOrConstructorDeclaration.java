/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author Clément Fournier
 */
public interface ASTMethodOrConstructorDeclaration extends QualifiableNode, Node, AccessNode, JavaNode {

    /**
     * Returns a map of parameter names to their type image. Iterating over its keys yields the parameters in the
     * right order.
     *
     * @return A map of parameter names to their type image.
     */
    Map<String, String> getParameterMap();

    // TODO could use a default implementation when we change the compiler version to 1.8

}

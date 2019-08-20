/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeMethod extends AbstractScalaNode<Type.Method> {

    public ASTTypeMethod(Type.Method scalaNode) {
        super(scalaNode);
    }
}

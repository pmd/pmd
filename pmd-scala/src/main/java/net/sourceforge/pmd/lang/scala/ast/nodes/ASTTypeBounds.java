/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeBounds extends AbstractScalaNode<Type.Bounds> {

    public ASTTypeBounds(Type.Bounds scalaNode) {
        super(scalaNode);
    }
}

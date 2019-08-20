/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeTuple extends AbstractScalaNode<Type.Tuple> {

    public ASTTypeTuple(Type.Tuple scalaNode) {
        super(scalaNode);
    }
}

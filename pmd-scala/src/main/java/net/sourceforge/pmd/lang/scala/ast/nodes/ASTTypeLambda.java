/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeLambda extends AbstractScalaNode<Type.Lambda> {

    public ASTTypeLambda(Type.Lambda scalaNode) {
        super(scalaNode);
    }
}

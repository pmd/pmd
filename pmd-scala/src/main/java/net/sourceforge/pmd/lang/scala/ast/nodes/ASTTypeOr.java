/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeOr extends AbstractScalaNode<Type.Or> {

    public ASTTypeOr(Type.Or scalaNode) {
        super(scalaNode);
    }
}

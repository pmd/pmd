/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeByName extends AbstractScalaNode<Type.ByName> {

    public ASTTypeByName(Type.ByName scalaNode) {
        super(scalaNode);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeRepeated extends AbstractScalaNode<Type.Repeated> {

    public ASTTypeRepeated(Type.Repeated scalaNode) {
        super(scalaNode);
    }
}

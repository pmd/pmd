/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeParam extends AbstractScalaNode<Type.Param> {

    public ASTTypeParam(Type.Param scalaNode) {
        super(scalaNode);
    }
}

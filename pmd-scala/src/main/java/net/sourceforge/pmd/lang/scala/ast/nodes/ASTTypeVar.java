/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeVar extends AbstractScalaNode<Type.Var> {

    public ASTTypeVar(Type.Var scalaNode) {
        super(scalaNode);
    }

}

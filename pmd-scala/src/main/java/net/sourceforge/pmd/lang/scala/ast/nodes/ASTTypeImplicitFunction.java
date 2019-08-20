/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeImplicitFunction extends AbstractScalaNode<Type.ImplicitFunction> {

    public ASTTypeImplicitFunction(Type.ImplicitFunction scalaNode) {
        super(scalaNode);
    }
}

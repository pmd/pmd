/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeFunction extends AbstractScalaNode<Type.Function> {

    public ASTTypeFunction(Type.Function scalaNode) {
        super(scalaNode);
    }
}

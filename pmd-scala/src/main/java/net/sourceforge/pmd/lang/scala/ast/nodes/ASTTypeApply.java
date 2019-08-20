/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeApply extends AbstractScalaNode<Type.Apply> {

    public ASTTypeApply(Type.Apply scalaNode) {
        super(scalaNode);
    }
}

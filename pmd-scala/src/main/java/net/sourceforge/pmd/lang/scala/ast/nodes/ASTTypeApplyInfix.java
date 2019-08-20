/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeApplyInfix extends AbstractScalaNode<Type.ApplyInfix> {

    public ASTTypeApplyInfix(Type.ApplyInfix scalaNode) {
        super(scalaNode);
    }
}

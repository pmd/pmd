/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeRefine extends AbstractScalaNode<Type.Refine> {

    public ASTTypeRefine(Type.Refine scalaNode) {
        super(scalaNode);
    }
}

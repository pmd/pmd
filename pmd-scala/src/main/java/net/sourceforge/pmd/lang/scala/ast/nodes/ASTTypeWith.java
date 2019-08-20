/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeWith extends AbstractScalaNode<Type.With> {

    public ASTTypeWith(Type.With scalaNode) {
        super(scalaNode);
    }
}

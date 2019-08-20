/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Ctor;

public class ASTCtorPrimary extends AbstractScalaNode<Ctor.Primary> {

    public ASTCtorPrimary(Ctor.Primary scalaNode) {
        super(scalaNode);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Enumerator;

public class ASTEnumeratorGuard extends AbstractScalaNode<Enumerator.Guard> {

    public ASTEnumeratorGuard(Enumerator.Guard scalaNode) {
        super(scalaNode);
    }
}

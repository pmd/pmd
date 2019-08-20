/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Enumerator;

public class ASTEnumeratorVal extends AbstractScalaNode<Enumerator.Val> {

    public ASTEnumeratorVal(Enumerator.Val scalaNode) {
        super(scalaNode);
    }
}

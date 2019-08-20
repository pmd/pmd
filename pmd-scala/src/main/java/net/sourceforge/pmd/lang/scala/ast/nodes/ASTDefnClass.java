/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Defn;

public class ASTDefnClass extends AbstractScalaNode<Defn.Class> {

    public ASTDefnClass(Defn.Class scalaNode) {
        super(scalaNode);
    }
}

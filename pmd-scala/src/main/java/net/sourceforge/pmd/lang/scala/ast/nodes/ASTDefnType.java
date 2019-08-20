/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Defn;

public class ASTDefnType extends AbstractScalaNode<Defn.Type> {

    public ASTDefnType(Defn.Type scalaNode) {
        super(scalaNode);
    }
}

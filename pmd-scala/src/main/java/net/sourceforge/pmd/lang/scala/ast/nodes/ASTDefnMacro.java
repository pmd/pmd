/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Defn;

public class ASTDefnMacro extends AbstractScalaNode<Defn.Macro> {

    public ASTDefnMacro(Defn.Macro scalaNode) {
        super(scalaNode);
    }
}

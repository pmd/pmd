/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Decl;

public class ASTDeclVal extends AbstractScalaNode<Decl.Val> {

    public ASTDeclVal(Decl.Val scalaNode) {
        super(scalaNode);
    }
}

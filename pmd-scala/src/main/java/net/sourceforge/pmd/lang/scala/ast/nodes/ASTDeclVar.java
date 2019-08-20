/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Decl;

public class ASTDeclVar extends AbstractScalaNode<Decl.Var> {

    public ASTDeclVar(Decl.Var scalaNode) {
        super(scalaNode);
    }
}

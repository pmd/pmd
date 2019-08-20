/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Decl;

public class ASTDeclDef extends AbstractScalaNode<Decl.Def> {

    public ASTDeclDef(Decl.Def scalaNode) {
        super(scalaNode);
    }
}

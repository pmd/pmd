/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Decl;

public class ASTDeclType extends AbstractScalaNode<Decl.Type> {

    public ASTDeclType(Decl.Type scalaNode) {
        super(scalaNode);
    }
}

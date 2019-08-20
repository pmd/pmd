/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Importee;

public class ASTImporteeUnimport extends AbstractScalaNode<Importee.Unimport> {

    public ASTImporteeUnimport(Importee.Unimport scalaNode) {
        super(scalaNode);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Importee;

public class ASTImporteeRename extends AbstractScalaNode<Importee.Rename> {

    public ASTImporteeRename(Importee.Rename scalaNode) {
        super(scalaNode);
    }
}

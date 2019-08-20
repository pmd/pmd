/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Importee;

public class ASTImporteeName extends AbstractScalaNode<Importee.Name> {

    public ASTImporteeName(Importee.Name scalaNode) {
        super(scalaNode);
    }
}

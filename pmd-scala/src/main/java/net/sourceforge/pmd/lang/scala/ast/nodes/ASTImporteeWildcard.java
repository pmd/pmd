/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Importee;

public class ASTImporteeWildcard extends AbstractScalaNode<Importee.Wildcard> {

    public ASTImporteeWildcard(Importee.Wildcard scalaNode) {
        super(scalaNode);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Import;

public class ASTImport extends AbstractScalaNode<Import> {

    public ASTImport(Import scalaNode) {
        super(scalaNode);
    }
}

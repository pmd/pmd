/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Pat;

public class ASTPatSeqWildcard extends AbstractScalaNode<Pat.SeqWildcard> {

    public ASTPatSeqWildcard(Pat.SeqWildcard scalaNode) {
        super(scalaNode);
    }
}

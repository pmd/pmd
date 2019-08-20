/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermXml extends AbstractScalaNode<Term.Xml> {

    public ASTTermXml(Term.Xml scalaNode) {
        super(scalaNode);
    }
}

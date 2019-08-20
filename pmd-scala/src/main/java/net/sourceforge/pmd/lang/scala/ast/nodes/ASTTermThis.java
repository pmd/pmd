/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Term;

public class ASTTermThis extends AbstractScalaNode<Term.This> {

    public ASTTermThis(Term.This scalaNode) {
        super(scalaNode);
    }
}

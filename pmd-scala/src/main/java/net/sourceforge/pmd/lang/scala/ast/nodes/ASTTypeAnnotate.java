/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeAnnotate extends AbstractScalaNode<Type.Annotate> {

    public ASTTypeAnnotate(Type.Annotate scalaNode) {
        super(scalaNode);
    }
}

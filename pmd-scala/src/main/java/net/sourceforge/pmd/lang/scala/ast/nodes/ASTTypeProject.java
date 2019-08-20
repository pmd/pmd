/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeProject extends AbstractScalaNode<Type.Project> {

    public ASTTypeProject(Type.Project scalaNode) {
        super(scalaNode);
    }
}

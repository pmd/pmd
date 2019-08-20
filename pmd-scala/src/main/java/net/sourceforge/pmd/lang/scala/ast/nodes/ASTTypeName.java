/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeName extends AbstractScalaNode<Type.Name> {

    public ASTTypeName(Type.Name scalaNode) {
        super(scalaNode);
    }

    @Override
    public String getImage() {
        return getNode().value();
    }
}

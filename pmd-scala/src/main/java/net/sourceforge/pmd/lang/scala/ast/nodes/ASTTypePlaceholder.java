/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypePlaceholder extends AbstractScalaNode<Type.Placeholder> {

    public ASTTypePlaceholder(Type.Placeholder scalaNode) {
        super(scalaNode);
    }
}

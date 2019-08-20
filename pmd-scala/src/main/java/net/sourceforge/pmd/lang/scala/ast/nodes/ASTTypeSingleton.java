/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeSingleton extends AbstractScalaNode<Type.Singleton> {

    public ASTTypeSingleton(Type.Singleton scalaNode) {
        super(scalaNode);
    }
}

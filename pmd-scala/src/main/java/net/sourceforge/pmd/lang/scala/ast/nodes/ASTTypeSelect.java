/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast.nodes;

import scala.meta.Type;

public class ASTTypeSelect extends AbstractScalaNode<Type.Select> {

    public ASTTypeSelect(Type.Select scalaNode) {
        super(scalaNode);
    }
}

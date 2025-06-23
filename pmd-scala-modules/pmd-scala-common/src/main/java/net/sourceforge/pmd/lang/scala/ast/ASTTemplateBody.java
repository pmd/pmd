/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import scala.meta.Template;

/**
 * The ASTTemplateBody node implementation.
 * @since 7.10.0
 */
public final class ASTTemplateBody extends AbstractScalaNode<Template.Body> {

    ASTTemplateBody(Template.Body scalaNode) {
        super(scalaNode);
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface HtmlNode extends Node {

    @Override
    Iterable<? extends HtmlNode> children();

    Object acceptVisitor(HtmlVisitor visitor, Object data);
}

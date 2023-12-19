/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import net.sourceforge.pmd.lang.ast.Node;

public interface CommentNode extends Node {

    String getData();

    @Override
    default String getXPathNodeName() {
        return "#comment";
    }
}

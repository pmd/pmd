/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import net.sourceforge.pmd.lang.ast.Node;

public interface TextNode extends Node {
    String getText();

    @Override
    default String getXPathNodeName() {
        return "#text";
    }
}

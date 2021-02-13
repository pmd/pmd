/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Marker interface.
 */
public interface AstNodeOwner {

    Node getUnderlyingNode();
}

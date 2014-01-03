/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import java.util.Iterator;

/**
 * This interface can be used by an AST node to indicate it can directly provide
 * access to it's attributes, versus having them be determined via introspection.
 */
public interface AttributeNode {
    Iterator<Attribute> getAttributeIterator();
}

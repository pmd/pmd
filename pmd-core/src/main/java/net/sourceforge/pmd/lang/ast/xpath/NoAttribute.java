/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Filters out some methods from the XPath attributes of a node.
 *
 * @author Clément Fournier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.METHOD, ElementType.TYPE})
public @interface NoAttribute {

    /**
     * When applied to a type declaration, this value determines which
     * XPath attributes are filtered out.
     */
    NoAttrScope scope() default NoAttrScope.ALL;


    enum NoAttrScope {
        /**
         * Only attributes inherited from superclasses or superinterfaces
         * are filtered out (except those from {@link Node} and {@link AbstractNode}).
         */
        INHERITED,
        /** All attributes are suppressed. */
        ALL
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;

/**
 * Filters out some methods from the XPath attributes of a node.
 *
 * @author Clément Fournier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NoAttribute {

    /**
     * When applied to a type declaration, this value determines which
     * XPath attributes are filtered out. When applied to an attribute
     * accessor this value has no effect and the annotation suppresses
     * the attribute in any case.
     */
    NoAttrScope scope() default NoAttrScope.ALL;


    enum NoAttrScope {
        /**
         * Only attributes inherited from superclasses or superinterfaces
         * are filtered out (except those from {@link Node} and {@link AbstractNode}).
         * Attributes defined here, or overridden, are kept. Attributes can
         * be suppressed individually with a {@link NoAttribute} on their
         * accessor.
         */
        INHERITED,
        /**
         * All attributes are suppressed, except those from {@link Node}
         * and {@link AbstractNode}. This extends {@link #INHERITED} to
         * the attributes defined in this class. Note that subclasses of
         * the current class also will see those attributes suppressed
         * unless they override them.
         */
        ALL
    }
}

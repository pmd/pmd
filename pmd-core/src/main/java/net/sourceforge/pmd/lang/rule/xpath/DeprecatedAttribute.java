/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Node attribute getter methods might be annotated with {@code DeprecatedAttribute}
 * to mark the attribute as deprecated for XPath. Unlike {@link Deprecated}, this
 * annotation does not deprecate the method for java usage.
 *
 * <p>When used in combination with {@link Deprecated}, this attribute allows specifying
 * a replacement for the XPath attribute.
 *
 * @since 6.21.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeprecatedAttribute {

    /** Sentinel expressing that the attribute is deprecated without replacement. */
    String NO_REPLACEMENT = "";


    /**
     * An XPath expression to suggest as a replacement for use of the
     * deprecated attribute.
     * If empty, then the attribute is deprecated for removal.
     * Example values: {@code @NewerAttribute}, {@code NodeType/@SomeAttribute},
     * {@code some-function()}.
     */
    String replaceWith() default NO_REPLACEMENT;
}

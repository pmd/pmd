/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

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
 * @since 6.21.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeprecatedAttribute {
}

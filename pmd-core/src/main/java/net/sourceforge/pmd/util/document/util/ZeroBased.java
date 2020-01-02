/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify that some number's valid values start with 0 (inclusive).
 */
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ZeroBased {
}

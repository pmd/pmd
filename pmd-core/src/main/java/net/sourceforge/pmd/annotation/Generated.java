/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a class as generated code, and therefore to be ignored for code coverage purposes.
 *
 * @since 7.6.0
 */
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Generated {

    /** The generator that produced this code */
    String value() default "";

}

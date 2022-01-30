/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;


/**
 * Indicates the feature is in experimental state: its existence, signature
 * or behavior might change without warning from one release to the next.
 * The only clients that are "safe" using experimental APIs are the sources
 * of PMD itself.
 *
 * @since 6.7.0
 */
@Documented
public @interface Experimental {

    /** A reason given for the experimental status. */
    String value() default "";

}

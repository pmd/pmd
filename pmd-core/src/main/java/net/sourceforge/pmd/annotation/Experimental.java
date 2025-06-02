/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;


/**
 * Tagged API members are subject to change.
 * It is an indication that the feature is in experimental, unstable state.
 * The API members can be modified in any way, or even removed, at any time, without warning.
 * You should not use or rely on them in any production code. They are purely to allow broad testing and feedback.
 *
 * @since 6.7.0
 */
@Documented
public @interface Experimental {

    /** A reason given for the experimental status. */
    String value() default "";

}

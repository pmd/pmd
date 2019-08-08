/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Complements a {@link Deprecated} annotation with information about
 * migration. These features were only added to {@link Deprecated} in
 * JDK 9.
 *
 * <p>This annotation does not replace a {@code @deprecated} Javadoc
 * tag and is used to display information to XPath users (while Javadoc
 * caters to the Java API). It's not published API, also not {@link Documented}.
 *
 * @since 6.18.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathMigration {


    /**
     * An English sentence like "Use @SuchAttribute instead".
     */
    String replacement();


}

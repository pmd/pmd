/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * Indicates that subclassing this type is not publicly
 * supported API. Abstract methods may be added or removed
 * at any time, which could break binary compatibility with
 * existing implementors. Protected methods are also part of
 * the private API of this type.
 *
 * <p>The API that is not inheritance-specific (unless {@linkplain InternalApi noted otherwise},
 * all public members), is still public API and will remain binary-
 * compatible between major releases.
 *
 * <p>Types tagged with this annotation will remain supported
 * until 7.0.0, at which point no guarantees will be maintained
 * about the stability of the inheritance hierarchy for external
 * clients.
 *
 * <p>This should be used for example for base rule classes that
 * are meant to be used in PMD only, or for AST-related interfaces
 * and abstract classes.
 *
 * @since 6.7.0
 */
@Target(ElementType.TYPE)
@Documented
public @interface ReservedSubclassing {
}

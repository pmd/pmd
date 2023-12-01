/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * Indicates that annotated types are only meant to be subclassed
 * by classes within PMD. These types are only partially public API. Abstract or protected methods
 * may be added or removed at any time, which could break compatibility with existing
 * implementors.
 *
 * <p>The API that is not inheritance-specific (all public members) is still public API,
 * unless the public members are marked as {@link InternalApi} explicitly.
 *
 * <p>The annotation is <strong>not</strong> inherited, which
 * means a reserved interface doesn't prevent its implementors to be subclassed.
 *
 * <p>This should be used for example for base rule classes that
 * are meant to be used in PMD only, or for AST-related interfaces
 * and abstract classes.
 *
 * <p>Types tagged with this annotation will remain supported
 * until 7.0.0, at which point no guarantees will be maintained
 * about the stability of the inheritance hierarchy for external
 * clients.
 *
 * @since 6.7.0
 */
@Target(ElementType.TYPE)
@Documented
public @interface ReservedSubclassing {
}

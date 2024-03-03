/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.properties.internal.PropertyTypeId;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static <B extends PropertyBuilder<B, ?>> B withTypeId(PropertyBuilder<B, ?> builder, PropertyTypeId typeId) {
        return builder.typeId(typeId);
    }

    public static @Nullable PropertyTypeId getTypeId(PropertyDescriptor<?> propertyDescriptor) {
        return propertyDescriptor.getTypeId();
    }

    public abstract static class InternalPropertySerializer<T> extends PropertySerializer<T> {
        // make the default constructor available
        protected InternalPropertySerializer() {
            super();
        }
    }
}

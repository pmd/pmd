/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public abstract class AbstractPropertyBuilder<B extends AbstractPropertyBuilder<B, T>, T> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w-]*");
    protected final Set<PropertyValidator<T>> validators = new LinkedHashSet<>();
    protected String name;
    protected String description;
    protected float uiOrder = 0f;


    public AbstractPropertyBuilder(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be provided");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        this.name = name;
    }


    /**
     * Specify the description of the property.
     *
     * @param desc The description
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B desc(String desc) {
        if (StringUtils.isBlank(desc)) {
            throw new IllegalArgumentException("Description must be provided");
        }
        this.description = desc;
        return (B) this;
    }


    /**
     * Specify the UI order of the property.
     *
     * @param f The UI order
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B uiOrder(float f) {
        this.uiOrder = f;
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    protected B addValidator(Predicate<T> pred, String errorMessage) {
        validators.add(PropertyValidator.fromPredicate(pred, errorMessage));
        return (B) this;
    }


    /**
     * Builds the descriptor and returns it.
     *
     * @return The built descriptor
     *
     * @throws IllegalArgumentException if parameters are incorrect
     */
    public abstract PropertyDescriptor<T> build();


    /**
     * Returns the name of the property to be built.
     */
    public String getName() {
        return name;
    }
}

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.util.List;

public interface TypeDefinition {
    /**
     * Get the raw Class type of the definition.
     *
     * @return Raw Class type.
     */
    Class getType();

    /**
     * Get the list of type arguments for this TypeDefinition.
     *
     * @return An ordered and immutable list of type arguments.
     */
    List<? extends TypeDefinition> getGenericArgs();
}

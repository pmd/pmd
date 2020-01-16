/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.properties.PropertySource;

/**
 * Describes the configuration options of a specific {@link TreeRenderer}.
 *
 * @see TreeRenderers
 */
@Experimental
public interface TreeRendererDescriptor {

    /**
     * Returns a new property bundle, that can be used to configure
     * the output of {@link #produceRenderer(PropertySource)}. Properties
     * supported by the renderer are already registered on the returned
     * bundle.
     */
    PropertySource newPropertyBundle();


    /**
     * Returns the ID of this renderer, used to select it.
     * The ID of a descriptor should never change.
     *
     * @see TreeRenderers#findById(String)
     */
    String id();


    /**
     * Returns a short description of the format of this renderer's output.
     */
    String description();


    /**
     * Builds a new renderer from the given properties.
     *
     * @param properties A property bundle, that should have been produced by
     *                   {@link #newPropertyBundle()}.
     */
    TreeRenderer produceRenderer(PropertySource properties);


}

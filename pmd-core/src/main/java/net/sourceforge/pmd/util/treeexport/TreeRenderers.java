/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer.XmlRenderingConfig;

/**
 * Entry point to fetch and register tree renderers. This API is meant
 * to be be integrated in tools that operate on tree descriptors generically.
 * For that reason the standard descriptors provided by PMD and their
 * properties are not public.
 *
 * @see #findById(String)
 * @see #register(TreeRendererDescriptor)
 */
@Experimental
public final class TreeRenderers {

    // descriptors are test only

    static final PropertyDescriptor<Boolean> XML_RENDER_PROLOG =
        PropertyFactory.booleanProperty("renderProlog")
                       .desc("True to output a prolog")
                       .defaultValue(true)
                       .build();

    static final PropertyDescriptor<Boolean> XML_USE_SINGLE_QUOTES =
        PropertyFactory.booleanProperty("singleQuoteAttributes")
                       .desc("Use single quotes to delimit attribute values")
                       .defaultValue(true)
                       .build();


    static final PropertyDescriptor<String> XML_LINE_SEPARATOR =
        PropertyFactory.stringProperty("lineSeparator")
                       .desc("Line separator to use. The default is platform-specific.")
                       .defaultValue(System.lineSeparator())
                       .build();

    static final PropertyDescriptor<Boolean> XML_RENDER_COMMON_ATTRIBUTES =
        PropertyFactory.booleanProperty("renderCommonAttributes")
                       .desc("True to render attributes like BeginLine, EndLine, etc.")
                       .defaultValue(false)
                       .build();


    static final TreeRendererDescriptor XML =
        new TreeRendererDescriptorImpl("xml", "XML format with the same structure as the one used in XPath") {

            private final Set<PropertyDescriptor<?>> myDescriptors
                = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.<PropertyDescriptor<?>>asList(
                XML_USE_SINGLE_QUOTES, XML_LINE_SEPARATOR, XML_RENDER_PROLOG, XML_RENDER_COMMON_ATTRIBUTES
            )));

            @Override
            protected Set<PropertyDescriptor<?>> availableDescriptors() {
                return myDescriptors;
            }

            @Override
            public TreeRenderer produceRenderer(final PropertySource properties) {

                XmlRenderingConfig config =
                    new XmlRenderingConfig() {

                        private final List<String> excluded = Arrays.asList("BeginLine", "BeginColumn", "EndLine", "EndColumn", "SingleLine", "FindBoundary");

                        @Override
                        protected boolean takeAttribute(Node node, Attribute attribute) {
                            if (!properties.getProperty(XML_RENDER_COMMON_ATTRIBUTES)) {
                                return !excluded.contains(attribute.getName());
                            }
                            return true;
                        }
                    }
                        .singleQuoteAttributes(properties.getProperty(XML_USE_SINGLE_QUOTES))
                        .renderProlog(properties.getProperty(XML_RENDER_PROLOG))
                        .lineSeparator(properties.getProperty(XML_LINE_SEPARATOR));

                return new XmlTreeRenderer(config);
            }
        };

    private static final Map<String, TreeRendererDescriptor> REGISTRY = new ConcurrentHashMap<>();


    static {
        REGISTRY.put(XML.id(), XML);
    }


    private TreeRenderers() {

    }

    /**
     * Returns the renderer descriptor registered by the given ID.
     * Returns null if not found, or the id is null.
     *
     * @param id ID of the renderer to find
     *
     * @return The descriptor, or null
     */
    public static TreeRendererDescriptor findById(String id) {
        synchronized (REGISTRY) {
            return REGISTRY.get(id);
        }
    }

    /**
     * Returns the set of renderers currently registered. Order is
     * undefined.
     */
    public static Collection<TreeRendererDescriptor> registeredRenderers() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    /**
     * Registers the given renderer. If registration succeeds (the ID
     * is not already associated to a descriptor), the descriptor
     * will be available with {@link #findById(String)}.
     *
     * @param descriptor Descriptor to register
     *
     * @return True if the registration succeeded, false if there was
     *     already a registered renderer with the given ID.
     */
    public static boolean register(TreeRendererDescriptor descriptor) {
        synchronized (REGISTRY) {
            if (REGISTRY.containsKey(descriptor.id())) {
                return false;
            }
            REGISTRY.put(descriptor.id(), descriptor);
        }
        return true;
    }

}

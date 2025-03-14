/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.util.Collections;
import java.util.Set;

import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

abstract class TreeRendererDescriptorImpl implements TreeRendererDescriptor {

    private final String id;
    private final String description;


    protected TreeRendererDescriptorImpl(String id, String description) {
        this.id = id;
        this.description = description;
    }


    @Override
    public PropertySource newPropertyBundle() {
        return new PropertyBundle(id, availableDescriptors());
    }


    protected Set<PropertyDescriptor<?>> availableDescriptors() {
        return Collections.emptySet();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return "TreeDescriptorImpl{"
            + "id='" + id + '\''
            + ", description='" + description + '\''
            + '}';
    }

    private static class PropertyBundle extends AbstractPropertySource {


        private final String name;

        PropertyBundle(String name,
                              Set<PropertyDescriptor<?>> available) {
            this.name = name;
            for (PropertyDescriptor<?> p : available) {
                definePropertyDescriptor(p);
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        protected String getPropertySourceType() {
            return "tree renderer";
        }
    }
}

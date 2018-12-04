/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;


/**
 * Visits a bean model and restores the properties described by the nodes
 * into their respective settings owner.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class RestorePropertyVisitor extends BeanNodeVisitor<SettingsOwner> {


    @Override
    public void visit(SimpleBeanModelNode model, SettingsOwner target) {
        if (model == null) {
            return; // possibly it wasn't saved during the previous save cycle
        }

        if (target == null) {
            throw new IllegalArgumentException();
        }

        if (target.getClass() != model.getNodeType()) {
            throw new IllegalArgumentException("Incorrect settings restoration target, expected "
                                                       + model.getNodeType() + ", actual " + target.getClass());
        }

        Map<String, PropertyDescriptor> descriptors = Arrays.stream(PropertyUtils.getPropertyDescriptors(target))
                                                            .filter(d -> d.getReadMethod() != null && d.getReadMethod().isAnnotationPresent(PersistentProperty.class))
                                                            .collect(Collectors.toMap(PropertyDescriptor::getName, d -> d));

        for (Entry<String, Object> saved : model.getSettingsValues().entrySet()) {
            if (descriptors.containsKey(saved.getKey())) {
                try {
                    PropertyUtils.setProperty(target, saved.getKey(), saved.getValue());
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Error setting property " + saved.getKey() + " on a " + target.getClass().getSimpleName());
                    e.printStackTrace();
                }
            }
        }

        for (BeanModelNodeSeq<?> seq : model.getSequenceProperties()) {
            this.visit(seq, target);
        }

        for (SettingsOwner child : target.getChildrenSettingsNodes()) {
            model.getChildrenByType().get(child.getClass()).accept(this, child);
        }
    }


    @Override
    public void visit(BeanModelNodeSeq<?> model, SettingsOwner target) {
        if (model == null) {
            return; // possibly it wasn't saved during the previous save cycle
        }

        if (target == null) {
            throw new IllegalArgumentException();
        }

        Collection<SettingsOwner> container;
        try {
            @SuppressWarnings("unchecked")
            Collection<SettingsOwner> tmp = (Collection<SettingsOwner>) PropertyUtils.getProperty(target, model.getPropertyName());
            container = tmp;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }


        Iterator<SettingsOwner> existingItems = container.iterator();
        Class<?> itemType = null;
        // use a buffer to avoid concurrent modification
        List<SettingsOwner> itemsToAdd = new ArrayList<>();

        for (SimpleBeanModelNode child : model.getChildrenNodes()) {
            SettingsOwner item;
            if (existingItems.hasNext()) {
                item = existingItems.next();
            } else {
                if (itemType == null) {
                    itemType = child.getNodeType();
                }

                try {
                    item = (SettingsOwner) itemType.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    continue; // try hard
                }
            }

            child.accept(this, item);
            itemsToAdd.add(item);
        }

        container.addAll(itemsToAdd);

        try {
            PropertyUtils.setProperty(target, model.getPropertyName(), container);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

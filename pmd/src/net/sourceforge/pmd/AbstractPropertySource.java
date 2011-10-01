package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractPropertySource implements PropertySource {

	protected List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<PropertyDescriptor<?>>();
	protected Map<PropertyDescriptor<?>, Object> propertyValuesByDescriptor = new HashMap<PropertyDescriptor<?>, Object>();

	public AbstractPropertySource() {
		super();
	}

	protected List<PropertyDescriptor<?>> copyPropertyDescriptors() {
		List<PropertyDescriptor<?>> copy = new ArrayList<PropertyDescriptor<?>>(propertyDescriptors.size());
		copy.addAll(propertyDescriptors);
		return copy;
	}

	protected Map<PropertyDescriptor<?>, Object> copyPropertyValues() {
		Map<PropertyDescriptor<?>, Object> copy = new HashMap<PropertyDescriptor<?>, Object>(propertyValuesByDescriptor.size());
		copy.putAll(propertyValuesByDescriptor);
		return copy;
	}

	/**
	  * @see Rule#ignoredProperties()
	  */
	public Set<PropertyDescriptor<?>> ignoredProperties() {
		 return Collections.emptySet();
	 }

	/**
	  * @see Rule#definePropertyDescriptor(PropertyDescriptor)
	  */
	public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
		 // Check to ensure the property does not already exist.
		 for (PropertyDescriptor<?> descriptor : propertyDescriptors) {
			 if (descriptor.name().equals(propertyDescriptor.name())) {
				 throw new IllegalArgumentException("There is already a PropertyDescriptor with name '"
						 + propertyDescriptor.name() + "' defined on Rule " + getName() + ".");
			 }
		 }
		 propertyDescriptors.add(propertyDescriptor);
		 // Sort in UI order
		 Collections.sort(propertyDescriptors);
	 }

	public abstract String getName(); 
	
	/**
	  * @see Rule#getPropertyDescriptor(String)
	  */
	public PropertyDescriptor<?> getPropertyDescriptor(String name) {
		 for (PropertyDescriptor<?> propertyDescriptor : propertyDescriptors) {
			 if (name.equals(propertyDescriptor.name())) {
				 return propertyDescriptor;
			 }
		 }
		 return null;
	 }

	/**
	  * @see Rule#hasDescriptor(PropertyDescriptor)
	  */
	public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
	
		 if (propertyValuesByDescriptor.isEmpty()) {
			 propertyValuesByDescriptor = getPropertiesByPropertyDescriptor();
		 }
	
		 return propertyValuesByDescriptor.containsKey(descriptor);
	 }

	/**
	  * @see Rule#getPropertyDescriptors()
	  */
	public List<PropertyDescriptor<?>> getPropertyDescriptors() {
		 return propertyDescriptors;
	 }

	/**
	  * @see Rule#getProperty(PropertyDescriptor)
	  */
	@SuppressWarnings("unchecked")
	public <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
		 checkValidPropertyDescriptor(propertyDescriptor);
		 T value;
		 if (propertyValuesByDescriptor.containsKey(propertyDescriptor)) {
			 value = (T) propertyValuesByDescriptor.get(propertyDescriptor);
		 } else {
			 value = propertyDescriptor.defaultValue();
		 }
		 return value;
	 }

	/**
	  * @see Rule#setProperty(PropertyDescriptor, Object)
	  */
	public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
		 checkValidPropertyDescriptor(propertyDescriptor);
		 propertyValuesByDescriptor.put(propertyDescriptor, value);
	 }

	private void checkValidPropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
		 if (!propertyDescriptors.contains(propertyDescriptor)) {
			 throw new IllegalArgumentException("Property descriptor not defined for Rule " + getName() + ": "
					 + propertyDescriptor);
		 }
	 }

	/**
	  * @see Rule#getPropertiesByPropertyDescriptor()
	  */
	public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
		 if (propertyDescriptors.isEmpty()) {
			 return Collections.emptyMap();
		 }
	
		 Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor = new HashMap<PropertyDescriptor<?>, Object>(
				 propertyDescriptors.size());
		 // Fill with existing explicitly values
		 propertiesByPropertyDescriptor.putAll(this.propertyValuesByDescriptor);
	
		 // Add default values for anything not yet set
		 for (PropertyDescriptor<?> propertyDescriptor : this.propertyDescriptors) {
			 if (!propertiesByPropertyDescriptor.containsKey(propertyDescriptor)) {
				 propertiesByPropertyDescriptor.put(propertyDescriptor, propertyDescriptor.defaultValue());
			 }
		 }
	
		 return propertiesByPropertyDescriptor;
	 }

	/**
	  * @see Rule#usesDefaultValues()
	  */
	public boolean usesDefaultValues() {
	
		 Map<PropertyDescriptor<?>, Object> valuesByProperty = getPropertiesByPropertyDescriptor();
		 if (valuesByProperty.isEmpty()) {
			 return true;
		 }
	
		 Iterator<Map.Entry<PropertyDescriptor<?>, Object>> iter = valuesByProperty.entrySet().iterator();
	
		 while (iter.hasNext()) {
			 Map.Entry<PropertyDescriptor<?>, Object> entry = iter.next();
			 if (!CollectionUtil.areEqual(entry.getKey().defaultValue(), entry.getValue())) {
				 return false;
			 }
		 }
	
		 return true;
	 }

	public void useDefaultValueFor(PropertyDescriptor<?> desc) {
		 propertyValuesByDescriptor.remove(desc);
	 }

	 /**
	  * @see PropertySource#dysfunctionReason()
	  */
	 public String dysfunctionReason() {
		 return null;
	 }
}
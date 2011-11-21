package net.sourceforge.pmd;

import java.util.Map;

/**
 * 
 * @author Brian Remedios
 */
public interface PropertyDescriptorFactory {

	Class<?> valueType();
	/**
	 * Denote the identifiers of the expected fields paired with booleans denoting whether they are
	 * required (non-null) or not.
	 * 
	 * @return Map
	 */
	Map<String, Boolean> expectedFields();

	/**
	 * Create a property descriptor of the appropriate type using the values provided. 
	 * 
	 * @param valuesById
	 * @return PropertyDescriptor<?>
	 */
	PropertyDescriptor<?> createWith(Map<String, String> valuesById);
}

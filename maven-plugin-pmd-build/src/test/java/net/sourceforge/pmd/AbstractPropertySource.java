/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/*
 * Note: This class is here in pmd-build to test the RuntimeRulePropertiesAnalyzer
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractPropertySource {

    protected List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();
    protected Map<PropertyDescriptor, Object> propertyValuesByDescriptor = new HashMap<PropertyDescriptor, Object>();
    
    protected void defineProperty(final String name, final String description, final Object defaultValue) {
	net.sourceforge.pmd.PropertyDescriptor descriptor = new net.sourceforge.pmd.PropertyDescriptor() {
	    
	    @Override
	    public String name() {
		return name;
	    }
	    
	    @Override
	    public String description() {
		return description;
	    }

	    @Override
	    public Object defaultValue() {
	        return defaultValue;
	    }
	};
	propertyDescriptors.add(descriptor);
	propertyValuesByDescriptor.put(descriptor, defaultValue);
    }
}

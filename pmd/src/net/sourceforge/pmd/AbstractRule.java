/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

public abstract class AbstractRule extends JavaParserVisitorAdapter implements Rule {

    protected String name = getClass().getName();
    protected Properties properties = new Properties();		// TODO - remove when ready
    protected String message;
    protected String description;
    protected String example;
    protected String ruleSetName;
    protected boolean include;
    protected boolean usesDFA;
    protected boolean usesTypeResolution;
    protected int priority = LOWEST_PRIORITY;
    protected String externalInfoUrl;

    private static final boolean inOldPropertyMode = true;	// temporary flag during conversion
    
	protected static Map asFixedMap(PropertyDescriptor[] descriptors) {
		
		Map descsById = new HashMap(descriptors.length);
		
		for (int i=0; i<descriptors.length; i++) {
			descsById.put(descriptors[i].name(), descriptors[i]);
		}
		return Collections.unmodifiableMap(descsById);
	}	
	
	protected static Map asFixedMap(PropertyDescriptor descriptor) {
		return asFixedMap(new PropertyDescriptor[] {descriptor});
	}
    
    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    /**
     * @deprecated - property values will be guaranteed available via default values
     */
    public boolean hasProperty(String name) {
    	
        return inOldPropertyMode ?	// TODO -remove 
        	properties.containsKey(name) :
        	propertiesByName().containsKey(name);
    }
    
    /**
     * @deprecated 
     */
    public void addProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    /**
     * @deprecated 
     */
    public void addProperties(Properties properties) {
        this.properties.putAll(properties);
    }
    
    public double[] getDoubleProperties(PropertyDescriptor descriptor) {
    	
        Number[] values = (Number[])getProperties(descriptor);
        
        double[] doubles = new double[values.length];
        for (int i=0; i<doubles.length; i++) doubles[i] = values[i].doubleValue();
        return doubles;
    }
    
    /**
     * @deprecated - use getDoubleProperty(PropertyDescriptor) instead
     */
    public double getDoubleProperty(String name) {
    	
    	return Double.parseDouble(properties.getProperty(name));
    }

    public double getDoubleProperty(PropertyDescriptor descriptor) {
    	
    	return ((Number)getProperty(descriptor)).doubleValue();
    }
    
    public int[] getIntProperties(PropertyDescriptor descriptor) {
    	
        Number[] values = (Number[])getProperties(descriptor);
        
        int[] ints = new int[values.length];
        for (int i=0; i<ints.length; i++) ints[i] = values[i].intValue();
        return ints;
    }
    
    /**
     * @deprecated - use getIntProperty(PropertyDescriptor) instead
     */
    public int getIntProperty(String name) {
    	
    	return Integer.parseInt(properties.getProperty(name));
    }

    public int getIntProperty(PropertyDescriptor descriptor) {
    	
    	return ((Number)getProperty(descriptor)).intValue();
    }
    
    public Class[] getTypeProperties(PropertyDescriptor descriptor) {
    	
        return (Class[])getProperties(descriptor);
    }
    
    public Class getTypeProperty(PropertyDescriptor descriptor) {
    	
    	return (Class)getProperty(descriptor);
    }
    
    public boolean[] getBooleanProperties(PropertyDescriptor descriptor) {
    	
        Boolean[] values = (Boolean[])getProperties(descriptor);
        
        boolean[] bools = new boolean[values.length];
        for (int i=0; i<bools.length; i++) bools[i] = values[i].booleanValue();
        return bools;
    }
    
    public boolean getBooleanProperty(PropertyDescriptor descriptor) {
    	
    	return ((Boolean)getProperty(descriptor)).booleanValue();  
    }
    
    /**
     * @deprecated - use getBooleanProperty(PropertyDescriptor) instead
     */
    public boolean getBooleanProperty(String name) {
    	
    	return Boolean.valueOf(properties.getProperty(name)).booleanValue();        
    }
    
    /**
     * @deprecated - use setProperty(PropertyDescriptor, Object) instead
     * 
     * @param name
     * @param flag
     */
    public void setBooleanProperty(String name, boolean flag) {
    	
    	properties.setProperty(name, Boolean.toString(flag));
    }
    
    public String[] getStringProperties(PropertyDescriptor descriptor) {
    	
        return (String[])getProperties(descriptor);
    }
    

    /**
     * @deprecated - use getStringProperty(PropertyDescriptor) instead
     * 
     */
    public String getStringProperty(String name) {    	    	
    	return properties.getProperty(name);
    }
    
    public String getStringProperty(PropertyDescriptor descriptor) {
    	return (String)getProperty(descriptor);
    }
    
    private Object getProperty(PropertyDescriptor descriptor) {
    	    	
    	if (descriptor.maxValueCount() > 1) propertyGetError(descriptor, true);
    	
    	String rawValue = properties.getProperty(descriptor.name());
    	
        return rawValue == null || rawValue.length() == 0 ?
        	descriptor.defaultValue() :
        	descriptor.valueFrom(rawValue);
    }
    
    public void setProperty(PropertyDescriptor descriptor, Object value) {
    	    	
    	if (descriptor.maxValueCount() > 1) propertySetError(descriptor, true);
    	
    	properties.setProperty(descriptor.name(), descriptor.asDelimitedString(value));
    }
    
    private Object[] getProperties(PropertyDescriptor descriptor) {
    	    	
    	if (descriptor.maxValueCount() == 1) propertyGetError(descriptor, false);
    	
    	String rawValue = properties.getProperty(descriptor.name());
    	
        return rawValue == null || rawValue.length() == 0 ?
           	(Object[])descriptor.defaultValue() :
           	(Object[])descriptor.valueFrom(rawValue);
    }
    
    public void setProperties(PropertyDescriptor descriptor, Object[] values) {
    	    	
    	if (descriptor.maxValueCount() == 1) propertySetError(descriptor, false);
    	
    	properties.setProperty(descriptor.name(), descriptor.asDelimitedString(values));
    }
    
    private void propertyGetError(PropertyDescriptor descriptor, boolean requestedSingleValue) {
    	
    	if (requestedSingleValue) {
    		throw new RuntimeException("Cannot retrieve a single value from a multi-value property field");
    		}
    	throw new RuntimeException("Cannot retrieve multiple values from a single-value property field");
    }
    
    private void propertySetError(PropertyDescriptor descriptor, boolean setSingleValue) {
    	
    	if (setSingleValue) {
    		throw new RuntimeException("Cannot set a single value within a multi-value property field");
    		}
    	throw new RuntimeException("Cannot set multiple values within a single-value property field");
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExternalInfoUrl() {
        return externalInfoUrl;
    }

    public void setExternalInfoUrl(String url) {
        this.externalInfoUrl = url;
    }

    /**
     * Test if rules are equals. Rules are equals if
     * 1. they have the same implementation class
     * 2. they have the same name
     * 3. they have the same priority
     * 4. they share the same properties/values
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false; // trivial
        }

        if (this == o) {
            return true;  // trivial
        }

        Rule rule = null;
        boolean equality = this.getClass().getName().equals(o.getClass().getName());

        if (equality) {
            rule = (Rule) o;
            equality = this.getName().equals(rule.getName())
                    && this.getPriority() == rule.getPriority()
                    && this.getProperties().equals(rule.getProperties());
        }

        return equality;
    }

    /**
     * Return a hash code to conform to equality. Try with a string.
     */
    public int hashCode() {
        String s = getClass().getName() + getName() + getPriority() + getProperties().toString();
        return s.hashCode();
    }

    public void apply(List acus, RuleContext ctx) {
        visitAll(acus, ctx);
    }

    /**
     * @deprecated - retrieve by name using get<type>Property or get<type>Properties
     */
    public Properties getProperties() {
        return properties;
    }

    public boolean include() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }

    public int getPriority() {
        return priority;
    }

    public String getPriorityName() {
        return PRIORITIES[getPriority() - 1];
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setUsesDFA() {
        this.usesDFA = true;
    }

    public boolean usesDFA() {
        return this.usesDFA;
    }

    public void setUsesTypeResolution() {
        this.usesTypeResolution = true;
    }

    public boolean usesTypeResolution() {
        return this.usesTypeResolution;
    }

    protected void visitAll(List acus, RuleContext ctx) {
        for (Iterator i = acus.iterator(); i.hasNext();) {
            ASTCompilationUnit node = (ASTCompilationUnit) i.next();
            visit(node, ctx);
        }
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation
     */
    protected final void addViolation(Object data, SimpleNode node) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node));
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation
     * @param msg  specific message to put in the report
     */
    protected final void addViolationWithMessage(Object data, SimpleNode node, String msg) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node, msg));
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx   the RuleContext
     * @param node  the node that produces the violation
     * @param embed a variable to embed in the rule violation message
     */
    protected final void addViolation(Object data, SimpleNode node, String embed) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, node, MessageFormat.format(getMessage(), new Object[]{embed})));
    }

    /**
     * Adds a violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation, may be null, in which case all line and column info will be set to zero
     * @param args objects to embed in the rule violation message
     */
    protected final void addViolation(Object data, Node node, Object[] args) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new RuleViolation(this, ctx, (SimpleNode) node, MessageFormat.format(getMessage(), args)));
    }

    /**
     * Gets the Image of the first parent node of type ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node the node which will be searched
     */
    protected final String getDeclaringType(SimpleNode node) {
        ASTClassOrInterfaceDeclaration c = (ASTClassOrInterfaceDeclaration) node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (c != null)
            return c.getImage();
        return null;
    }
    
    public static boolean isQualifiedName(SimpleNode node) {
    	return node.getImage().indexOf('.') != -1;
    }
    
    public static boolean importsPackage(ASTCompilationUnit node, String packageName) {
    	
        List nodes = node.findChildrenOfType(ASTImportDeclaration.class);
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            ASTImportDeclaration n = (ASTImportDeclaration) i.next();
            if (n.getPackageName().startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return all the relevant properties for the receiver by
     * overriding in subclasses as necessary.
     * 
     * @return Map
     */
    protected Map propertiesByName() {
    	return Collections.EMPTY_MAP;
    }
    
    /**
     * Return the indicated property descriptor or null if not found.
     * 
     * @param propertyName String
     * @return PropertyDescriptor
     */
    public PropertyDescriptor propertyDescriptorFor(String propertyName) {
    	PropertyDescriptor desc = (PropertyDescriptor)propertiesByName().get(propertyName);
    	if (desc == null) throw new IllegalArgumentException("unknown property: " + propertyName);
    	return desc;
    }
}

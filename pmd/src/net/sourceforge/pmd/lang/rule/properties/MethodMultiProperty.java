/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Method;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property type that can specify multiple methods to use as part of a rule.
 *
 * Rule developers can limit the rules to those within designated packages per the
 * 'legalPackages' argument in the constructor which can be an array of partial
 * package names, i.e., ["java.lang", "com.mycompany" ].
 *
 * @author Brian Remedios
 */
public class MethodMultiProperty extends AbstractPackagedProperty<Method[]> {
    /**
     * Constructor for MethodProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param theDefaults    Method[]
     * @param legalPackageNames String[]
     * @param theUIOrder     float
     */
    public MethodMultiProperty(String theName, String theDescription, Method[] theDefaults, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

        multiValueDelimiter(' ');
    }

    /**
     * Return the value as a string that can be easily recognized and parsed
     * when we see it again.
     *
     * @param value Object
     * @return String
     */
    @Override
    protected String asString(Object value) {
        return value == null ? "" : MethodProperty.asStringFor((Method) value);
    }

    /**
     * Method packageNameOf.
     * @param item Object
     * @return String
     */
    @Override
    protected String packageNameOf(Object item) {

        final Method method = (Method) item;
        return method.getDeclaringClass().getName() + '.' + method.getName();
    }

    /**
     * Method itemTypeName.
     * @return String
     */
    @Override
    protected String itemTypeName() {
        return "method";
    }

	/**
	 * @return boolean
	 * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
	 */
	@Override
	public boolean isMultiValue() {
		return true;
	}
    
    /**
     *
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Method[]> type() {
        return Method[].class;
    }

    /**
     *
     * @param valueString  String
     * @return Object
     * @throws IllegalArgumentException
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Method[] valueFrom(String valueString) throws IllegalArgumentException {
        String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter());

        Method[] methods = new Method[values.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = MethodProperty.methodFrom(values[i], MethodProperty.CLASS_METHOD_DELIMITER, MethodProperty.METHOD_ARG_DELIMITER);
        }
        return methods;
    }
}

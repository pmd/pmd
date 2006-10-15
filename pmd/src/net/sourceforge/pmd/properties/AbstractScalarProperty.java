package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * No, subclasses are not necessarily scalar per se, they're just easy to parse without error.
 * If you can come up with a better name...
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public abstract class AbstractScalarProperty extends AbstractPMDProperty {

	/**
	 * Constructor for AbstractScalarProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault Object
	 * @param theUIOrder float
	 */
	public AbstractScalarProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}

	/**
	 * Method createFrom.
	 * @param value String
	 * @return Object
	 */
	protected abstract Object createFrom(String value);
	
	/**
	 * Method arrayFor.
	 * @param size int
	 * @return Object[]
	 */
	protected abstract Object[] arrayFor(int size);
	
	/**
	 * Method valueFrom.
	 * @param valueString String
	 * @return Object[]
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String valueString) throws IllegalArgumentException {
		
		if (maxValueCount() == 1) return createFrom(valueString);
		
		String[] strValues = StringUtil.substringsOf(valueString, multiValueDelimiter);
		
		Object[] values = arrayFor(strValues.length);
		for (int i=0; i<strValues.length; i++) values[i] = createFrom(strValues[i]);
		return values;
	}
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Enumeration;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype with a set of preset values of any type as held within a
 * pair of maps. While the values are not serialized out, the labels are and
 * serve as keys to obtain the values. The choices() method provides the ordered
 * selections to be used in an editor widget.
 * 
 * @author Brian Remedios
 * @param <E>
 */
public class EnumeratedMultiProperty<E> extends AbstractEnumeratedProperty<E, Object[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<EnumeratedMultiProperty>(
            Enumeration[].class) {

        public EnumeratedMultiProperty createWith(Map<String, String> valuesById) {

            return new EnumeratedMultiProperty(nameIn(valuesById), descriptionIn(valuesById), labelsIn(valuesById),
                    choicesIn(valuesById), indiciesIn(valuesById), 0f);
        }
    };

    /**
     * Constructor for EnumeratedProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theLabels String[]
     * @param theChoices E[]
     * @param choiceIndices int[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public EnumeratedMultiProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
            int[] choiceIndices, float theUIOrder) {
        super(theName, theDescription, theLabels, theChoices, choiceIndices, theUIOrder, true);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Object[]> type() {
        return Object[].class;
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
     * @param value Object
     * @return String
     * @see net.sourceforge.pmd.PropertyDescriptor#errorFor(Object)
     */
    @Override
    public String errorFor(Object value) {
        Object[] values = (Object[]) value;
        for (int i = 0; i < values.length; i++) {
            if (!labelsByChoice.containsKey(values[i])) {
                return nonLegalValueMsgFor(values[i]);
            }
        }
        return null;
    }

    /**
     * 
     * @param value String
     * @return Object
     * @throws IllegalArgumentException
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Object[] valueFrom(String value) throws IllegalArgumentException {
        String[] strValues = StringUtil.substringsOf(value, multiValueDelimiter());

        Object[] values = new Object[strValues.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = choiceFrom(strValues[i]);
        }
        return values;
    }

    /**
     * 
     * @param value Object
     * @return String
     * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
     */
    @Override
    public String asDelimitedString(Object[] value) {
        Object[] choices = value;

        StringBuilder sb = new StringBuilder();

        sb.append(labelsByChoice.get(choices[0]));

        for (int i = 1; i < choices.length; i++) {
            sb.append(multiValueDelimiter());
            sb.append(labelsByChoice.get(choices[i]));
        }

        return sb.toString();
    }
}
